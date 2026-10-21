package com.proto.BIS.Appointment.Controller;

import com.proto.BIS.Appointment.DTO.AppointmentRequest;
import com.proto.BIS.Appointment.DTO.AppointmentResponse;
import com.proto.BIS.Appointment.DTO.AppointmentStatsResponse;
import com.proto.BIS.Appointment.Service.AppointmentService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
@Tag(name = "Appointments", description = "Schedule and manage client appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping
    @Operation(
            summary = "Search appointments",
            description = "Returns a paginated list of appointments filtered by date range, staff member, status, or client search text.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointments returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date, pagination, or filter parameters", content = @Content)
    })
    public Page<AppointmentResponse> getAppointments(@RequestParam(required = false) LocalDate from,
                                                     @RequestParam(required = false) LocalDate to,
                                                     @RequestParam(required = false) Integer staffId,
                                                     @RequestParam(required = false) String status,
                                                     @RequestParam(required = false) String q,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return appointmentService.search(from, to, staffId, status, q, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an appointment", description = "Returns an appointment with its client, staff, and appointment items.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment found"),
            @ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content)
    })
    public AppointmentResponse get(@PathVariable Long id) {
        return appointmentService.get(id);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get appointment statistics", description = "Returns appointment counts and totals used by the appointments dashboard.")
    @ApiResponse(responseCode = "200", description = "Statistics returned successfully", content = @Content(schema = @Schema(implementation = AppointmentStatsResponse.class)))
    public AppointmentStatsResponse stats() {
        return appointmentService.stats();
    }

    @PostMapping
    @Operation(summary = "Create an appointment", description = "Creates an appointment, calculates the total amount, validates references, and rejects overlapping appointments for the same staff member.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Appointment created", content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid appointment data or overlapping staff schedule", content = @Content),
            @ApiResponse(responseCode = "404", description = "Client, staff, service, or package not found", content = @Content)
    })
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an appointment", description = "Replaces an appointment and its items after applying reference and staff-overlap validation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment updated", content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid appointment data or overlapping staff schedule", content = @Content),
            @ApiResponse(responseCode = "404", description = "Appointment or referenced entity not found", content = @Content)
    })
    public AppointmentResponse update(@PathVariable Long id, @Valid @RequestBody AppointmentRequest request) {
        return appointmentService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status", description = "Changes the status of an existing appointment.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Appointment status updated", content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content)
    })
    public AppointmentResponse updateStatus(@PathVariable Long id, @RequestParam String status) {
        return appointmentService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an appointment", description = "Deletes an appointment and its appointment items.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Appointment deleted"),
            @ApiResponse(responseCode = "404", description = "Appointment not found", content = @Content)
    })
    public void delete(@PathVariable Long id) {
        appointmentService.delete(id);
    }
}