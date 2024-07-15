package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.enums.CertificateType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;

import java.util.Set;
import java.util.UUID;


public interface FileService {

    public void storeProfilePicture(UUID userId, String pictureUrl);
    public void storeCertificate(UUID userId, CertificateType type, String certificateUrl);
    public Certification getCertificationByConsultant(Consultant consultant, CertificateType type);
    public ProfilePicture getProfilePictureByUserId(String user);
    public Set<Certification> getAllCertificationsByConsultantId(UUID userId);
    public ProfilePicture getProfilePictureByProfilePictureId(String id);
    public Certification getCertificationById(String id);


}
