package com.proto.BIS.common.Service;

import com.proto.BIS.Appointment.Model.AppointmentItemModel;
import com.proto.BIS.common.DTO.DashboardDetailsResponse;
import com.proto.BIS.common.DTO.DashboardSummaryResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.Billing.Model.BillItemModel;
import com.proto.BIS.Appointment.Model.AppointmentModel;
import com.proto.BIS.common.Model.ClientModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    @PersistenceContext
    private EntityManager em;

    private static final long LOW_STOCK_LIMIT = 5;
    private static final List<String> INACTIVE = List.of("CANCELLED", "NO_SHOW");

    public DashboardDetailsResponse getDetails() {
        log.info("In get dashboard details");
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(29);
        return new DashboardDetailsResponse(
                recentAppointments(),
                upcomingAppointments(today, LocalTime.now()),
                topServices(from),
                staffPerformance(from),
                inventoryAlerts());
    }

    private List<DashboardDetailsResponse.AppointmentRow> recentAppointments() {
        return em.createQuery(
                        "select a from AppointmentModel a order by a.appointmentDate desc, a.startTime desc",
                        AppointmentModel.class)
                .setMaxResults(5)
                .getResultList()
                .stream().map(this::toRow).toList();
    }

    private List<DashboardDetailsResponse.AppointmentRow> upcomingAppointments(LocalDate today, LocalTime now) {
        return em.createQuery(
                        "select a from AppointmentModel a where (a.appointmentDate > :today or (a.appointmentDate = :today and a.startTime >= :now)) and a.status not in :inactive order by a.appointmentDate, a.startTime",
                        AppointmentModel.class)
                .setParameter("today", today)
                .setParameter("now", now)
                .setParameter("inactive", INACTIVE)
                .setMaxResults(5)
                .getResultList()
                .stream().map(this::toRow).toList();
    }

    private DashboardDetailsResponse.AppointmentRow toRow(AppointmentModel a) {
        String services = a.getItems().stream()
                .map(AppointmentItemModel::getItemName)
                .collect(Collectors.joining(" + "));
        return new DashboardDetailsResponse.AppointmentRow(
                a.getAppointmentId(), a.getAppointmentDate(), a.getStartTime(),
                a.getClient().getClientName(), services, a.getStaff().getUserName(), a.getStatus());
    }

    private List<DashboardDetailsResponse.TopService> topServices(LocalDate from) {
        Long total = em.createQuery(
                        "select count(i) from AppointmentItemModel i where i.appointment.appointmentDate >= :from and i.appointment.status not in :inactive",
                        Long.class)
                .setParameter("from", from)
                .setParameter("inactive", INACTIVE)
                .getSingleResult();

        List<Object[]> rows = em.createQuery(
                        "select i.itemName, count(i) from AppointmentItemModel i where i.appointment.appointmentDate >= :from and i.appointment.status not in :inactive group by i.itemName order by count(i) desc",
                        Object[].class)
                .setParameter("from", from)
                .setParameter("inactive", INACTIVE)
                .setMaxResults(5)
                .getResultList();

        return rows.stream().map(r -> {
            long bookings = ((Number) r[1]).longValue();
            double share = total == 0 ? 0.0 : Math.round(bookings * 1000.0 / total) / 10.0;
            return new DashboardDetailsResponse.TopService((String) r[0], bookings, share);
        }).toList();
    }

    private List<DashboardDetailsResponse.StaffPerformance> staffPerformance(LocalDate from) {
        Map<Integer, DashboardDetailsResponse.StaffPerformance> byStaff = new HashMap<>();

        List<Object[]> appointmentRows = em.createQuery(
                        "select a.staff.userId, a.staff.userName, count(a) from AppointmentModel a where a.appointmentDate >= :from and a.status not in :inactive group by a.staff.userId, a.staff.userName",
                        Object[].class)
                .setParameter("from", from)
                .setParameter("inactive", INACTIVE)
                .getResultList();
        for (Object[] r : appointmentRows) {
            byStaff.put(((Number) r[0]).intValue(),
                    new DashboardDetailsResponse.StaffPerformance((String) r[1], ((Number) r[2]).longValue(), 0.0));
        }

        List<Object[]> revenueRows = em.createQuery(
                        "select b.staff.userId, b.staff.userName, sum(b.billAmount) from Bills b where b.billDate >= :from group by b.staff.userId, b.staff.userName",
                        Object[].class)
                .setParameter("from", from.atStartOfDay())
                .getResultList();
        for (Object[] r : revenueRows) {
            String name = (String) r[1];
            DashboardDetailsResponse.StaffPerformance row = byStaff.computeIfAbsent(
                    ((Number) r[0]).intValue(),
                    id -> new DashboardDetailsResponse.StaffPerformance(name, 0L, 0.0));
            row.setRevenue(r[2] == null ? 0.0 : ((Number) r[2]).doubleValue());
        }

        return byStaff.values().stream()
                .sorted(Comparator.comparingDouble(DashboardDetailsResponse.StaffPerformance::getRevenue).reversed())
                .limit(5)
                .toList();
    }

    private List<DashboardDetailsResponse.InventoryAlert> inventoryAlerts() {
        return em.createQuery(
                        "select p.productName, p.productQuantity from ProductModel p order by p.productQuantity asc",
                        Object[].class)
                .setMaxResults(5)
                .getResultList()
                .stream().map(r -> {
                    long stock = ((Number) r[1]).longValue();
                    String status = stock <= 0 ? "OUT_OF_STOCK" : stock <= LOW_STOCK_LIMIT ? "LOW" : "OK";
                    return new DashboardDetailsResponse.InventoryAlert((String) r[0], stock, status);
                }).toList();
    }

    public DashboardSummaryResponse getSummary() {
        log.info("In get dashboard summary");
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate prevWeekStart = today.minusDays(13);
        LocalDate tomorrow = today.plusDays(1);

        LocalDateTime weekStartTime = weekStart.atStartOfDay();
        LocalDateTime prevWeekStartTime = prevWeekStart.atStartOfDay();
        LocalDateTime tomorrowTime = tomorrow.atStartOfDay();

        long appointments = countAppointments(weekStart, tomorrow);
        long prevAppointments = countAppointments(prevWeekStart, weekStart);

        double revenue = revenue(weekStartTime, tomorrowTime);
        double prevRevenue = revenue(prevWeekStartTime, weekStartTime);

        long sold = productsSold(weekStartTime, tomorrowTime);
        long prevSold = productsSold(prevWeekStartTime, weekStartTime);

        long clients = em.createQuery("select count(c) from ClientModel c", Long.class).getSingleResult();
        long clientsMonthAgo = em.createQuery(
                        "select count(c) from ClientModel c where c.createdAt < :t", Long.class)
                .setParameter("t", LocalDateTime.now().minusMonths(1))
                .getSingleResult();

        return new DashboardSummaryResponse(
                appointments, pct(appointments, prevAppointments),
                clients, pct(clients, clientsMonthAgo),
                revenue, pct(revenue, prevRevenue),
                sold, pct(sold, prevSold),
                overview(today));
    }

    private long countAppointments(LocalDate from, LocalDate toExclusive) {
        return em.createQuery(
                        "select count(a) from AppointmentModel a where a.appointmentDate >= :from and a.appointmentDate < :to",
                        Long.class)
                .setParameter("from", from)
                .setParameter("to", toExclusive)
                .getSingleResult();
    }

    private double revenue(LocalDateTime from, LocalDateTime toExclusive) {
        Double value = em.createQuery(
                        "select sum(b.billAmount) from Bills b where b.billDate >= :from and b.billDate < :to",
                        Double.class)
                .setParameter("from", from)
                .setParameter("to", toExclusive)
                .getSingleResult();
        return value == null ? 0.0 : value;
    }

    private long productsSold(LocalDateTime from, LocalDateTime toExclusive) {
        Long value = em.createQuery(
                        "select sum(i.quantity) from BillItemModel i where i.product is not null and i.bill.billDate >= :from and i.bill.billDate < :to",
                        Long.class)
                .setParameter("from", from)
                .setParameter("to", toExclusive)
                .getSingleResult();
        return value == null ? 0L : value;
    }

    private List<DashboardSummaryResponse.DailyCount> overview(LocalDate today) {
        List<Object[]> rows = em.createQuery(
                        "select a.appointmentDate, count(a) from AppointmentModel a where a.appointmentDate >= :from and a.appointmentDate < :to group by a.appointmentDate",
                        Object[].class)
                .setParameter("from", today.minusDays(6))
                .setParameter("to", today.plusDays(1))
                .getResultList();

        Map<LocalDate, Long> counts = new HashMap<>();
        for (Object[] row : rows) {
            counts.put((LocalDate) row[0], (Long) row[1]);
        }

        List<DashboardSummaryResponse.DailyCount> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            result.add(new DashboardSummaryResponse.DailyCount(day, counts.getOrDefault(day, 0L)));
        }
        return result;
    }

    private Double pct(double current, double previous) {
        if (previous == 0) {
            return null;
        }
        return Math.round((current - previous) / previous * 1000.0) / 10.0;
    }
}