package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Gender;
import com.divjazz.recommendic.user.repository.UserRepositoryCustom;
import com.divjazz.recommendic.user.repository.UserRepositoryImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ConsultantService {

    private final UserRepositoryImpl userRepository;

    public ConsultantService(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<User> createConsultant(ConsultantDTO consultantDTO) {
        User user = new User(
                userRepository.nextId(),
                consultantDTO.userName(),
                consultantDTO.email(),
                consultantDTO.phoneNumber(),
                consultantDTO.gender(),
                consultantDTO.address(),
                UserType.CONSULTANT,
                consultantDTO.password()
        );
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
