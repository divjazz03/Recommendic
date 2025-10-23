package com.divjazz.recommendic.user.transformer;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.repository.projection.ConsultantEducationProjection;
import com.divjazz.recommendic.user.repository.projection.ConsultantProfileProjection;
import com.divjazz.recommendic.user.repository.projection.MedicalCategoryProjection;
import com.divjazz.recommendic.user.repository.projection.PatientProfileProjection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
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
        int experience = 0;
        String[] languages = null;
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
                bio = rs.getString("bio");

                populateEducation(rs, consultantEducationProjections);
            } else {
                populateEducation(rs, consultantEducationProjections);
            }

        }

        UserName finalUserName = userName;
        String finalEmail = email;
        String finalPhoneNumber = phoneNumber;
        LocalDate finalDateOfBirth = dateOfBirth;
        Gender finalGender = gender;
        Address finalAddress = address;
        ProfilePicture finalProfilePicture = profilePicture;
        MedicalCategoryProjection finalMedicalCategoryProjection = medicalCategoryProjection;

        int finalExperience = experience;
        String[] finalLanguages = languages;
        String finalBio = bio;
        String finalLocation = location;
        return Optional.of(
                new ConsultantProfileProjection() {
                    @Override
                    public UserName getUserName() {
                        return finalUserName;
                    }

                    @Override
                    public String getEmail() {
                        return finalEmail;
                    }

                    @Override
                    public String getPhoneNumber() {
                        return finalPhoneNumber;
                    }

                    @Override
                    public LocalDate getDateOfBirth() {
                        return finalDateOfBirth;
                    }

                    @Override
                    public Gender getGender() {
                        return finalGender;
                    }

                    @Override
                    public String getLocation() {
                        return finalLocation;
                    }

                    @Override
                    public Address getAddress() {
                        return finalAddress;
                    }

                    @Override
                    public MedicalCategoryProjection getSpecialty() {
                        return finalMedicalCategoryProjection;
                    }

                    @Override
                    public int getExperience() {
                        return finalExperience;
                    }

                    @Override
                    public String[] getLanguages() {
                        return finalLanguages;
                    }

                    @Override
                    public String getBio() {
                        return finalBio;
                    }

                    @Override
                    public Set<ConsultantEducationProjection> getEducations() {
                        return consultantEducationProjections;
                    }

                    @Override
                    public ProfilePicture getProfilePicture() {
                        return finalProfilePicture;
                    }
                });
    }

    private static void populateEducation(ResultSet rs, Set<ConsultantEducationProjection> consultantEducationProjections) throws SQLException {
        var educationYear = rs.getInt("educationYear");
        var educationInstitution = rs.getString("educationInstitution");
        var educationDegree = rs.getString("educationDegree");
        var educationId = rs.getString("educationId");

        if (Objects.nonNull(educationId)) {
            consultantEducationProjections.add(new ConsultantEducationProjection() {
                @Override
                public String getDegree() {
                    return educationDegree;
                }

                @Override
                public String getInstitution() {
                    return educationInstitution;
                }

                @Override
                public int getYear() {
                    return educationYear;
                }
            });
        }
    }
}
