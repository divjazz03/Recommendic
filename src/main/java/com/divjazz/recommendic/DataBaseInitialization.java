package com.divjazz.recommendic;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("init-db")
public class DataBaseInitialization implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {

    }


}
