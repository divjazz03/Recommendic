package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.repository.ConsultantRepository;
import org.springframework.stereotype.Service;

@Service
public class ConsultantService {
    private final ConsultantRepository consultantRepository;

    public ConsultantService(ConsultantRepository consultantRepository) {
        this.consultantRepository = consultantRepository;
    }
}
