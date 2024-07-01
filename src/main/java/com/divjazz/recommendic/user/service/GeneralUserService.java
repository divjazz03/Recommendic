package com.divjazz.recommendic.user.service;

import org.springframework.stereotype.Service;

@Service
public class GeneralUserService {
    UserRepository repositoryCustom;

    public GeneralUserService(UserRepository repositoryCustom) {
        this.repositoryCustom = repositoryCustom;
    }

    public boolean verifyIfEmailNotExists(String email){
        return (repositoryCustom.findUserByEmail(email).isEmpty());
    }
}
