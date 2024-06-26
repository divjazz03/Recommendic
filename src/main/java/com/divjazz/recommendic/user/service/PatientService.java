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

    private final UserRepository userRepository;
    private final UserIdRepository userIdRepository;

    private final PatientRepository patientRepository;

    private final GeneralUserService userService;

    private final PasswordEncoder encoder;



    public PatientService(UserRepository userRepositoryCustom,
                          UserIdRepository userIdRepository,
                          PatientRepository patientRepository,
                          GeneralUserService userService,
                          PasswordEncoder encoder
                          ) {
        this.userRepository = userRepositoryCustom;
        this.userIdRepository = userIdRepository;
        this.patientRepository = patientRepository;
        this.userService = userService;
        this.encoder = encoder;

    }



    public ResponseMessage createPatient(PatientDTO patientDTO){
        User user = new User(userIdRepository.nextId(),
                patientDTO.userName(),
                patientDTO.email(),
                patientDTO.phoneNumber(),
                patientDTO.gender(),
                patientDTO.address(),
                UserType.PATIENT,
                encoder.encode(patientDTO.password()));

        if (userService.verifyIfEmailNotExists(user.getEmail())) {
            userRepository.save(user);
            UserId patientID = userIdRepository.nextId();
            Patient patient = new Patient(patientID, user,Arrays.stream(patientDTO.categoryOfInterest())
                    .map(MedicalCategory::valueOf)
                    .collect(Collectors.toSet()));
            patientRepository.save(patient);
            return new ResponseMessage(user.toString() + " Patient_id + " + patientID.toString());
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }



    public Set<Patient> getAllPatients(){
        UserType patient = UserType.PATIENT;
        Set<User> patients = ImmutableSet
                .copyOf(userRepository
                .findAllByUserType(patient).orElseThrow(() -> new UsernameNotFoundException("No patients found")));
        return patients.stream()
                .map(user -> patientRepository
                        .findByUser(user)
                        .orElseThrow(() -> new UserNotFoundException("Patient was not found")))
                .collect(Collectors.toSet());
    }

    public ResponseMessage deletePatientById(String patient_Id_String){
        UserId patient_Id = new UserId(UUID.fromString(patient_Id_String));
        patientRepository.deleteById(patient_Id);
        return new ResponseMessage("The deletion was successful");
    }

    public void modifyPatient(Patient patient){
        patientRepository.save(patient);
    }

    public Patient findPatientById(String id){
        return patientRepository.findById(new UserId(UUID.fromString(id)))
                .orElseThrow(() -> new UserNotFoundException(String.format("Patient with id %s was not found", id)));
    }

}
