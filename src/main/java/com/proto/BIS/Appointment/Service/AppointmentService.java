package com.proto.BIS.Appointment.Service;

import com.proto.BIS.Appointment.DTO.AppointmentRequest;
import com.proto.BIS.Appointment.DTO.AppointmentResponse;
import com.proto.BIS.Appointment.DTO.AppointmentStatsResponse;
import com.proto.BIS.Appointment.Model.AppointmentItemModel;
import com.proto.BIS.Appointment.Model.AppointmentModel;
import com.proto.BIS.Appointment.Repository.AppointmentRepository;
import com.proto.BIS.Package.Model.PackageModel;
import com.proto.BIS.Package.Repository.PackageRepository;
import com.proto.BIS.common.Model.ClientModel;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.ClientRepo;
import com.proto.BIS.common.Repository.UserRepo;
import com.proto.BIS.common.Repository.WorkRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ClientRepo clientRepository;
    private final UserRepo userRepository;
    private final WorkRepo serviceRepository;
    private final PackageRepository packageRepository;

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> search(java.time.LocalDate from, java.time.LocalDate to, Integer staffId, String status, String q, Pageable pageable) {
        return appointmentRepository.search(from, to, staffId, blankToNull(status), blankToNull(q), pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse get(Long id) { return toResponse(find(id)); }

    public AppointmentResponse create(AppointmentRequest request) { return save(new AppointmentModel(), request); }
    public AppointmentResponse update(Long id, AppointmentRequest request) { return save(find(id), request); }

    public AppointmentResponse updateStatus(Long id, String status) {
        AppointmentModel appointment = find(id);
        appointment.setStatus(status.toUpperCase());
        return toResponse(appointment);
    }

    public void delete(Long id) { appointmentRepository.delete(find(id)); }

    @Transactional(readOnly = true)
    public AppointmentStatsResponse stats() {
        return AppointmentStatsResponse.builder().total(appointmentRepository.count())
                .confirmed(appointmentRepository.countByStatus("CONFIRMED"))
                .pending(appointmentRepository.countByStatus("PENDING"))
                .cancelled(appointmentRepository.countByStatus("CANCELLED")).build();
    }

    private AppointmentResponse save(AppointmentModel appointment, AppointmentRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) throw new IllegalArgumentException("End time must be after start time");
        if (appointmentRepository.hasOverlap(request.getStaffId(), request.getAppointmentDate(), request.getStartTime(), request.getEndTime(), appointment.getAppointmentId())) {
            throw new IllegalArgumentException("Staff member already has an appointment during this time");
        }
        ClientModel client = clientRepository.findById(request.getClientId()).orElseThrow(() -> new EntityNotFoundException("Client not found: " + request.getClientId()));
        UserModel staff = userRepository.findById(request.getStaffId()).orElseThrow(() -> new EntityNotFoundException("Staff not found: " + request.getStaffId()));
        appointment.setClient(client); appointment.setStaff(staff); appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime()); appointment.setEndTime(request.getEndTime());
        appointment.setStatus(request.getStatus() == null ? "PENDING" : request.getStatus().toUpperCase()); appointment.setNotes(request.getNotes());
        appointment.getItems().clear(); double total = 0;
        for (AppointmentRequest.ItemRequest item : request.getItems()) {
            if ((item.getServiceId() == null) == (item.getPackageId() == null)) throw new IllegalArgumentException("Each appointment item must reference exactly one service or package");
            AppointmentItemModel appointmentItem = AppointmentItemModel.builder().appointment(appointment).build();
            if (item.getServiceId() != null) {
                ServicesModel service = serviceRepository.findById(item.getServiceId().intValue()).orElseThrow(() -> new EntityNotFoundException("Service not found: " + item.getServiceId()));
                appointmentItem.setService(service); appointmentItem.setItemName(service.getServiceName()); appointmentItem.setPrice(service.getServicePrice()); appointmentItem.setDurationMinutes(0);
            } else {
                PackageModel pack = packageRepository.findById(item.getPackageId()).orElseThrow(() -> new EntityNotFoundException("Package not found: " + item.getPackageId()));
                appointmentItem.setPackageModel(pack); appointmentItem.setItemName(pack.getPackageName()); appointmentItem.setPrice(pack.getPackagePrice()); appointmentItem.setDurationMinutes(pack.getDurationMinutes());
            }
            total += appointmentItem.getPrice(); appointment.getItems().add(appointmentItem);
        }
        appointment.setTotalAmount(total);
        return toResponse(appointmentRepository.save(appointment));
    }

    private AppointmentModel find(Long id) { return appointmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Appointment not found: " + id)); }

    private AppointmentResponse toResponse(AppointmentModel a) {
        return AppointmentResponse.builder().appointmentId(a.getAppointmentId())
                .client(AppointmentResponse.ClientResponse.builder().clientId(a.getClient().getClientId().longValue()).clientName(a.getClient().getClientName()).clientPhone(a.getClient().getClientPhone()).build())
                .staff(AppointmentResponse.StaffResponse.builder().staffId(a.getStaff().getUserId()).staffName(a.getStaff().getUserName()).build())
                .appointmentDate(a.getAppointmentDate()).startTime(a.getStartTime()).endTime(a.getEndTime()).status(a.getStatus()).notes(a.getNotes()).totalAmount(a.getTotalAmount())
                .items(a.getItems().stream().map(i -> AppointmentResponse.ItemResponse.builder().id(i.getService() != null ? i.getService().getServiceId() : i.getPackageModel().getPackageId()).type(i.getService() != null ? "SERVICE" : "PACKAGE").name(i.getItemName()).price(i.getPrice()).durationMinutes(i.getDurationMinutes()).build()).toList()).build();
    }

    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value; }
}