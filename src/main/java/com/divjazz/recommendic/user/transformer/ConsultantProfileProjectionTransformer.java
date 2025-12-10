package com.divjazz.recommendic.user.transformer;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.repository.projection.ConsultantEducationProjection;
import com.divjazz.recommendic.user.repository.projection.ConsultantProfileProjection;
import com.divjazz.recommendic.user.repository.projection.MedicalCategoryProjection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ConsultantProfileProjectionTransformer {

    private final ObjectMapper objectMapper;

    public Optional<ConsultantProfileProjection> transform(ResultSet rs) throws SQLException, JsonProcessingException {
        String userId = null;
        UserName userName = null;
        String email = null;
        String phoneNumber = null;
        LocalDate dateOfBirth = null;
        Gender gender = null;
        Address address = null;
        Set<ConsultantEducationProjection> consultantEducationProjections = new HashSet<>();
        ProfilePicture profilePicture = null;
        MedicalCategoryProjection medicalCategoryProjection = null;
        String location = null;
        String certifications= null;
        int experience = 0;
        String[] languages = null;
        String licenseNumber= null;
        String[] subSpecialties = null;
        String bio = null;

        while (rs.next()) {
            if (Objects.isNull(userId)) {
                userId = rs.getString("userId");
                if (userId == null) {
                    return Optional.empty();
                }
                var userNameString = rs.getString("userName");
                if (Objects.nonNull(userNameString)) {
                    userName = objectMapper.readValue(userNameString, UserName.class);
                }
                email = rs.getString("email");
                phoneNumber = rs.getString("phoneNumber");
                var sqlDateOfBirth = rs.getDate("dateOfBirth");
                if (Objects.nonNull(sqlDateOfBirth)) {
                    dateOfBirth = sqlDateOfBirth.toLocalDate();
                }
                gender = Gender.valueOf(rs.getString("gender"));
                experience = rs.getInt("experience");
                location = rs.getString("location");
                licenseNumber = rs.getString("licenseNumber");
                certifications = rs.getString("certifications");

                var specialtyDesc = rs.getString("specialtyDesc");
                var specialtyName = rs.getString("specialtyName");
                medicalCategoryProjection = new MedicalCategoryProjection(specialtyName, specialtyDesc);

                var addressString = rs.getString("address");
                if (Objects.nonNull(addressString)) {
                    address = objectMapper.readValue(addressString, Address.class);
                }

                var profilePictureString = rs.getString("profilePicture");
                if (Objects.nonNull(profilePictureString)) {
                    profilePicture = objectMapper.readValue(profilePictureString, ProfilePicture.class);
                }
                var languagesSqlArray = rs.getArray("languages");
                languages = Objects.nonNull(languagesSqlArray) ? (String[]) languagesSqlArray.getArray(): new String[0];
                var subSpecialtySqlArray = rs.getArray("subSpecialties");
                subSpecialties = Objects.nonNull(subSpecialtySqlArray) ? (String[]) subSpecialtySqlArray.getArray(): new String[0];
                bio = rs.getString("bio");

                populateEducation(rs, consultantEducationProjections);
            } else {
                populateEducation(rs, consultantEducationProjections);
            }

        }

        final UserName finalUserName = userName;
        final String finalEmail = email;
        final String finalPhoneNumber = phoneNumber;
        final LocalDate finalDateOfBirth = dateOfBirth;
        final Gender finalGender = gender;
        final Address finalAddress = address;
        final String finalCertifications = certifications;
        final ProfilePicture finalProfilePicture = profilePicture;
        final MedicalCategoryProjection finalMedicalCategoryProjection = medicalCategoryProjection;

        int finalExperience = experience;
        String[] finalLanguages = languages.clone();
        String finalBio = bio;
        String finalLocation = location;
        String finalLicenseNumber = licenseNumber;
        String[] finalSubSpecialties = subSpecialties.clone();
        return Optional.of(new ConsultantProfileProjection(
                finalUserName,
                finalEmail,
                finalPhoneNumber,
                finalDateOfBirth,
                finalGender,
                finalLocation,
                finalAddress,
                finalMedicalCategoryProjection,
                finalExperience,
                finalLanguages,
                finalBio,
                consultantEducationProjections,
                finalProfilePicture,
                finalSubSpecialties,
                finalLicenseNumber,
                finalCertifications
        ));
    }

    private static void populateEducation(ResultSet rs, Set<ConsultantEducationProjection> consultantEducationProjections) throws SQLException {
        var educationYear = rs.getInt("educationYear");
        var educationInstitution = rs.getString("educationInstitution");
        var educationDegree = rs.getString("educationDegree");
        var educationId = rs.getString("educationId");

        if (Objects.nonNull(educationId)) {
            consultantEducationProjections.add(new ConsultantEducationProjection(
                    educationDegree,
                    educationInstitution,
                    educationYear
            ) );
        }
    }
}
