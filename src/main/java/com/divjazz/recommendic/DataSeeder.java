package com.divjazz.recommendic;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Profile({"seed"})
@Component
public class DataSeeder implements ApplicationRunner {

    private final PatientRepository patientRepository;
    private final ConsultantRepository consultantRepository;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();

    public DataSeeder(PatientRepository patientRepository, ConsultantRepository consultantRepository,@Qualifier("seedPasswordEncoder") PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.consultantRepository = consultantRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(ApplicationArguments args)  {

            CompletableFuture.runAsync(() -> {
                for (int i = 0; i < 100000; i++) {
                    UserName userName = new UserName(faker.name().firstName(), faker.name().lastName());
                    Address address = new Address(faker.address().city(), faker.address().state(), faker.address().country());
                    String password = passwordEncoder.encode(faker.lorem().characters(12, true, true, true));
                    UserCredential userCredential = new UserCredential(password);
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
                    patientRepository.save(patient);
                }
            });
            CompletableFuture.runAsync(() -> {
                for (int i = 0; i < 100000; i++) {
                    UserName userName1 = new UserName(faker.name().firstName(), faker.name().lastName());
                    Address address1 = new Address(faker.address().city(), faker.address().state(), faker.address().country());
                    String password1 = passwordEncoder.encode(faker.lorem().characters(12, true, true, true));
                    UserCredential userCredential1 = new UserCredential(password1);

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

                    consultantRepository.save(consultant);
                }
            });

    }
}
