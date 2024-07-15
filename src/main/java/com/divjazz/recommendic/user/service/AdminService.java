package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.controller.admin.AdminCredentialResponse;
import com.divjazz.recommendic.user.controller.admin.GenerateAdminPasswordResponse;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.userAttributes.credential.AdminCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.credential.AdminCredentialRepository;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AdminService {

    private final AdminCredentialRepository adminCredentialRepository;

    private final AppUserDetailsService userService;

    private final PasswordEncoder passwordEncoder;

    private final AdminRepository adminRepository;

    private final AlternativeJdkIdGenerator idGenerator;

    public AdminService(
            AdminCredentialRepository adminCredentialRepository, AppUserDetailsService userService,
            PasswordEncoder passwordEncoder,
            AdminRepository adminRepository, AlternativeJdkIdGenerator idGenerator) {
        this.adminCredentialRepository = adminCredentialRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;

        this.idGenerator = idGenerator;
    }


    public AdminCredentialResponse createAdmin(AdminDTO adminDTO) {
        GenerateAdminPasswordResponse response = generateAdminPassword();
        String password = response.encryptedPassword();

        Admin admin = new Admin(
                UUID.randomUUID(),
                adminDTO.userName(),
                adminDTO.email(),
                adminDTO.number(),
                adminDTO.gender(),
                adminDTO.address()
        );

        AdminCredential adminCredential = new AdminCredential(admin, response.encryptedPassword(),idGenerator.generateId());

        if (!userService.isUserExists(admin.getEmail())) {
            adminRepository.save(admin);
            adminCredentialRepository.save(adminCredential);
            return new AdminCredentialResponse(admin.getEmail(),
                    password,
                    adminCredential.getExpiryDate());
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
