package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.UserRepositoryCustom;
import com.divjazz.recommendic.user.repository.UserRepositoryImpl;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import com.google.common.collect.ImmutableSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConsultantService {

    private final UserRepositoryCustom userRepositoryCustom;
    private final UserRepositoryImpl userRepository;
    private final ConsultantRepository consultantRepository;

    private final GeneralUserService userService;

    public ConsultantService(UserRepositoryCustom userRepositoryCustom, UserRepositoryImpl userRepository, ConsultantRepository consultantRepository, GeneralUserService userService) {
        this.userRepositoryCustom = userRepositoryCustom;
        this.userRepository = userRepository;
        this.consultantRepository = consultantRepository;
        this.userService = userService;
    }

    public ResponseEntity<ResponseMessage> createConsultant(ConsultantDTO consultantDTO) {
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
        if (!userService.verifyIfEmailExists(user.getEmail())) {
            userRepositoryCustom.save(user);
            Consultant consultant = new Consultant(userRepository.nextId(), user);
            consultantRepository.save(consultant);
            return new ResponseEntity<>(new ResponseMessage(user.toString()), HttpStatus.CREATED);
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }

    public ResponseEntity<Set<Consultant>> getAllConsultants(){
        Set<User> consultants = ImmutableSet.
                copyOf(userRepositoryCustom
                        .findAllByUserType(UserType.CONSULTANT)
                        .orElseThrow(() -> new UserNotFoundException("No consultant was found")));
        return new ResponseEntity<>(consultants.stream()
                .map(user -> consultantRepository
                        .findByUser(user)
                        .orElseThrow(() -> new UserNotFoundException("Consultant was not found")))
                .collect(Collectors.toSet()), HttpStatus.OK);
    }
}
