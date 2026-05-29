package com.kinedical.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kinedical.model.Appointment;
import com.kinedical.repository.AppointmentRepository;
import com.kinedical.security.AppUserDetails;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAll(Authentication authentication) {
        String userId = resolveUserId(authentication);
        String role = resolveUserRole(authentication);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if ("DOCTOR".equalsIgnoreCase(role)) {
            return ResponseEntity.ok(appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(userId));
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.ok(appointmentRepository.findByOrderByAppointmentDateDesc());
        } else {
            // Default to PATIENT
            return ResponseEntity.ok(appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(userId));
        }
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody AppointmentRequest request, Authentication authentication) {
        String patientId = resolveUserId(authentication);
        if (patientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (request.getDoctorId() == null || request.getAppointmentDate() == null) {
            return ResponseEntity.badRequest().build();
        }

        Appointment appointment = new Appointment();
        appointment.setPatientId(patientId);
        appointment.setDoctorId(request.getDoctorId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStatus(Appointment.Status.PENDING);
        appointment.setNotes(request.getNotes());
        appointment.setCreatedAt(Instant.now());

        Appointment saved = appointmentRepository.save(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Appointment> updateStatus(@PathVariable("id") String id, @RequestBody StatusRequest request, Authentication authentication) {
        String userId = resolveUserId(authentication);
        String role = resolveUserRole(authentication);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Appointment> optional = appointmentRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Appointment appointment = optional.get();

        // Enforce permissions:
        // Patients can only cancel their own appointments
        if ("PATIENT".equalsIgnoreCase(role)) {
            if (!userId.equals(appointment.getPatientId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (request.getStatus() != Appointment.Status.CANCELED) {
                return ResponseEntity.badRequest().build(); // patients can only cancel
            }
        }
        // Doctors can only manage appointments assigned to them
        else if ("DOCTOR".equalsIgnoreCase(role)) {
            if (!userId.equals(appointment.getDoctorId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        appointment.setStatus(request.getStatus());
        Appointment saved = appointmentRepository.save(appointment);
        return ResponseEntity.ok(saved);
    }

    private String resolveUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails details) {
            return details.getUserId();
        }
        return null;
    }

    private String resolveUserRole(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AppUserDetails details) {
            return details.getRole().name();
        }
        return "PATIENT";
    }

    // Requests DTOs
    public static class AppointmentRequest {
        private String doctorId;
        private Instant appointmentDate;
        private String notes;

        public String getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(String doctorId) {
            this.doctorId = doctorId;
        }

        public Instant getAppointmentDate() {
            return appointmentDate;
        }

        public void setAppointmentDate(Instant appointmentDate) {
            this.appointmentDate = appointmentDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class StatusRequest {
        private Appointment.Status status;

        public Appointment.Status getStatus() {
            return status;
        }

        public void setStatus(Appointment.Status status) {
            this.status = status;
        }
    }
}
