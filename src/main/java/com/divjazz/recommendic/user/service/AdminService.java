package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.controller.admin.AdminResponse;
import com.divjazz.recommendic.user.controller.admin.GenerateAdminPasswordResponse;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.AdminPassword;
import com.divjazz.recommendic.user.repository.AdminPasswordRepository;
import com.divjazz.recommendic.user.repository.UserRepositoryCustom;
import com.divjazz.recommendic.user.repository.UserRepositoryImpl;
import com.github.javafaker.Faker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {

    private final UserRepositoryCustom userRepositoryCustom;

    private final UserRepositoryImpl userRepositoryImpl;

    private final AdminPasswordRepository adminPasswordRepository;

    private final GeneralUserService userService;

    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepositoryCustom userRepositoryCustom, UserRepositoryImpl userRepositoryImpl, AdminPasswordRepository adminPasswordRepository, GeneralUserService userService, PasswordEncoder passwordEncoder) {
        this.userRepositoryCustom = userRepositoryCustom;
        this.userRepositoryImpl = userRepositoryImpl;
        this.adminPasswordRepository = adminPasswordRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<AdminResponse> createAdmin(AdminDTO adminDTO) {
        GenerateAdminPasswordResponse response = generateAdminPassword();
        AdminPassword password = response.encryptedPassword();
        User admin = new User(
                userRepositoryImpl.nextId(),
                adminDTO.userName(),
                adminDTO.email(),
                adminDTO.number(),
                adminDTO.gender(),
                adminDTO.address(),
                UserType.ADMIN,
                password.getPassword()
        );
        password.setAssignedAdmin(admin);

        if (userService.verifyIfEmailExists(admin.getEmail())) {
            userRepositoryCustom.save(admin);
            adminPasswordRepository.save(password);
            return new ResponseEntity<>(new AdminResponse(admin.getEmail(),
                    response.normalPassword(),
                    password.getExpiryDate()), HttpStatus.CREATED);
        } else {
            throw new UserAlreadyExistsException(admin.getEmail());
        }


    }

    public Optional<User> getAdminByEmail(String email){
        return userRepositoryCustom.findByUserTypeAndEmail(UserType.ADMIN, email);
    }

    private GenerateAdminPasswordResponse generateAdminPassword(){
        Faker faker = new Faker();
        String password = faker.internet().password(10,15,true);
        return new GenerateAdminPasswordResponse(new AdminPassword(userRepositoryImpl.nextId(), null, passwordEncoder.encode(password)), password);

    }


}
