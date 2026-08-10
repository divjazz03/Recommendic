package com.divjazz.recommendic.global.general;

import java.time.LocalDate;

public class DateUtils {

    public static String toAge(LocalDate dateOfBirth) {
        var yearOfBirth = dateOfBirth.getYear();
        var thisYear = LocalDate.now().getYear();

        return String.valueOf(thisYear - yearOfBirth);
    }
}
