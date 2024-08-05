package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import com.divjazz.recommendic.utils.fileUpload.FileResponseMessage;
import com.google.common.collect.ImmutableSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PatientService {


    private final PatientRepository patientRepository;

    private final UserCredentialRepository userCredentialRepository;

    private final GeneralUserService userService;

    private final PasswordEncoder encoder;




    public PatientService(
            PatientRepository patientRepository,
            UserCredentialRepository userCredentialRepository,
            GeneralUserService userService,
            PasswordEncoder encoder
                          ) {
        this.patientRepository = patientRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.userService = userService;
        this.encoder = encoder;
    }




    public PatientInfoResponse createPatient(PatientDTO patientDTO){
        Role role = new Role();
        UserCredential userCredential = new UserCredential(encoder.encode(patientDTO.password()));
        Patient user = new Patient(
                patientDTO.userName(),
                patientDTO.email(),
                patientDTO.phoneNumber(),
                patientDTO.gender(),
                patientDTO.address(),
                role,
                userCredential);

        user.setUserCredential(userCredential);
        userCredential.setUser(user);
        if (userService.isUserNotExists(user.getEmail())) {
            patientRepository.save(user);
            userCredentialRepository.save(userCredential);
            return new PatientInfoResponse(user.getId()
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

    public void deletePatientById(long patient_Id){
        patientRepository.deleteById(patient_Id);
        new FileResponseMessage("The deletion was successful");
    }

    public void modifyPatient(Patient patient){
        patientRepository.save(patient);
    }

    public Patient findPatientById(long id){
        return patientRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Patient with id %s was not found", id)));
    }

}
