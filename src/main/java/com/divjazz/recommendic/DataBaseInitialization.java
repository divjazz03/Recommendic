package com.divjazz.recommendic;

import com.divjazz.recommendic.user.controller.admin.AdminCredentialResponse;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.dto.PatientDTO;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.*;

import com.divjazz.recommendic.user.service.AdminService;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.PatientService;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.apache.commons.lang3.StringUtils;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Component
@Profile("dev")
public class DataBaseInitialization implements ApplicationRunner {

    private final Faker faker = new Faker();

    private final static UUID ROOT_ADMIN_ID = UUID.randomUUID();
    private final PatientService patientService;
    private final ConsultantService consultantService;

    private final AdminService adminService;
    String[] medicalCategories = Arrays.stream(MedicalCategory.values())
            .map(Enum::name)
            .toArray(String[]::new);

    public DataBaseInitialization(PatientService patientService, ConsultantService consultantService, AdminService service) {
        this.adminService = service;
        this.patientService = patientService;
        this.consultantService = consultantService;

    }

    private AdminCredentialResponse generateDefaultAdmin(){
        AdminDTO adminDTO = new AdminDTO(
                new UserName("Maduka", "Akachukwu"),
                "divjazz20@gmail.com",
                "07058695592",
                Gender.MALE,
                new Address("2003940","Ibadan","Oyo","Nigeria")
        );
        return adminService.createAdmin(adminDTO, ROOT_ADMIN_ID);
    }

    private PatientDTO generatePatient() {
        Name name = faker.name();
        UserName userName = new UserName(name.firstName(), name.lastName());
        String email = faker.internet().emailAddress(generateEmailLocalPart(userName));
        String phoneNumber = faker.phoneNumber().phoneNumber();
        Gender gender = faker.bool().bool() ? Gender.MALE : Gender.FEMALE;
        Address address = new Address(
                faker.address().zipCode(),
                faker.address().city(),
                faker.address().state(),
                faker.address().country()
        );
        String password = faker.internet().password();

        Random randomNumber = new Random();
        return new PatientDTO(userName,
                email,
                phoneNumber,
                gender,
                address,
                password,
                Arrays.copyOf(medicalCategories, randomNumber.nextInt(1, medicalCategories.length - 1) ));
    }
    private ConsultantDTO generateUnverifiedConsultant(){
        Name name = faker.name();
        UserName userName = new UserName(name.firstName(), name.lastName());
        String email = faker.internet().emailAddress(generateEmailLocalPart(userName));
        String number = faker.phoneNumber().phoneNumber();
        Gender gender = faker.bool().bool()? Gender.FEMALE: Gender.MALE;
        Random random = new Random();
        MedicalCategory medicalCategory = MedicalCategory.valueOf(medicalCategories[random.nextInt(medicalCategories.length - 1)].toUpperCase(Locale.ROOT));
        Address address = new Address(
                faker.address().zipCode(),
                faker.address().city(),
                faker.address().state(),
                faker.address().country()
        );
        String password = faker.internet().password();
        return new ConsultantDTO(userName,email,number,gender,address, password, medicalCategory);

    }

    private String generateEmailLocalPart(UserName userName) {
        return String.format("%s.%s", StringUtils.remove(userName.getFirstName().toLowerCase(), "'"),
                StringUtils.remove(userName.getLastName().toLowerCase(), "'"));
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {

        RequestContext.setUserId(ROOT_ADMIN_ID);
        var admin = generateDefaultAdmin();
        for (int i = 0; i < 30; i++) {
            PatientDTO patientDTO = generatePatient();
            ConsultantDTO consultantDTO = generateUnverifiedConsultant();
            patientService.createPatient(patientDTO);
            consultantService.createConsultant(consultantDTO);

        }

    }
}
