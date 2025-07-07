package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsultantService {

    private final UserConfirmationRepository userConfirmationRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeneralUserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ConsultantRepository consultantRepository;

    public static ConsultantInfoResponse consultantToConsultantInfoResponse(Consultant consultant) {
        return new ConsultantInfoResponse(consultant.getUserId(),
                consultant.getUserNameObject().getLastName(),
                consultant.getUserNameObject().getFirstName(),
                consultant.getGender().toString(),
                consultant.getPhoneNumber(),
                consultant.getAddress(), "");
    }

    private static Consultant getConsultant(ConsultantDTO consultantDTO, UserCredential userCredential) {
        Consultant user = new Consultant(
                consultantDTO.userName(),
                consultantDTO.email(),
                consultantDTO.phoneNumber(),
                consultantDTO.gender(),
                consultantDTO.address(),
                userCredential
        );

        user.setUserCredential(userCredential);
        user.setUserStage(UserStage.ONBOARDING);
        var profilePicture = new ProfilePicture();

        profilePicture.setPictureUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
        profilePicture.setName("149071.png");
        user.setProfilePicture(profilePicture);
        user.setUserType(UserType.CONSULTANT);
        return user;
    }

    @Transactional
    public ConsultantInfoResponse createConsultant(ConsultantDTO consultantDTO) {
        UserCredential userCredential = new UserCredential(passwordEncoder.encode(consultantDTO.password()));
        Consultant user = getConsultant(consultantDTO, userCredential);
        if (userService.isUserNotExists(user.getEmail())) {
            RequestContext.setUserId(user.getId());
            var userConfirmation = new UserConfirmation(user);
            var savedConsultant = consultantRepository.save(user);
            userConfirmationRepository.save(userConfirmation);
            UserEvent userEvent = new UserEvent(user, EventType.REGISTRATION, Map.of("key", userConfirmation.getKey()));
            applicationEventPublisher.publishEvent(userEvent);
            return consultantToConsultantInfoResponse(savedConsultant);
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }

    @Transactional(readOnly = true)
    public Page<Consultant> getAllConsultants(Pageable pageable) {
        return consultantRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Set<Consultant> getConsultantsByCategory(MedicalCategoryEnum category) {
        return ImmutableSet.
                copyOf(
                        consultantRepository
                                .findByMedicalCategoryIgnoreCase(
                                        MedicalCategory.fromMedicalCategoryEnum(category).name()
                                )

                );
    }

    public Set<ConsultantInfoResponse> searchSomeConsultantsByQuery(String query) {
        return consultantRepository.searchConsultant(query)
                .stream()
                .limit(5)
                .map(consultant -> new ConsultantInfoResponse(
                        consultant.getUserId(),
                        consultant.getUserNameObject().getLastName(),
                        consultant.getUserNameObject().getFirstName(),
                        consultant.getGender().toString(),
                        consultant.getPhoneNumber(),
                        consultant.getAddress(),
                        consultant.getMedicalCategory().toString()
                ))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Consultant retrieveConsultantByUserId(String userId) {
        return consultantRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Consultant with id: %s not found".formatted(userId)));
    }

    public void deleteConsultantById(String userId) {
        if (userService.isUserNotExists(userId)) {
            throw new EntityNotFoundException("Consultant does not exist or has been deleted");
        }
        consultantRepository.deleteByUserId(userId);
    }


    public Set<Consultant> getAllUnCertifiedConsultants() {
        return consultantRepository.findUnCertifiedConsultant();
    }

    public boolean handleOnboarding(String userId, String medicalSpecialization) {

        MedicalCategoryEnum medicalSpec = MedicalCategoryEnum.fromValue(medicalSpecialization);
        Consultant consultant = consultantRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Consultant with id: %s not found".formatted(userId)));
        consultant.setMedicalCategory(medicalSpec);
        consultant.setUserStage(UserStage.ACTIVE_USER);
        consultantRepository.save(consultant);

        return true;
    }
}
