package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.controller.admin.AdminCredentialResponse;
import com.divjazz.recommendic.user.controller.admin.AdminRegistrationParams;
import com.divjazz.recommendic.user.controller.admin.GenerateAdminPasswordResponse;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.Assignment;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {
    private final GeneralUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final AssignmentService assignmentService;


    @Transactional
    public AdminCredentialResponse createAdmin(AdminRegistrationParams adminRegistrationParams) throws UserAlreadyExistsException {

        GenerateAdminPasswordResponse response = generateAdminPassword();
        String password = response.encryptedPassword();
        UserCredential userCredential = new UserCredential(response.encryptedPassword());

        var user = new Admin(
                adminRegistrationParams.email(),
                Gender.valueOf(adminRegistrationParams.gender().toUpperCase()),
                userCredential
        );
        user.getUserPrincipal().setEnabled(true);
        user.setUserStage(UserStage.ACTIVE_USER);
        var profilePicture = new ProfilePicture();
        profilePicture.setPictureUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
        profilePicture.setName("149071.png");
        var userProfile = AdminProfile.builder()
                .admin(user)
                .address(new Address(
                        adminRegistrationParams.city(),
                        adminRegistrationParams.state(),
                        adminRegistrationParams.country()))
                .phoneNumber(adminRegistrationParams.phoneNumber())
                .profilePicture(profilePicture)
                .userName(new UserName(adminRegistrationParams.firstName(), adminRegistrationParams.lastName()))
                .build();
        user.setAdminProfile(userProfile);
        adminRepository.save(user);
        var userConfirmation = new UserConfirmation(user);
        UserEvent userEvent = new UserEvent(user.getUserType(),
                EventType.REGISTRATION,
                Map.of("key", userConfirmation.getKey(),
                        "password", response.normalPassword(),
                        "firstname", "",
                        "email", user.getUserPrincipal().getUsername()));
        applicationEventPublisher.publishEvent(userEvent);
        return new AdminCredentialResponse(user.getUserPrincipal().getUsername(),
                password);

    }


    public Admin getAdminByEmail(String email) {
        return adminRepository
                .findByUserPrincipal_Email(email)
                .orElseThrow(() -> new EntityNotFoundException("Admin with email: %s not found".formatted(email)));
    }

    private GenerateAdminPasswordResponse generateAdminPassword() {
        Faker faker = new Faker();
        String password = faker.internet().password(8, 15, true);
        return new GenerateAdminPasswordResponse(passwordEncoder.encode(password), password);

    }

    public Page<Admin> getAllAdmins(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }

    public Set<Assignment> getAllAssignmentsAssigned(String adminId) {
        return assignmentService.retrieveAllAssignmentByAdminId(adminId);
    }

}
