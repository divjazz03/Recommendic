package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConsultantService {


    private final ConsultantRepository consultantRepository;

    private final PasswordEncoder passwordEncoder;

    private final AppUserDetailsService userService;

    public ConsultantService(
                             ConsultantRepository consultantRepository,
                             PasswordEncoder passwordEncoder,
                             AppUserDetailsService userService) {
        this.consultantRepository = consultantRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public ResponseEntity<ResponseMessage> createConsultant(ConsultantDTO consultantDTO) {
        Consultant user = new Consultant(
                UUID.randomUUID(),
                consultantDTO.userName(),
                consultantDTO.email(),
                consultantDTO.phoneNumber(),
                consultantDTO.gender(),
                consultantDTO.address(),
                passwordEncoder.encode(consultantDTO.password()),
                consultantDTO.medicalCategory()
        );
        if (!userService.isUserExists(user.getEmail())) {
            consultantRepository.save(user);
            return new ResponseEntity<>(new ResponseMessage(user.toString()), HttpStatus.CREATED);
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }

    public ResponseEntity<Set<Consultant>> getAllConsultants(){
        Set<Consultant> consultants = ImmutableSet.
                copyOf(consultantRepository.findAll());
        return new ResponseEntity<>(consultants, HttpStatus.OK);
    }

    public Set<Consultant> getConsultantByCategory(MedicalCategory category){
        return ImmutableSet.
                copyOf(
                        consultantRepository.findByMedicalCategory(category)
                                .orElseThrow(() -> new UserNotFoundException("Consultant with that Category does not exist"))
                );
    }

    public Set<Consultant> getConsultantsByName(String name){

        return Set.copyOf(consultantRepository.findAll()
                .stream().filter(user -> user.getUsername().contains(name))
                .collect(Collectors.toSet()));
    }
}
