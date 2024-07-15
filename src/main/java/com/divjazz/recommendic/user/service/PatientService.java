package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.credential.PatientCredential;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.credential.PatientCredentialRepository;
import com.divjazz.recommendic.utils.fileUpload.FileResponseMessage;
import com.google.common.collect.ImmutableSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.util.*;

@Service
@Transactional
public class PatientService {


    private final PatientRepository patientRepository;

    private final PatientCredentialRepository patientCredentialRepository;

    private final AppUserDetailsService userService;

    private final PasswordEncoder encoder;

    private final AlternativeJdkIdGenerator idGenerator;



    public PatientService(AlternativeJdkIdGenerator idGenerator,
                          PatientRepository patientRepository,
                          PatientCredentialRepository patientCredentialRepository, AppUserDetailsService userService,
                          PasswordEncoder encoder
                          ) {
        this.idGenerator = idGenerator;
        this.patientRepository = patientRepository;
        this.patientCredentialRepository = patientCredentialRepository;
        this.userService = userService;
        this.encoder = encoder;

    }




    public PatientInfoResponse createPatient(PatientDTO patientDTO){
        Patient user = new Patient(idGenerator.generateId(),
                patientDTO.userName(),
                patientDTO.email(),
                patientDTO.phoneNumber(),
                patientDTO.gender(),
                patientDTO.address());
        PatientCredential patientCredential = new PatientCredential(user,
                encoder.encode(patientDTO.password()),
                idGenerator.generateId());
        user.setCredential(patientCredential);

        if (!userService.isUserExists(user.getEmail())) {
            patientRepository.save(user);
            patientCredentialRepository.save(patientCredential);
            return new PatientInfoResponse(user.getId().toString()
                    ,user.getUserNameObject().getLastName()
                    ,user.getUserNameObject().getFirstName()
                    ,user.getPhoneNumber()
                    ,user.getGender().toString()
                    ,user.getAddress());
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }


    public Set<Patient> getAllPatients(){
        return ImmutableSet
                .copyOf(patientRepository.findAll());
    }

    public FileResponseMessage deletePatientById(String patient_Id){
        patientRepository.deleteById(UUID.fromString(patient_Id));
        return new FileResponseMessage("The deletion was successful");
    }

    public void modifyPatient(Patient patient){
        patientRepository.save(patient);
    }

    public Patient findPatientById(String id){
        return patientRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException(String.format("Patient with id %s was not found", id)));
    }

}
