package com.divjazz.recommendic.user.transformer;

import com.divjazz.recommendic.user.enums.BloodType;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.userAttributes.*;
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

public class PatientProfileProjectionTransformer {

    public static Optional<PatientProfileProjection> transform(ResultSet rs, ObjectMapper objectMapper) throws SQLException, JsonProcessingException {
        String userId = null;
        UserName userName = null;
        String email = null;
        String phoneNumber = null;
        LocalDate dateOfBirth = null;
        Gender gender = null;
        Address address = null;
        BloodType bloodType = null;
        MedicalHistory medicalHistory = null;
        LifeStyleInfo lifeStyleInfo = null;
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

                var bloodTypeString = rs.getString("bloodType");
                if (Objects.nonNull(bloodTypeString)) {
                    bloodType = BloodType.fromValue(bloodTypeString);
                }

                var lifeStyleInfoString = rs.getString("lifeStyleInfo");
                if (Objects.nonNull(lifeStyleInfoString)) {
                    lifeStyleInfo = objectMapper.readValue(lifeStyleInfoString, LifeStyleInfo.class);
                }

                var medicalHistoryString = rs.getString("medicalHistory");
                if (Objects.nonNull(medicalHistoryString)) {
                    medicalHistory = objectMapper.readValue(medicalHistoryString, MedicalHistory.class);
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

        final UserName finalUserName = userName;
        final String finalEmail = email;
        final String finalPhoneNumber = phoneNumber;
        final LocalDate finalDateOfBirth = dateOfBirth;
        final Gender finalGender = gender;
        final Address finalAddress = address;
        final ProfilePicture finalProfilePicture = profilePicture;
        final MedicalHistory finalMedicalHistory = medicalHistory;
        final BloodType finalBloodType = bloodType;
        final LifeStyleInfo finalLifeStyleInfo = lifeStyleInfo;
        final Set<MedicalCategoryProjection> finalMedicalCategories = Set.copyOf(medicalCategoryProjections);

        return Optional.of(new PatientProfileProjection(
                finalUserName,
                finalEmail,
                finalDateOfBirth,
                finalGender,
                finalAddress,
                finalPhoneNumber,
                finalMedicalCategories,
                finalProfilePicture,
                finalBloodType,
                finalLifeStyleInfo,
                finalMedicalHistory
        ));
    }
}
