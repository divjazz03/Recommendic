package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.controller.admin.AdminCredentialResponse;
import com.divjazz.recommendic.user.controller.admin.GenerateAdminPasswordResponse;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class AdminService {

    private final UserCredentialRepository userCredentialRepository;

    private final GeneralUserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AdminRepository adminRepository;


    public AdminService(
            UserCredentialRepository userCredentialRepository, GeneralUserService userService,
            PasswordEncoder passwordEncoder,
            AdminRepository adminRepository) {
        this.userCredentialRepository = userCredentialRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
    }


    public AdminCredentialResponse createAdmin(AdminDTO adminDTO) {
        GenerateAdminPasswordResponse response = generateAdminPassword();
        String password = response.encryptedPassword();

        Role role = new Role();
        UserCredential userCredential = new UserCredential(response.encryptedPassword());

        Admin admin = new Admin(
                adminDTO.userName(),
                adminDTO.email(),
                adminDTO.number(),
                adminDTO.gender(),
                adminDTO.address(),role,userCredential
        );

        userCredential.setUser(admin);

        if (userService.isUserNotExists(admin.getEmail())) {
            adminRepository.save(admin);
            userCredentialRepository.save(userCredential);
            return new AdminCredentialResponse(admin.getEmail(),
                    password);
        } else {
            throw new UserAlreadyExistsException(admin.getEmail());
        }

    }



    public Admin getAdminByUsername(String email){
        return adminRepository
                .findByEmail(email).orElseThrow(() -> new UserNotFoundException("This Admin was not found"));
    }

    private GenerateAdminPasswordResponse generateAdminPassword(){
        Faker faker = new Faker();
        String password = faker.internet().password(8,15,true);
        return new GenerateAdminPasswordResponse( passwordEncoder.encode(password), password);

    }

    public ResponseEntity<Set<Admin>> getAllAdmins(){
        ImmutableSet<Admin> admins = ImmutableSet
                .copyOf(adminRepository.findAll());

        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

}
