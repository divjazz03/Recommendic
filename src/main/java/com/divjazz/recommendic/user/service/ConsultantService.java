package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.RoleRepository;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import com.google.common.collect.ImmutableSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsultantService {


    private final ConsultantRepository consultantRepository;
    private final UserCredentialRepository userCredentialRepository;

    private final PasswordEncoder passwordEncoder;

    private final GeneralUserService userService;

    private final RoleRepository roleRepository;

    public ConsultantService(
            ConsultantRepository consultantRepository,
            UserCredentialRepository userCredentialRepository, PasswordEncoder passwordEncoder,
            GeneralUserService userService, RoleRepository roleRepository) {
        this.consultantRepository = consultantRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    public ConsultantInfoResponse createConsultant(ConsultantDTO consultantDTO) {
        Role role = new Role();
        UserCredential userCredential = new UserCredential(passwordEncoder.encode(consultantDTO.password()));
        Consultant user = new Consultant(
                consultantDTO.userName(),
                consultantDTO.email(),
                consultantDTO.phoneNumber(),
                consultantDTO.gender(),
                consultantDTO.address(),
                consultantDTO.medicalCategory(),null, userCredential
        );

        user.setUserCredential(userCredential);
        userCredential.setUser(user);

        if (userService.isUserNotExists(user.getEmail())) {
            consultantRepository.save(user);
            userCredentialRepository.save(userCredential);

           return new ConsultantInfoResponse(user.getId(),
                   user.getUserNameObject().getLastName(),
                   user.getUserNameObject().getFirstName(),
                   user.getGender().toString(),
                   user.getAddress(),
                   user.getMedicalCategory().toString());
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
