package com.divjazz.recommendic;

import com.divjazz.recommendic.user.dto.PatientDTO;

import com.divjazz.recommendic.user.model.userAttributes.*;

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

    public DataBaseInitialization(PatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i < 100; i++) {
            PatientDTO patientDTO = generatePatient();
            patientService.createPatient(patientDTO);
        }
    }

    private PatientDTO generatePatient() {
        Name name = faker.name();
        UserName userName = new UserName(name.firstName(), name.lastName());
        Email email = new Email(faker.internet().emailAddress(generateEmailLocalPart(userName)));
        PhoneNumber phoneNumber = new PhoneNumber(faker.phoneNumber().phoneNumber());
        Gender gender = faker.bool().bool() ? Gender.MALE : Gender.FEMALE;
        Address address = new Address(
                faker.address().zipCode(),
                faker.address().city(),
                faker.address().state(),
                faker.address().country());
        return new PatientDTO(userName, email, phoneNumber, gender, address);
    }

    private String generateEmailLocalPart(UserName userName) {
        return String.format("%s.%s", StringUtils.remove(userName.getFirstName().toLowerCase(), "'"),
                StringUtils.remove(userName.getLastName().toLowerCase(), "'"));
    }


}
