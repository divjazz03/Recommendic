package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.controller.admin.AdminCredentialResponse;
import com.divjazz.recommendic.user.controller.admin.GenerateAdminPasswordResponse;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.Assignment;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
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
public class AdminService {
    private final GeneralUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final AssignmentService assignmentService;


    public AdminService(GeneralUserService userService,
            PasswordEncoder passwordEncoder,
            AdminRepository adminRepository,
            ApplicationEventPublisher applicationEventPublisher, AssignmentService assignmentService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.assignmentService = assignmentService;
    }

    @Transactional
    public AdminCredentialResponse createAdmin(AdminDTO adminDTO) throws UserAlreadyExistsException {
        GenerateAdminPasswordResponse response = generateAdminPassword();
        String password = response.encryptedPassword();
        UserCredential userCredential = new UserCredential(response.encryptedPassword());

        Admin user = new Admin(
                adminDTO.userName(),
                adminDTO.email(),
                adminDTO.number(),
                adminDTO.gender(),
                adminDTO.address(), Role.ADMIN, userCredential
        );
        var profilePicture = new ProfilePicture();
        profilePicture.setPictureUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
        profilePicture.setName("149071.png");
        user.setProfilePicture(profilePicture);
        user.setUserType(UserType.ADMIN);
        if (userService.isUserNotExists(user.getEmail())) {
            adminRepository.save(user);
            var userConfirmation = new UserConfirmation(user);
            UserEvent userEvent = new UserEvent(user,
                    EventType.REGISTRATION,
                    Map.of("key", userConfirmation.getKey(), "password", response.normalPassword()));
            applicationEventPublisher.publishEvent(userEvent);
            return new AdminCredentialResponse(user.getEmail(),
                    password);
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }


    public Admin getAdminByEmail(String email) {
        return adminRepository
                .findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    private GenerateAdminPasswordResponse generateAdminPassword() {
//        Faker faker = new Faker();
//        String password = faker.internet().password(8, 15, true);
//        return new GenerateAdminPasswordResponse(passwordEncoder.encode(password), password);
        return null;

    }

    public Page<Admin> getAllAdmins(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }

    public Set<Assignment> getAllAssignmentsAssigned(String adminId) {
        return assignmentService.retrieveAllAssignmentByAdminId(adminId);
    }

}
