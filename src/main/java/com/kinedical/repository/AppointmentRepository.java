package com.kinedical.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.kinedical.model.Appointment;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(String patientId);
    List<Appointment> findByDoctorIdOrderByAppointmentDateDesc(String doctorId);
    List<Appointment> findByOrderByAppointmentDateDesc();
}
