package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.AdminPassword;
import com.divjazz.recommendic.user.repository.AdminPasswordRepository;
import com.divjazz.recommendic.user.repository.UserRepositoryCustom;
import com.github.javafaker.Faker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final UserRepositoryCustom userRepositoryCustom;

    private final AdminPasswordRepository adminPasswordRepository;

    private final GeneralUserService userService;

    private final PasswordEncoder passwordEncoder;

    public AdminService(PasswordEncoder passwordEncoder, UserRepositoryCustom userRepositoryCustom, AdminPasswordRepository adminPasswordRepository, GeneralUserService userService) {
        this.userRepositoryCustom = userRepositoryCustom;
        this.adminPasswordRepository = adminPasswordRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public ResponseEntity<User> createAdmin(AdminDTO adminDTO) {
        User admin = null;
        AdminPassword password = generateAdminPassword(admin);
        admin = new User(
                userRepositoryCustom.nextId(),
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
            return new ResponseEntity<>(admin, HttpStatus.CREATED);
        } else {
            throw new UserAlreadyExistsException(admin.getEmail());
        }


    }

    public Optional<User> getAdminByEmail(String email){
        return userRepositoryCustom.findByUserTypeAndEmail(UserType.ADMIN, email);
    }

    private AdminPassword generateAdminPassword(User admin){
        Faker faker = new Faker();
        return new AdminPassword(userRepositoryCustom.nextId(), admin, passwordEncoder.encode(faker.internet().password(10,15,true)));

    }


}
