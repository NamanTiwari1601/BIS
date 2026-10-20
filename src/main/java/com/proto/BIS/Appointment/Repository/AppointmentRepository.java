package com.proto.BIS.Appointment.Repository;

import com.proto.BIS.Appointment.Model.AppointmentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AppointmentRepository extends JpaRepository<AppointmentModel, Long> {
    @Query(value = """
            SELECT a.*
            FROM s_appointment a
            JOIN s_client c ON c.client_id = a.client_id
            WHERE (CAST(:fromDate AS date) IS NULL OR a.appointment_date >= CAST(:fromDate AS date))
              AND (CAST(:toDate AS date) IS NULL OR a.appointment_date <= CAST(:toDate AS date))
              AND (CAST(:staffId AS integer) IS NULL OR a.staff_id = CAST(:staffId AS integer))
              AND (CAST(:status AS text) IS NULL OR a.status = CAST(:status AS text))
              AND (CAST(:q AS text) IS NULL
                   OR LOWER(c.client_name) LIKE LOWER('%' || CAST(:q AS text) || '%')
                   OR c.client_phone LIKE '%' || CAST(:q AS text) || '%')
            ORDER BY a.appointment_date, a.start_time
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM s_appointment a
            JOIN s_client c ON c.client_id = a.client_id
            WHERE (CAST(:fromDate AS date) IS NULL OR a.appointment_date >= CAST(:fromDate AS date))
              AND (CAST(:toDate AS date) IS NULL OR a.appointment_date <= CAST(:toDate AS date))
              AND (CAST(:staffId AS integer) IS NULL OR a.staff_id = CAST(:staffId AS integer))
              AND (CAST(:status AS text) IS NULL OR a.status = CAST(:status AS text))
              AND (CAST(:q AS text) IS NULL
                   OR LOWER(c.client_name) LIKE LOWER('%' || CAST(:q AS text) || '%')
                   OR c.client_phone LIKE '%' || CAST(:q AS text) || '%')
            """,
            nativeQuery = true)
    Page<AppointmentModel> search(@Param("fromDate") LocalDate from, @Param("toDate") LocalDate to, @Param("staffId") Integer staffId, @Param("status") String status, @Param("q") String q, Pageable pageable);

    @Query("select count(a) > 0 from AppointmentModel a where a.staff.userId = :staffId and a.appointmentDate = :date and a.status not in ('CANCELLED', 'NO_SHOW') and a.startTime < :endTime and a.endTime > :startTime and (:id is null or a.appointmentId <> :id)")
    boolean hasOverlap(@Param("staffId") Integer staffId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime, @Param("id") Long id);

    long countByStatus(String status);
    long countByAppointmentDate(LocalDate date);
}