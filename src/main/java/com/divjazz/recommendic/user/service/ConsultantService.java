package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.credential.ConsultantCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.credential.ConsultantCredentialRepository;
import com.divjazz.recommendic.utils.fileUpload.FileResponseMessage;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsultantService {


    private final ConsultantRepository consultantRepository;
    private final ConsultantCredentialRepository consultantCredentialRepository;

    private final PasswordEncoder passwordEncoder;

    private final AppUserDetailsService userService;
    private final AlternativeJdkIdGenerator idGenerator;

    public ConsultantService(
            ConsultantRepository consultantRepository,
            ConsultantCredentialRepository consultantCredentialRepository, PasswordEncoder passwordEncoder,
            AppUserDetailsService userService, AlternativeJdkIdGenerator idGenerator) {
        this.consultantRepository = consultantRepository;
        this.consultantCredentialRepository = consultantCredentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.idGenerator = idGenerator;
    }

    public ConsultantInfoResponse createConsultant(ConsultantDTO consultantDTO) {
        Consultant user = new Consultant(
                idGenerator.generateId(),
                consultantDTO.userName(),
                consultantDTO.email(),
                consultantDTO.phoneNumber(),
                consultantDTO.gender(),
                consultantDTO.address(),
                consultantDTO.medicalCategory()
        );

        ConsultantCredential consultantCredential = new ConsultantCredential(user,
                passwordEncoder.encode(consultantDTO.password()),
                idGenerator.generateId());
        user.setCredential(consultantCredential);

        if (!userService.isUserExists(user.getEmail())) {
            consultantRepository.save(user);
            consultantCredentialRepository.save(consultantCredential);

           return new ConsultantInfoResponse(user.getReferenceId().toString(),
                   user.getUserNameObject().getLastName(),
                   user.getUserNameObject().getFirstName(),
                   user.getGender().toString(),
                   user.getAddress(),
                   user.getMedicalCategory());
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }

    public Set<Consultant> getAllConsultants(){
        return ImmutableSet.
                copyOf(consultantRepository.findAll());
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
