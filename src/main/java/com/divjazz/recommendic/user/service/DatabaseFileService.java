package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.enums.CertificateType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class DatabaseFileService implements FileService{
    @Override
    public void storeProfilePicture(UUID userId, String pictureUrl) {

    }

    @Override
    public void storeCertificate(UUID userId, CertificateType type, String certificateUrl) {

    }

    @Override
    public Certification getCertificationByConsultant(Consultant consultant, CertificateType type) {
        return null;
    }

    @Override
    public ProfilePicture getProfilePictureByUserId(String user) {
        return null;
    }

    @Override
    public Set<Certification> getAllCertificationsByConsultantId(UUID userId) {
        return null;
    }

    @Override
    public ProfilePicture getProfilePictureByProfilePictureId(String id) {
        return null;
    }

    @Override
    public Certification getCertificationById(String id) {
        return null;
    }
}
