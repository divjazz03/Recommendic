package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.repository.UserIdRepository;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConsultantService {

    private final UserRepository userRepository;
    private final UserIdRepository userIdRepository;
    private final ConsultantRepository consultantRepository;

    private final PasswordEncoder passwordEncoder;

    private final GeneralUserService userService;

    public ConsultantService(UserRepository userRepositoryCustom, UserIdRepository userIdRepository, ConsultantRepository consultantRepository, PasswordEncoder passwordEncoder, GeneralUserService userService) {
        this.userRepository = userRepositoryCustom;
        this.userIdRepository = userIdRepository;
        this.consultantRepository = consultantRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public ResponseEntity<ResponseMessage> createConsultant(ConsultantDTO consultantDTO) {
        User user = new User(
                userIdRepository.nextId(),
                consultantDTO.userName(),
                consultantDTO.email(),
                consultantDTO.phoneNumber(),
                consultantDTO.gender(),
                consultantDTO.address(),
                UserType.CONSULTANT,
                passwordEncoder.encode(consultantDTO.password())
        );
        if (userService.verifyIfEmailNotExists(user.getEmail())) {
            userRepository.save(user);
            Consultant consultant = new Consultant(userIdRepository.nextId(), user, consultantDTO.medicalCategory());
            consultantRepository.save(consultant);
            return new ResponseEntity<>(new ResponseMessage(user.toString()), HttpStatus.CREATED);
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }

    public ResponseEntity<Set<Consultant>> getAllConsultants(){
        Set<User> consultants = ImmutableSet.
                copyOf(userRepository
                        .findAllByUserType(UserType.CONSULTANT)
                        .orElseThrow(() -> new UserNotFoundException("No consultant was found")));
        return new ResponseEntity<>(consultants.stream()
                .map(user -> consultantRepository
                        .findByUser(user)
                        .orElseThrow(() -> new UserNotFoundException("Consultant was not found")))
                .collect(Collectors.toSet()), HttpStatus.OK);
    }
}
