package com.proto.BIS.Appointment.Service;

import com.proto.BIS.Appointment.DTO.AppointmentRequest;
import com.proto.BIS.Appointment.Model.AppointmentModel;
import com.proto.BIS.Appointment.Repository.AppointmentRepository;
import com.proto.BIS.Package.Repository.PackageRepository;
import com.proto.BIS.common.Model.ClientModel;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.ClientRepo;
import com.proto.BIS.common.Repository.UserRepo;
import com.proto.BIS.common.Repository.WorkRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    @Mock AppointmentRepository appointmentRepo;
    @Mock ClientRepo clientRepo;
    @Mock UserRepo userRepo;
    @Mock WorkRepo workRepo;
    @Mock PackageRepository packageRepo;
    @InjectMocks AppointmentService service;

    @Test
    void createsAppointmentWithServiceAndCalculatesTotal() {
        AppointmentRequest request = request();
        ServicesModel serviceModel = ServicesModel.builder().serviceId(7L).serviceName("Cut").servicePrice(25).build();
        ClientModel client = new ClientModel();
        client.setClientId(2);
        client.setClientName("Alice");
        client.setClientPhone("1234567890");
        UserModel staff = new UserModel();
        staff.setUserId(3);
        staff.setUserName("Bob");
        when(appointmentRepo.hasOverlap(anyInt(), any(), any(), any(), isNull())).thenReturn(false);
        when(clientRepo.findById(2L)).thenReturn(Optional.of(client));
        when(userRepo.findById(3)).thenReturn(Optional.of(staff));
        when(workRepo.findById(7)).thenReturn(Optional.of(serviceModel));
        when(appointmentRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request);

        assertEquals(25, response.getTotalAmount());
        assertEquals("PENDING", response.getStatus());
        assertEquals("SERVICE", response.getItems().get(0).getType());
    }

    @Test
    void rejectsInvalidTimesOverlapAndMissingClient() {
        AppointmentRequest request = request();
        request.setEndTime(request.getStartTime());
        assertThrows(IllegalArgumentException.class, () -> service.create(request));

        request.setEndTime(LocalTime.of(11, 0));
        when(appointmentRepo.hasOverlap(anyInt(), any(), any(), any(), isNull())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.create(request));

        when(appointmentRepo.hasOverlap(anyInt(), any(), any(), any(), isNull())).thenReturn(false);
        when(clientRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.create(request));
    }

    private AppointmentRequest request() {
        AppointmentRequest request = new AppointmentRequest();
        request.setClientId(2L);
        request.setStaffId(3);
        request.setAppointmentDate(LocalDate.of(2026, 10, 20));
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(11, 0));
        AppointmentRequest.ItemRequest item = new AppointmentRequest.ItemRequest();
        item.setServiceId(7L);
        request.setItems(List.of(item));
        return request;
    }
}