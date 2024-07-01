package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.exceptions.CertificateNotFoundException;
import com.divjazz.recommendic.user.exceptions.NoCertificateException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.enums.CertificateType;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.certification.CertificationID;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.ProfilePictureRepository;
import com.divjazz.recommendic.user.repository.certificationRepo.ResumeRepository;
import com.divjazz.recommendic.user.repository.certificationRepo.UniCertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class FileService {
    private final ResumeRepository resumeRepository;
    private final UniCertRepository uniCertRepository;

    private final ProfilePictureRepository profilePictureRepository;

    private final ConsultantRepository consultantRepository;
    private final AppUserDetailsService userRepository;
    private final Random random;


    public FileService(ResumeRepository resumeRepository,
                       UniCertRepository uniCertRepository,
                       ProfilePictureRepository profilePictureRepository,
                       ConsultantRepository consultantRepository, AppUserDetailsService userRepository) {
        this.resumeRepository = resumeRepository;
        this.uniCertRepository = uniCertRepository;
        this.profilePictureRepository = profilePictureRepository;
        this.consultantRepository = consultantRepository;
        this.userRepository = userRepository;
        random = new Random();
    }


    @Transactional
    public void storeProfilePicture(UUID userId, String pictureUrl) throws IOException {
        User user = userRepository.retrieveUserByID(userId)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with that ID"));

        ProfilePicture profilePicture = new ProfilePicture(UUID.randomUUID(), user.getId(), user.getUserNameObject().getFullName() + " profile pic " + String.valueOf(random.nextInt(0,500)),pictureUrl);
        profilePictureRepository.save(profilePicture);
    }
    @Transactional
    public void storeCertificate(UUID userId, CertificateType type, String certificateUrl) throws IOException{
        Consultant consultant = consultantRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("The consultant was not found"));

        Certification certification = new Certification(UUID.randomUUID(),
                consultant,
                consultant.getUserNameObject().getFullName() + "certificate " + String.valueOf(random.nextInt(0,500)),
                certificateUrl);

        switch (type){
            case RESUME -> resumeRepository.save(certification);
            case UNI_CERTIFICATE -> uniCertRepository.save(certification);
        }
    }
    public Certification getCertificationByConsultant(Consultant consultant, CertificateType type){
        return switch (type){
            case UNI_CERTIFICATE -> uniCertRepository
                    .findByOwnerOfCertification(consultant)
                    .orElseThrow(() -> new CertificateNotFoundException(consultant));
            case RESUME -> resumeRepository
                    .findByOwnerOfCertification(consultant)
                    .orElseThrow(() -> new CertificateNotFoundException(consultant));
        };
    }


    public ProfilePicture getProfilePictureByUserId(String userId) {
        User user = null;
        UUID id = UUID.fromString(userId);
        if (userRepository.retrieveUserByID(id).isPresent()){
            user = userRepository.retrieveUserByID(id).get();
        } else
            throw new UserNotFoundException("User was not found");
        return user.getProfilePicture();

    }

    public Set<Certification> getAllCertificationsByConsultantId(UUID userId){
        Consultant consultant = consultantRepository.findById(userId)
                .orElseThrow(() ->new UserNotFoundException("User with the id was not found"));
        Set<Certification> certifications = new HashSet<>();
        certifications.add(resumeRepository.findByOwnerOfCertification(consultant)
                .orElseThrow(() -> new CertificateNotFoundException(consultant)));
        certifications.add(uniCertRepository.findByOwnerOfCertification(consultant)
                .orElseThrow(() -> new CertificateNotFoundException(consultant)));
        return certifications;
    }

    public ProfilePicture getProfilePictureByProfilePictureId(String id){
        return profilePictureRepository.findById(new UserId(UUID.fromString(id)))
                .orElseThrow(() -> new UserNotFoundException(String.format("No profile picture of id %s!", id)));
    }

    @Transactional
    public Certification getCertificationById(String id){
        Optional<Certification> test1 = resumeRepository.findById(new CertificationID(UUID.fromString(id)));
        Optional<Certification> test2 = uniCertRepository.findById(new CertificationID(UUID.fromString(id)));
        if (test1.isPresent() && test2.isEmpty()){
            return test1.get();
        } else if (test2.isPresent() && test1.isEmpty()){
            return test2.get();
        }
        else{
            throw new NoCertificateException("No Certificate with that id found");
        }
    }


}
