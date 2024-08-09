package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.controller.admin.AdminCredentialResponse;
import com.divjazz.recommendic.user.controller.admin.GenerateAdminPasswordResponse;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.confirmation.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.RoleRepository;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableSet;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class AdminService {

    private final UserCredentialRepository userCredentialRepository;

    private final RoleRepository roleRepository;

    private final GeneralUserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AdminRepository adminRepository;
    private final ApplicationEventPublisher applicationEventPublisher;


    public AdminService(
            UserCredentialRepository userCredentialRepository, RoleRepository roleRepository, GeneralUserService userService,
            PasswordEncoder passwordEncoder,
            AdminRepository adminRepository,
            ApplicationEventPublisher applicationEventPublisher) {
        this.userCredentialRepository = userCredentialRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public AdminCredentialResponse createAdmin(AdminDTO adminDTO) throws UserAlreadyExistsException{
            GenerateAdminPasswordResponse response = generateAdminPassword();
            String password = response.encryptedPassword();
            Role role = roleRepository.getRoleByName("ROLE_ADMIN").orElseThrow(() -> new RuntimeException("No such roles found"));
            UserCredential userCredential = new UserCredential(response.encryptedPassword());

            Admin user = new Admin(
                    adminDTO.userName(),
                    adminDTO.email(),
                    adminDTO.number(),
                    adminDTO.gender(),
                    adminDTO.address(),role,userCredential
            );

            userCredential.setUser(user);
            var profilePicture = new ProfilePicture();
            profilePicture.setPictureUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
            profilePicture.setName("149071.png");
            user.setProfilePicture(profilePicture);
            if (userService.isUserNotExists(user.getEmail())) {
                adminRepository.save(user);
                userCredentialRepository.save(userCredential);
                var userConfirmation = new UserConfirmation(user);
                UserEvent userEvent = new UserEvent(user,
                        EventType.REGISTRATION,
                        Map.of("key",userConfirmation.getKey(), "password", response.normalPassword()));
                applicationEventPublisher.publishEvent(userEvent);
                return new AdminCredentialResponse(user.getEmail(),
                        password);
            } else {
                throw new UserAlreadyExistsException(user.getEmail());
            }
    }



    public Admin getAdminByEmail(String email){
        return adminRepository
                .findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    private GenerateAdminPasswordResponse generateAdminPassword(){
        Faker faker = new Faker();
        String password = faker.internet().password(8,15,true);
        return new GenerateAdminPasswordResponse( passwordEncoder.encode(password), password);

    }

    public Set<Admin> getAllAdmins(){
        return ImmutableSet
                .copyOf(adminRepository.findAll());
    }

}
