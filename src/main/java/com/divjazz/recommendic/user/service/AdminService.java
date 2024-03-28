package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.enums.UserType;
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
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.repository.UserIdRepository;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;

    private final UserIdRepository userRepositoryImpl;

    private final AdminPasswordRepository adminPasswordRepository;

    private final GeneralUserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AdminRepository adminRepository;

    public AdminService(UserRepository userRepository, UserIdRepository userRepositoryImpl, AdminPasswordRepository adminPasswordRepository, GeneralUserService userService, PasswordEncoder passwordEncoder, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.userRepositoryImpl = userRepositoryImpl;
        this.adminPasswordRepository = adminPasswordRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
    }


    public ResponseEntity<AdminResponse> createAdmin(AdminDTO adminDTO) {
        GenerateAdminPasswordResponse response = generateAdminPassword();
        AdminPassword password = response.encryptedPassword();

        User adminUser = new User(
                userRepositoryImpl.nextId(),
                adminDTO.userName(),
                adminDTO.email(),
                adminDTO.number(),
                adminDTO.gender(),
                adminDTO.address(),
                UserType.ADMIN,
                password.getPassword()
        );
        password.setAssignedAdmin(adminUser);

        if (userService.verifyIfEmailNotExists(adminUser.getEmail())) {
            userRepository.save(adminUser);
            Admin admin = new Admin(userRepositoryImpl.nextId(), adminUser);
            adminRepository.save(admin);
            adminPasswordRepository.save(password);
            return new ResponseEntity<>(new AdminResponse(adminUser.getEmail(),
                    response.normalPassword(),
                    password.getExpiryDate()), HttpStatus.CREATED);
        } else {
            throw new UserAlreadyExistsException(adminUser.getEmail());
        }


    }

    public Optional<User> getAdminByEmail(String email){
        return userRepository.findByUserTypeAndEmail(UserType.ADMIN, email);
    }

    private GenerateAdminPasswordResponse generateAdminPassword(){
        Faker faker = new Faker();
        String password = faker.internet().password(10,15,true);
        return new GenerateAdminPasswordResponse(new AdminPassword(userRepositoryImpl.nextId(), null, passwordEncoder.encode(password)), password);

    }

    public ResponseEntity<Set<Admin>> getAllAdmins(){
        ImmutableSet<User> admins = ImmutableSet
                .copyOf(userRepository
                        .findAllByUserType(UserType.ADMIN)
                        .orElseThrow(() -> new UserNotFoundException("No Admins found")));

        return new ResponseEntity<>(admins.stream()
                .map(user -> adminRepository
                        .findByAdminUser(user)
                        .orElseThrow(() -> new UserNotFoundException("Admin user was not found")))
                .collect(Collectors.toSet()), HttpStatus.OK);
    }

}
