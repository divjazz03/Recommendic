package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.UserRepositoryCustom;
import com.google.common.collect.ImmutableSet;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PatientService {

    private final UserRepositoryCustom userRepositoryCustom;

    private final AppUserDetailsService service;
    private final PasswordEncoder encoder;

    public PatientService( UserRepositoryCustom userRepositoryCustom, AppUserDetailsService service, PasswordEncoder encoder) {
        this.userRepositoryCustom = userRepositoryCustom;
        this.service = service;
        this.encoder = encoder;
    }

    public ResponseEntity<User> createPatient(PatientDTO patientDTO){
        User user = new User(userRepositoryCustom.nextId(),
                patientDTO.userName(),
                patientDTO.email(),
                patientDTO.phoneNumber(), patientDTO.gender(), patientDTO.address(), UserType.PATIENT, encoder.encode(patientDTO.password()));

       return new ResponseEntity<>(userRepositoryCustom.save(user), HttpStatus.CREATED);
    }



    public ResponseEntity<Set<User>> getAllPatients(){
        UserType patient = UserType.PATIENT;
        Set<User> patients = ImmutableSet
                .copyOf(userRepositoryCustom
                .findAllByUserType(patient).orElseThrow(() -> new UsernameNotFoundException("No patients found")));
        return new ResponseEntity<>(patients,HttpStatus.OK);
    }

}
