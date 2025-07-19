package com.divjazz.recommendic;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Profile({"seed"})
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    public static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    public static final int BATCH_SIZE = 1000;
    private final JdbcClient jdbcClient;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();
    private final ObjectMapper objectMapper;

    private final Random random = new Random();

    public static Patient generateFakePatient(Faker faker, int i) {
        String password = faker.lorem().characters(12, true, true, true);
        UserCredential userCredential = new UserCredential(password);

        Patient patient = new Patient(
                faker.internet().emailAddress().replace("@", i + "@"),
                Gender.FEMALE,
                userCredential
        );
        patient.setUserStage(UserStage.ACTIVE_USER);
        return patient;
    }

    public static PatientProfile generateFakePatientProfile(Faker faker, Patient patient) {
        UserName userName = new UserName(faker.name().firstName(), faker.name().lastName());
        Address address = new Address(faker.address().city(), faker.address().state(), faker.address().country());
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setName("User Avatar");
        profilePicture.setPictureUrl(faker.avatar().image());
        var phoneNumber = faker.phoneNumber().phoneNumber();

        return PatientProfile.builder()
                .userName(userName)
                .address(address)
                .profilePicture(profilePicture)
                .patient(patient)
                .phoneNumber(phoneNumber)
                .build();
    }

    public static Consultant generateFakeConsultant(Faker faker, int i) {

        String password1 = faker.lorem().characters(12, true, true, true);
        UserCredential userCredential1 = new UserCredential(password1);

        Consultant consultant = new Consultant(
                faker.internet().emailAddress().replace("@", i + "@"),
                Gender.MALE,
                userCredential1
        );
        consultant.setUserStage(UserStage.ACTIVE_USER);
        return consultant;
    }

    public static ConsultantProfile generateFakeConsultantProfile(Faker faker, Consultant consultant) {
        UserName userName = new UserName(faker.name().firstName(), faker.name().lastName());
        Address address = new Address(faker.address().city(), faker.address().state(), faker.address().country());
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setName("User Avatar");
        profilePicture.setPictureUrl(faker.avatar().image());
        var phoneNumber = faker.phoneNumber().phoneNumber();

        return ConsultantProfile.builder()
                .userName(userName)
                .address(address)
                .profilePicture(profilePicture)
                .consultant(consultant)
                .phoneNumber(phoneNumber)
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .build();
    }

    public static PGobject asJsonb(String value) {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        try {
            jsonObject.setValue(value);
            return jsonObject;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(ApplicationArguments args) {


        CompletableFuture<Void> patientTask = CompletableFuture.runAsync(() -> {
            List<Object[]> patients = new ArrayList<>(BATCH_SIZE);
            List<Object[]> patientProfiles = new ArrayList<>(BATCH_SIZE);
            log.info("Inside async function");
            for (int i = 0; i < 1000000; i++) {
                var patient = generateFakePatient(faker, i);
                var patientProfile = generateFakePatientProfile(faker, patient);
                String usernameObj;
                String addressObj;
                String profilePic;
                String userCredential;
                try {
                    usernameObj = objectMapper.writeValueAsString(Map.of(
                            "first_name", patientProfile.getUserName().getFirstName(),
                            "last_name", patientProfile.getUserName().getLastName(),
                            "full_name", patientProfile.getUserName().getFullName()
                    ));
                    addressObj = objectMapper.writeValueAsString(Map.of(
                            "city", patientProfile.getAddress().getCity(),
                            "state", patientProfile.getAddress().getState(),
                            "country", patientProfile.getAddress().getCountry()
                    ));
                    profilePic = objectMapper.writeValueAsString(Map.of(
                            "name", patientProfile.getProfilePicture().getName(),
                            "state", patientProfile.getProfilePicture().getPictureUrl()
                    ));
                    userCredential = objectMapper.writeValueAsString(Map.of(
                            "password", patient.getUserCredential().getPassword(),
                            "expiry", patient.getUserCredential().getExpiry(),
                            "last_modified", patient.getUserCredential().getLastModified()
                    ));

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                patients.add(new Object[]{
                        patient.getUserId(),
                        patient.getEmail(),
                        patient.getUserType().name(),
                        patient.getUserStage().name(),
                        patient.isEnabled(),
                        patient.isAccountNonExpired(),
                        patient.isAccountNonLocked(),
                        patient.getGender().name(),
                        patient.getRole().getName(),
                        "SYSTEM",
                        "SYSTEM",
                        patient.getUserStage() == UserStage.ONBOARDING ? null :
                                new String[]{MedicalCategoryEnum.OPHTHALMOLOGY.getValue()},
                        asJsonb(userCredential)
                });
                patientProfiles.add(new Object[]{
                        patientProfile.getPatient().getUserId(),
                        asJsonb(profilePic),
                        asJsonb(addressObj),
                        patientProfile.getPhoneNumber(),
                        asJsonb(usernameObj),
                        "SYSTEM",
                        "SYSTEM"
                });

                if (i % BATCH_SIZE == 0 && !patients.isEmpty()) {
                    StringBuilder sqlBuilder = new StringBuilder("""
                                    INSERT INTO patient(user_id, email,user_type, user_stage,enabled, account_non_expired,account_non_locked, gender, role, created_by, updated_by, medical_categories, user_credential)
                            VALUES""").append(" ");
                    List<Object> flatParams = new ArrayList<>();
                    for (int j = 0; j < patients.size(); j++) {
                        sqlBuilder.append("(?,?,?,?,?,?,?,?,?,?,?,?,?)");
                        if (j < patientProfiles.size() - 1) sqlBuilder.append(", ");

                        Object[] row = patients.get(j);
                        Collections.addAll(flatParams, row);
                    }
                    sqlBuilder.append(" RETURNING id, user_id");

                    Map<String, Long> returned = jdbcTemplate.query(
                            sqlBuilder.toString(),
                            rs -> {
                                Map<String, Long> map = new HashMap<>();
                                while (rs.next()) {
                                    map.put(rs.getString("user_id"), rs.getLong("id"));
                                }
                                return map;
                            },
                            flatParams.toArray()
                    );
                    patients.clear();
                    List<Object[]> remappedProfiles = new ArrayList<>(patientProfiles.size());
                    for (Object[] profileRow : patientProfiles) {
                        String userId = (String) profileRow[0];
                        Long generatedId = returned.get(userId);
                        if (generatedId == null) {
                            throw new IllegalStateException("No generated ID for userId: %s".formatted(userId));
                        }

                        remappedProfiles.add(new Object[]{
                                generatedId,
                                profileRow[1],
                                profileRow[2],
                                profileRow[3],
                                profileRow[4],
                                profileRow[5],
                                profileRow[6]
                        });

                    }
                    jdbcTemplate.batchUpdate(
                            """
                                    INSERT INTO patient_profiles(id, profile_picture, address, phone_number, username, created_by, updated_by)
                                    VALUES (?,?,?,?,?,?,?)
                                    """,
                            remappedProfiles
                    );
                    patientProfiles.clear();
                }
            }
        });


        CompletableFuture<Void> consultantTask = CompletableFuture.runAsync(() -> {
            List<Object[]> consultants = new ArrayList<>(BATCH_SIZE);
            List<Object[]> consultantProfiles = new ArrayList<>(BATCH_SIZE);
            for (int i = 0; i < 1000000; i++) {
                var consultant = generateFakeConsultant(faker, i);
                var consultantProfile = generateFakeConsultantProfile(faker, consultant);
                String usernameObj;
                String addressObj;
                String profilePic;
                String userCredential;
                try {
                    usernameObj = objectMapper.writeValueAsString(Map.of(
                            "first_name", consultantProfile.getUserName().getFirstName(),
                            "last_name", consultantProfile.getUserName().getLastName(),
                            "full_name", consultantProfile.getUserName().getFullName()
                    ));
                    addressObj = objectMapper.writeValueAsString(Map.of(
                            "city", consultantProfile.getAddress().getCity(),
                            "state", consultantProfile.getAddress().getState(),
                            "country", consultantProfile.getAddress().getCountry()
                    ));
                    profilePic = objectMapper.writeValueAsString(Map.of(
                            "name", consultantProfile.getProfilePicture().getName(),
                            "state", consultantProfile.getProfilePicture().getPictureUrl()
                    ));
                    userCredential = objectMapper.writeValueAsString(Map.of(
                            "password", consultant.getUserCredential().getPassword(),
                            "expiry", consultant.getUserCredential().getExpiry(),
                            "last_modified", consultant.getUserCredential().getLastModified()
                    ));

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                consultants.add(new Object[]{
                        consultant.getUserId(),
                        consultant.getEmail(),
                        consultant.getUserType().name(),
                        consultant.getUserStage().name(),
                        consultant.isEnabled(),
                        consultant.isAccountNonExpired(),
                        consultant.isAccountNonLocked(),
                        consultant.getGender().name(),
                        consultant.getRole().name(),
                        "SYSTEM",
                        "SYSTEM",
                        MedicalCategoryEnum.CARDIOLOGY.getValue(),
                        asJsonb(userCredential)
                });
                consultantProfiles.add(new Object[]{
                        consultant.getUserId(),
                        asJsonb(profilePic),
                        asJsonb(addressObj),
                        faker.lorem().paragraph(30),
                        consultantProfile.getPhoneNumber(),
                        asJsonb(usernameObj),
                        consultantProfile.getLocationOfInstitution(),
                        random.nextInt(10),
                        consultantProfile.getTitle(),
                        new String[]{"English"},
                        "SYSTEM",
                        "SYSTEM"
                });

                if (i % BATCH_SIZE == 0) {
                    StringBuilder sqlBuilder = new StringBuilder("""
                                    INSERT INTO consultant(user_id,email,user_type, user_stage,enabled, account_non_expired,account_non_locked, gender, role, created_by, updated_by, specialization, user_credential)
                                    VALUES""").append(" ");
                    List<Object> flatParams = new ArrayList<>();
                    for (int j = 0; j < consultants.size(); j++) {
                        sqlBuilder.append("(?,?,?,?,?,?,?,?,?,?,?,?,?)");
                        if (j < consultants.size() - 1) sqlBuilder.append(", ");

                        Object[] row = consultants.get(j);
                        Collections.addAll(flatParams, row);
                    }
                    sqlBuilder.append(" RETURNING id, user_id");

                    Map<String, Long> returned = jdbcTemplate.query(
                            sqlBuilder.toString(),
                            rs -> {
                                Map<String, Long> map = new HashMap<>();
                                while (rs.next()) {
                                    map.put(rs.getString("user_id"), rs.getLong("id"));
                                }
                                return map;
                            },
                            flatParams.toArray()
                    );
                    consultants.clear();
                    List<Object[]> remappedProfiles = new ArrayList<>(consultantProfiles.size());
                    for (Object[] profileRow : consultantProfiles) {
                        String userId = (String) profileRow[0];
                        Long generatedId = returned.get(userId);
                        if (generatedId == null) {
                            throw new IllegalStateException("No generated ID for userId: %s".formatted(userId));
                        }

                        remappedProfiles.add(new Object[]{
                                generatedId,
                                profileRow[1],
                                profileRow[2],
                                profileRow[3],
                                profileRow[4],
                                profileRow[5],
                                profileRow[6],
                                profileRow[7],
                                profileRow[8],
                                profileRow[9],
                                profileRow[10],
                                profileRow[11]
                        });

                    }
                    jdbcTemplate.batchUpdate(
                            """
                                    INSERT INTO consultant_profiles(id, profile_picture, address, bio, phone_number, username, location, experience, title, languages, created_by, updated_by)
                                    VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
                                    """,
                            remappedProfiles
                    );
                    consultantProfiles.clear();
                }
            }
        });

        CompletableFuture.allOf(consultantTask).join();
        log.info("Finished seeding data");

    }
}
