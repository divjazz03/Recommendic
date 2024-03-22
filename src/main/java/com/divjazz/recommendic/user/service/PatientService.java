package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.UserRepositoryCustom;
import com.divjazz.recommendic.user.repository.UserRepositoryImpl;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final UserRepositoryCustom userRepositoryCustom;
    private final UserRepositoryImpl userRepository;

    private final PatientRepository patientRepository;

    private final GeneralUserService userService;

    public PatientService(UserRepositoryCustom userRepositoryCustom, UserRepositoryImpl userRepository, PatientRepository patientRepository, GeneralUserService userService, AppUserDetailsService service, PasswordEncoder encoder) {
        this.userRepositoryCustom = userRepositoryCustom;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.userService = userService;
        this.service = service;
        this.encoder = encoder;
    }

    private final AppUserDetailsService service;
    private final PasswordEncoder encoder;



    public ResponseEntity<ResponseMessage> createPatient(PatientDTO patientDTO){
        User user = new User(userRepository.nextId(),
                patientDTO.userName(),
                patientDTO.email(),
                patientDTO.phoneNumber(), patientDTO.gender(), patientDTO.address(), UserType.PATIENT, encoder.encode(patientDTO.password()));

        if (!userService.verifyIfEmailExists(user.getEmail())) {
            userRepositoryCustom.save(user);
            Patient patient = new Patient(userRepository.nextId(), user);
            patientRepository.save(patient);
            return new ResponseEntity<>(new ResponseMessage(user.toString()), HttpStatus.CREATED);
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }



    public ResponseEntity<Set<Patient>> getAllPatients(){
        UserType patient = UserType.PATIENT;
        Set<User> patients = ImmutableSet
                .copyOf(userRepositoryCustom
                .findAllByUserType(patient).orElseThrow(() -> new UsernameNotFoundException("No patients found")));
        return new ResponseEntity<>(patients.stream()
                .map(user -> patientRepository
                        .findByUser(user)
                        .orElseThrow(() -> new UserNotFoundException("Patient was not found")))
                .collect(Collectors.toSet()),HttpStatus.OK);
    }

}
