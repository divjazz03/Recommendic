package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.EntityIdentityConfig;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.repository.PatientRepository;
import io.github.wimdeblauwe.jpearl.UniqueIdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UniqueIdGenerator<UUID> uniqueIdGenerator;

    public PatientService(PatientRepository patientRepository, UniqueIdGenerator<UUID> uniqueIdGenerator) {
        this.patientRepository = patientRepository;
        this.uniqueIdGenerator = uniqueIdGenerator;

    }

    public ResponseEntity<Patient> createPatient(PatientDTO patientDTO){
        Patient patient = new Patient(new UserId(uniqueIdGenerator.getNextUniqueId()),
                patientDTO.userName(),
                patientDTO.email(),
                patientDTO.phoneNumber(),
                patientDTO.gender());
        var result = patientRepository.save(patient);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
