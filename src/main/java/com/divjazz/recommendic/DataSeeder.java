package com.divjazz.recommendic;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Profile({"seed"})
@Component
public class DataSeeder implements ApplicationRunner {

    public static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();
    private final ObjectMapper objectMapper;

    public static final int BATCH_SIZE = 1000;

    public DataSeeder(@Qualifier("seedPasswordEncoder") PasswordEncoder passwordEncoder, ObjectMapper objectMapper) {
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }
    @Override
    public void run(ApplicationArguments args)  {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
        List<Object[]> patients = new ArrayList<>(BATCH_SIZE);
        List<Object[]> consultants = new ArrayList<>(BATCH_SIZE);
            CompletableFuture<Void> patientTask = CompletableFuture.runAsync(() -> {
                log.info("Inside async function");
                for (int i = 0; i < 100000; i++) {
                    var patient = generateFakePatient(faker,passwordEncoder,i);
                    String usernameObj;
                    String addressObj;
                    String profilePic;
                    String userCredential;
                    try {
                        usernameObj = objectMapper.writeValueAsString(Map.of(
                                "first_name", patient.getUserNameObject().getFirstName(),
                                "last_name", patient.getUserNameObject().getLastName(),
                                "full_name", patient.getUserNameObject().getFullName()
                        ));
                        addressObj = objectMapper.writeValueAsString(Map.of(
                                "city", patient.getAddress().getCity(),
                                "state", patient.getAddress().getState(),
                                "country", patient.getAddress().getCountry()
                        ));
                        profilePic = objectMapper.writeValueAsString(Map.of(
                                "name", patient.getProfilePicture().getName(),
                                "state", patient.getProfilePicture().getPictureUrl()
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
                            asJsonb(usernameObj),
                            patient.getEmail(),
                            patient.getPhoneNumber(),
                            asJsonb(profilePic),
                            asJsonb(addressObj),
                            patient.getUserType().name(),
                            patient.getUserStage().name(),
                            patient.isEnabled(),
                            patient.isAccountNonExpired(),
                            patient.isAccountNonLocked(),
                            patient.getGender().name(),
                            patient.getRole().getName(),
                            "SYSTEM",
                            "SYSTEM",
                            patient.getUserStage() == UserStage.ONBOARDING ? null:
                                    new String[]{MedicalCategoryEnum.OPHTHALMOLOGY.getValue()},
                            asJsonb(userCredential)
                    });

                   if (i % BATCH_SIZE == 0) {
                       jdbcTemplate.batchUpdate(
                               """
                       INSERT INTO patient_schema.patient(user_id, username, email,phone_number, profile_picture, address, user_type, user_stage,enabled, account_non_expired,account_non_locked, gender, role, created_by, updated_by, medical_categories, user_credential)
                       VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                       """
                               , patients
                       );
                       patients.clear();
                   }
                }
            });
            CompletableFuture<Void> consultantTask = CompletableFuture.runAsync(() -> {
                for (int i = 0; i < 100000; i++) {
                    var consultant = generateFakeConsultant(faker,passwordEncoder,i);
                    String usernameObj;
                    String addressObj;
                    String profilePic;
                    String userCredential;
                    try {
                        usernameObj = objectMapper.writeValueAsString(Map.of(
                                "first_name", consultant.getUserNameObject().getFirstName(),
                                "last_name", consultant.getUserNameObject().getLastName(),
                                "full_name", consultant.getUserNameObject().getFullName()
                        ));
                        addressObj = objectMapper.writeValueAsString(Map.of(
                                "city", consultant.getAddress().getCity(),
                                "state", consultant.getAddress().getState(),
                                "country", consultant.getAddress().getCountry()
                        ));
                        profilePic = objectMapper.writeValueAsString(Map.of(
                                "name", consultant.getProfilePicture().getName(),
                                "state", consultant.getProfilePicture().getPictureUrl()
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
                            asJsonb(usernameObj),
                            consultant.getEmail(),
                            consultant.getPhoneNumber(),
                            asJsonb(profilePic),
                            asJsonb(addressObj),
                            consultant.getUserType().name(),
                            consultant.getUserStage().name(),
                            consultant.isEnabled(),
                            consultant.isAccountNonExpired(),
                            consultant.isAccountNonLocked(),
                            consultant.getGender().name(),
                            consultant.getRole().name(),
                            "SYSTEM",
                            "SYSTEM",
                            consultant.getUserStage() == UserStage.ONBOARDING ? null:
                                    MedicalCategoryEnum.CARDIOLOGY.getValue(),
                            asJsonb(userCredential),
                            consultant.getBio()
                    });

                    if (i % BATCH_SIZE == 0) {
                        jdbcTemplate.batchUpdate(
                                """
                        INSERT INTO consultant(user_id, username, email,phone_number, profile_picture, address, user_type, user_stage,enabled, account_non_expired,account_non_locked, gender, role, created_by, updated_by, specialization, user_credential,bio)
                        VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                        """
                                , consultants
                        );
                        consultants.clear();
                    }
                }
            });

            CompletableFuture.allOf(patientTask,consultantTask).join();
            log.info("Finished seeding data");

    }
    private static DriverManagerDataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/recommendic");
        ds.setUsername("divjazz");
        ds.setPassword("june12003");
        return ds;
    }

    public static Patient generateFakePatient(Faker faker, PasswordEncoder passwordEncoder, int i) {
        UserName userName = new UserName(faker.name().firstName(), faker.name().lastName());
        Address address = new Address(faker.address().city(), faker.address().state(), faker.address().country());
        String password = passwordEncoder.encode(faker.lorem().characters(12, true, true, true));
        UserCredential userCredential = new UserCredential(password);
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setName("User Avatar");
        profilePicture.setPictureUrl(faker.avatar().image());
        Patient patient = new Patient(
                userName,
                faker.internet().emailAddress().replace("@", i + "@"),
                faker.phoneNumber().phoneNumber(),
                Gender.FEMALE,
                address,
                Role.PATIENT,
                userCredential
        );
        patient.setUserStage(UserStage.ACTIVE_USER);
        patient.setProfilePicture(profilePicture);
        return patient;
    }
    public static Consultant generateFakeConsultant(Faker faker, PasswordEncoder passwordEncoder, int i) {
        UserName userName1 = new UserName(faker.name().firstName(), faker.name().lastName());
        Address address1 = new Address(faker.address().city(), faker.address().state(), faker.address().country());
        String password1 = passwordEncoder.encode(faker.lorem().characters(12, true, true, true));
        UserCredential userCredential1 = new UserCredential(password1);
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setName("User Avatar");
        profilePicture.setPictureUrl(faker.avatar().image());
        Consultant consultant = new Consultant(
                userName1,
                faker.internet().emailAddress().replace("@", i + "@"),
                faker.phoneNumber().phoneNumber(),
                Gender.MALE,
                address1,
                Role.CONSULTANT,
                userCredential1
        );
        consultant.setUserStage(UserStage.ACTIVE_USER);
        consultant.setProfilePicture(profilePicture);
        return consultant;
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
}
