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
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.repository.UserIdRepository;
import com.divjazz.recommendic.user.repository.certificationRepo.ResumeRepository;
import com.divjazz.recommendic.user.repository.certificationRepo.UniCertRepository;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final UserRepository userRepository;
    private final UserIdRepository userIdRepository;
    private final ConsultantRepository consultantRepository;


    public FileService(ResumeRepository resumeRepository, UniCertRepository uniCertRepository, ProfilePictureRepository profilePictureRepository, UserRepository userRepositoryCustom, UserIdRepository userIdRepository, ConsultantRepository consultantRepository) {
        this.resumeRepository = resumeRepository;
        this.uniCertRepository = uniCertRepository;
        this.profilePictureRepository = profilePictureRepository;
        this.userRepository = userRepositoryCustom;
        this.userIdRepository = userIdRepository;
        this.consultantRepository = consultantRepository;
    }


    @Transactional
    public void storeProfilePicture(MultipartFile file, UserId id) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Could not find user with that ID"));
        ProfilePicture profilePicture = new ProfilePicture(userIdRepository.nextId(), user, user.getUserNameObject().getFullName() + " profile pic", file.getBytes());
        User updatedUser = new User(user.getId(), user.getUserNameObject(),user.getEmail(),user.getPhoneNumber(),user.getGender(),user.getAddress(),user.getUserType(),profilePicture, user.getPassword());
        userRepository.save(user);

    }
    @Transactional
    public void storeCertificate(MultipartFile file, UserId userId, CertificateType type) throws IOException{
        Consultant consultant = consultantRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("The consultant was not found"));
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Certification certification = new Certification(userIdRepository.nextCertificateId(), consultant, fileName, file.getBytes());

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

    public Set<Certification> getAllCertificationsByConsultantId(UserId userId){
        Consultant consultant = consultantRepository.findById(userId).orElseThrow(() ->new UserNotFoundException("User with the id was not found"));
        Set<Certification> certifications = new HashSet<>();
        certifications.add(resumeRepository
                .findByOwnerOfCertification(consultant)
                .orElseThrow(() -> new CertificateNotFoundException(consultant)));
        certifications.add(uniCertRepository
                .findByOwnerOfCertification(consultant)
                .orElseThrow(() -> new CertificateNotFoundException(consultant)));
        return certifications;
    }

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
