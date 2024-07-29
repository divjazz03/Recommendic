package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final ConsultantRepository consultantRepository;
    private final AdminRepository adminRepository;

    public AppUserDetailsService(PatientRepository patientRepository, ConsultantRepository consultantRepository, AdminRepository adminRepository) {
        this.patientRepository = patientRepository;
        this.consultantRepository = consultantRepository;

        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return retrieveUserByUsername(username);

    }

    public User retrieveUserByUsername(String username){
        User user = null;
        if (patientRepository.findByEmail(username).isPresent()){
            user = patientRepository.findByEmail(username).get();
        } else if(consultantRepository.findByEmail(username).isPresent()){
            user = consultantRepository.findByEmail(username).get();
        } else if(adminRepository.findByEmail(username).isPresent()){
            user = adminRepository.findByEmail(username).get();
        } else
            throw new UsernameNotFoundException("User not found");

        return user;
    }
    public User retrieveUserByID(long id){
        User user = null;
        if (patientRepository.findById(id).isPresent()){
            user = patientRepository.findById(id).get();
        } else if(consultantRepository.findById(id).isPresent()){
            user = consultantRepository.findById(id).get();
        } else if(adminRepository.findById(id).isPresent()){
            user = adminRepository.findById(id).get();
        } else {
            throw new UserNotFoundException("no user with that id was found");
        }

        return user;
    }
    public boolean isUserNotExists(String username){
        return !patientRepository.existsByEmail(username) && !consultantRepository.existsByEmail(username) && !adminRepository.existsByEmail(username);
    }
}
