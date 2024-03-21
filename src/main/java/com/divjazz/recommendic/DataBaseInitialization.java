package com.divjazz.recommendic;

import com.divjazz.recommendic.user.dto.AdminDTO;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.dto.PatientDTO;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.*;

import com.divjazz.recommendic.user.service.AdminService;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.PatientService;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.apache.commons.lang3.StringUtils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("init-db")
public class DataBaseInitialization implements CommandLineRunner {

    private final Faker faker = new Faker();
    private final PatientService patientService;
    private final ConsultantService consultantService;

    private final AdminService adminService;

    public DataBaseInitialization(PatientService patientService, ConsultantService consultantService, AdminService service) {
        this.adminService = service;
        this.patientService = patientService;
        this.consultantService = consultantService;
    }

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i < 30; i++) {
            PatientDTO patientDTO = generatePatient();
            ConsultantDTO consultantDTO = generateUnverifiedConsultant();
            patientService.createPatient(patientDTO);
            consultantService.createConsultant(consultantDTO);

        }
        generateDefaultAdmin();
    }
    private void generateDefaultAdmin(){
        AdminDTO adminDTO = new AdminDTO(
                new UserName("Maduka", "Akachukwu"),
                "divjazz20@gmail.com",
                "07058695592",
                Gender.MALE,
                new Address("2003940","Ibadan","Oyo","Nigeria")
        );
        System.out.println(adminService.createAdmin(adminDTO));
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
        return new PatientDTO(userName, email, phoneNumber, gender, address,password);
    }
    private ConsultantDTO generateUnverifiedConsultant(){
        Name name = faker.name();
        UserName userName = new UserName(name.firstName(), name.lastName());
        String email = faker.internet().emailAddress(generateEmailLocalPart(userName));
        String number = faker.phoneNumber().phoneNumber();
        Gender gender = faker.bool().bool()? Gender.FEMALE: Gender.MALE;
        Address address = new Address(
                faker.address().zipCode(),
                faker.address().city(),
                faker.address().state(),
                faker.address().country()
        );
        String password = faker.internet().password();
        return new ConsultantDTO(userName,email,number,gender,address, password);

    }

    private String generateEmailLocalPart(UserName userName) {
        return String.format("%s.%s", StringUtils.remove(userName.getFirstName().toLowerCase(), "'"),
                StringUtils.remove(userName.getLastName().toLowerCase(), "'"));
    }


}
