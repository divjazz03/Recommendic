package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.controller.admin.AdminResponse;
import com.divjazz.recommendic.user.controller.admin.GenerateAdminPasswordResponse;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.AdminPassword;
import com.divjazz.recommendic.user.repository.AdminPasswordRepository;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AdminPasswordRepository adminPasswordRepository;

    private final AppUserDetailsService userService;

    private final PasswordEncoder passwordEncoder;

    private final AdminRepository adminRepository;

    public AdminService(
                        AdminPasswordRepository adminPasswordRepository,
                        AppUserDetailsService userService,
                        PasswordEncoder passwordEncoder,
                        AdminRepository adminRepository) {

        this.adminPasswordRepository = adminPasswordRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;

    }


    public ResponseEntity<AdminResponse> createAdmin(AdminDTO adminDTO) {
        GenerateAdminPasswordResponse response = generateAdminPassword();
        AdminPassword password = response.encryptedPassword();

        Admin admin = new Admin(
                UUID.randomUUID(),
                adminDTO.userName(),
                adminDTO.email(),
                adminDTO.number(),
                adminDTO.gender(),
                adminDTO.address()
        );
        password.setAssignedAdmin(admin);

        if (userService.isUserExists(admin.getEmail())) {

            adminRepository.save(admin);
            adminPasswordRepository.save(password);
            return new ResponseEntity<>(new AdminResponse(admin.getEmail(),
                    response.normalPassword(),
                    password.getExpiryDate()), HttpStatus.CREATED);
        } else {
            throw new UserAlreadyExistsException(admin.getEmail());
        }


    }

    public Optional<Admin> getAdminByUsername(String email){
        return adminRepository.findByEmail(email);
    }

    private GenerateAdminPasswordResponse generateAdminPassword(){
        Faker faker = new Faker();
        String password = faker.internet().password(10,15,true);
        return new GenerateAdminPasswordResponse(new AdminPassword(UUID.randomUUID(), null, passwordEncoder.encode(password)), password);

    }

    public ResponseEntity<Set<Admin>> getAllAdmins(){
        ImmutableSet<Admin> admins = ImmutableSet
                .copyOf(adminRepository.findAll());

        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

}
