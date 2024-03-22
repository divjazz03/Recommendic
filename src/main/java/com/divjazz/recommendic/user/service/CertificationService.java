package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.exceptions.CertificateNotFoundException;
import com.divjazz.recommendic.user.exceptions.NoCertificateException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.certification.CertificateType;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.certification.CertificationID;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.UserRepositoryImpl;
import com.divjazz.recommendic.user.repository.certificationRepo.ResumeRepository;
import com.divjazz.recommendic.user.repository.certificationRepo.UniCertRepository;
import com.divjazz.recommendic.utils.fileUpload.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class CertificationService {
    private final ResumeRepository resumeRepository;
    private final UniCertRepository uniCertRepository;
    private final UserRepositoryImpl userRepository;
    private final ConsultantRepository consultantRepository;


    public CertificationService(ResumeRepository resumeRepository, UniCertRepository uniCertRepository, UserRepositoryImpl userRepository, ConsultantRepository consultantRepository) {
        this.resumeRepository = resumeRepository;
        this.uniCertRepository = uniCertRepository;
        this.userRepository = userRepository;
        this.consultantRepository = consultantRepository;
    }

    public void storeCertificate(MultipartFile file, UserId userId, CertificateType type) throws IOException{
        Consultant consultant = consultantRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("The consultant was not found"));
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Certification certification = new Certification(userRepository.nextCertificateId(), consultant, fileName);

        switch (type){
            case RESUME -> resumeRepository.save(certification);
            case UNI_CERTIFICATE -> uniCertRepository.save(certification);
        }
        new ResponseEntity<>(new ResponseMessage("Successfully uploaded document"), HttpStatus.CREATED);

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
        } else{
            throw new NoCertificateException("No Certificate with that id found");
        }
    }


}
