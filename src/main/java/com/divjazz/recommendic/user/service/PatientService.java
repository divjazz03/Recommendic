package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import com.google.common.collect.ImmutableSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {


    private final PatientRepository patientRepository;

    private final AppUserDetailsService userService;

    private final PasswordEncoder encoder;



    public PatientService(
                          PatientRepository patientRepository,
                          AppUserDetailsService userService,
                          PasswordEncoder encoder
                          ) {

        this.patientRepository = patientRepository;
        this.userService = userService;
        this.encoder = encoder;

    }



    public ResponseMessage createPatient(PatientDTO patientDTO){
        Patient user = new Patient(UUID.randomUUID(),
                patientDTO.userName(),
                patientDTO.email(),
                patientDTO.phoneNumber(),
                patientDTO.gender(),
                patientDTO.address(),

                encoder.encode(patientDTO.password()));

        if (userService.isUserExists(user.getEmail())) {
            patientRepository.save(user);
            return new ResponseMessage(user.toString());
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }



    public Set<Patient> getAllPatients(){
        return ImmutableSet
                .copyOf(patientRepository.findAll());
    }

    public ResponseMessage deletePatientById(String patient_Id){
        patientRepository.deleteById(UUID.fromString(patient_Id));
        return new ResponseMessage("The deletion was successful");
    }

    public void modifyPatient(Patient patient){
        patientRepository.save(patient);
    }

    public Patient findPatientById(String id){
        return patientRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException(String.format("Patient with id %s was not found", id)));
    }

}
