package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.repository.UserRepositoryCustom;
import com.divjazz.recommendic.user.repository.UserRepositoryImpl;
import org.springframework.stereotype.Service;

@Service
public class GeneralUserService {
    UserRepositoryImpl repositoryCustom;

    public GeneralUserService(UserRepositoryImpl repositoryCustom) {
        this.repositoryCustom = repositoryCustom;
    }

    public boolean verifyIfEmailExists(String email){
        return (repositoryCustom.findUserByEmail(email).isPresent());
    }
}
