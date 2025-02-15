package com.divjazz.recommendic.unit.user.model;


import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

public class UserTest {


    @ParameterizedTest
    @MethodSource
    public void shouldCreateNewUserObjectWhenConstructorIsCalled(
            UserName userName,
            String email,
            String phoneNumber,
            Gender gender,
            Address address,
            Role role,
            UserCredential userCredential
    ){
        var user = new Patient(userName, email, phoneNumber, gender, address, role, userCredential);
        assertInstanceOf(User.class, user);
        assertFalse(user.isEnabled());
    }
    private static Stream<Arguments> shouldCreateNewUserObjectWhenConstructorIsCalled(){
        return Stream.of(
                Arguments.of(new UserName("Divjazz", "Maduks"),
                        "maduks@test.com",
                        "1234567890",
                        Gender.MALE,
                        new Address("123456", "test","state","country"),
                        new Role("ROLE_ADMIN", "ADMIN",2L)
                ),
                Arguments.of(new UserName("Victory", "Boland"),
                        "boland@test.com",
                        "1234567890",
                        Gender.MALE,
                        new Address("123456", "test","state","country"),
                        new Role("ROLE_CONSULTANT", "CONSULTANT",2L)
                ));
    }
}
