package com.divjazz.recommendic.fileupload.service;

import com.divjazz.recommendic.user.enums.CertificateType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;

import java.util.Set;
import java.util.UUID;


public interface FileService {

    void storeProfilePicture(UUID userId, String pictureUrl);
    void storeCertificate(UUID userId, CertificateType type, String certificateUrl);
    Certification getCertificationByConsultant(Consultant consultant, CertificateType type);
    ProfilePicture getProfilePictureByUserId(String user);
    Set<Certification> getAllCertificationsByConsultantId(UUID userId);
    ProfilePicture getProfilePictureByProfilePictureId(String id);
    Certification getCertificationById(String id);


}
