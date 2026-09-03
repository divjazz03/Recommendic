package com.divjazz.recommendic.global;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SqlQueryLoader {

    public String load(String path) {
        try {
            Resource resource = new ClassPathResource("db/queries/" + path);

            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load SQL file: " + path);
        }
    }
}
