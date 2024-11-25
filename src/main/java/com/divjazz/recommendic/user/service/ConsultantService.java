package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.ConsultantResponse;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.confirmation.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.RoleRepository;
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import com.google.common.collect.ImmutableSet;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsultantService {

    private final UserRepository userRepository;
    private final UserConfirmationRepository userConfirmationRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeneralUserService userService;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ConsultantRepository consultantRepository;
    private final AssignmentService assignmentService;


    public ConsultantService(
            ConsultantRepository consultantRepository,
            UserRepository userRepository,
            UserConfirmationRepository userConfirmationRepository,
            UserCredentialRepository userCredentialRepository,
            PasswordEncoder passwordEncoder,
            GeneralUserService userService,
            RoleRepository roleRepository,
            ApplicationEventPublisher applicationEventPublisher,
            AssignmentService assignmentService) {
        this.userRepository = userRepository;
        this.consultantRepository = consultantRepository;
        this.userConfirmationRepository = userConfirmationRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.assignmentService = assignmentService;
    }

    @Transactional
    public ConsultantResponse createConsultant(ConsultantDTO consultantDTO) {
        Role role = roleRepository.getRoleByName("ROLE_CONSULTANT").orElseThrow(() -> new RuntimeException("No such role exists"));
        UserCredential userCredential = new UserCredential(passwordEncoder.encode(consultantDTO.password()));
        Consultant user = new Consultant(
                consultantDTO.userName(),
                consultantDTO.email(),
                consultantDTO.phoneNumber(),
                consultantDTO.gender(),
                consultantDTO.address(),
                consultantDTO.medicalCategory(),role, userCredential
        );

        user.setUserCredential(userCredential);
        userCredential.setUser(user);
        var profilePicture = new ProfilePicture();

        profilePicture.setPictureUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
        profilePicture.setName("149071.png");
        user.setProfilePicture(profilePicture);
        user.setUserType(UserType.CONSULTANT);

        if (userService.isUserNotExists(user.getEmail())) {
            RequestContext.setUserId(user.getId());
            var userConfirmation = new UserConfirmation(user);
            userRepository.save(user);
            userConfirmationRepository.save(userConfirmation);
            userCredentialRepository.save(userCredential);
            UserEvent userEvent = new UserEvent(user, EventType.REGISTRATION, Map.of("key",userConfirmation.getKey()));
            applicationEventPublisher.publishEvent(userEvent);
           return new ConsultantResponse(user.getUserId(),
                   user.getUserNameObject().getLastName(),
                   user.getUserNameObject().getFirstName(),
                   user.getGender().toString(),
                   user.getAddress(),
                   user.getMedicalCategory().toString());
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }

    @Transactional(readOnly = true)
    public Set<Consultant> getAllConsultants(){
        return ImmutableSet.
                copyOf(consultantRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Set<Consultant> getConsultantByCategory(MedicalCategory category){
        return ImmutableSet.
                copyOf(
                        consultantRepository.findByMedicalCategory(category)
                                .orElseThrow(UserNotFoundException::new)
                );
    }

    @Transactional(readOnly = true)
    public Set<Consultant> getConsultantsByName(String name){

        return consultantRepository.findConsultantByName(name);
    }

    public Set<ConsultantResponse> searchSomeConsultantsByQuery(String query){
        return consultantRepository.searchConsultant(query)
                .stream()
                .limit(5)
                .map(consultant -> new ConsultantResponse(
                        consultant.getUserId(),
                        consultant.getUserNameObject().getLastName(),
                        consultant.getUserNameObject().getFirstName(),
                        consultant.getGender().toString(),
                        consultant.getAddress(),
                        consultant.getMedicalCategory().toString()
                ))
                .collect(Collectors.toSet());
    }

    public Set<Consultation> getAllConsultations(String consultantId){
        return consultantRepository.findAllConsultationsByConsultantId(consultantId);
    }

    public Optional<Consultant> retrieveConsultantByUserId(String userId){
        return consultantRepository.findByUserId(userId);
    }

    public Set<Consultant> getAllUnCertifiedConsultants(){
        return consultantRepository.findUnCertifiedConsultant();
    }

}
