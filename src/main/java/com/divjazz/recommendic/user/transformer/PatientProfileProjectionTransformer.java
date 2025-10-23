package com.divjazz.recommendic.user.transformer;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.repository.projection.MedicalCategoryProjection;
import com.divjazz.recommendic.user.repository.projection.PatientProfileProjection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PatientProfileProjectionTransformer {

    private final ObjectMapper objectMapper;

    public Optional<PatientProfileProjection> transform(ResultSet rs) throws SQLException, JsonProcessingException {
        String userId = null;
        UserName userName = null;
        String email = null;
        String phoneNumber = null;
        LocalDate dateOfBirth = null;
        Gender gender = null;
        Address address = null;
        Set<MedicalCategoryProjection> medicalCategoryProjections = new HashSet<>();
        ProfilePicture profilePicture = null;

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
                var addressString = rs.getString("address");
                if (Objects.nonNull(addressString)) {
                    address = objectMapper.readValue(addressString, Address.class);
                }
                var profilePictureString = rs.getString("profilePicture");
                if (Objects.nonNull(profilePictureString)) {
                    profilePicture = objectMapper.readValue(profilePictureString, ProfilePicture.class);
                }
                var medicalCategoryName = rs.getString("medicalCategoryName");
                var medicalCategoryDesc = rs.getString("medicalDesc");
                if (Objects.nonNull(medicalCategoryName)) {
                    medicalCategoryProjections.add(new MedicalCategoryProjection(
                            medicalCategoryName,
                            medicalCategoryDesc
                    ));
                }
            } else {
                var medicalCategoryName = rs.getString("medicalCategoryName");
                var medicalCategoryDesc = rs.getString("medicalDesc");
                if (Objects.nonNull(medicalCategoryName)) {
                    medicalCategoryProjections.add(new MedicalCategoryProjection(
                            medicalCategoryName,
                            medicalCategoryDesc
                    ));
                }
            }
        }

        UserName finalUserName = userName;
        String finalEmail = email;
        String finalPhoneNumber = phoneNumber;
        LocalDate finalDateOfBirth = dateOfBirth;
        Gender finalGender = gender;
        Address finalAddress = address;
        ProfilePicture finalProfilePicture = profilePicture;

        return Optional.of(
                new PatientProfileProjection() {
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
                    public Address getAddress() {
                        return finalAddress;
                    }

                    @Override
                    public Set<MedicalCategoryProjection> getMedicalCategories() {
                        return medicalCategoryProjections;
                    }

                    @Override
                    public ProfilePicture getProfilePicture() {
                        return finalProfilePicture;
                    }
                });
    }
}
