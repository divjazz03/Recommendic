package com.divjazz.recommendic.fileupload.config;

import com.divjazz.recommendic.fileupload.service.CloudFileUploadService;
import com.divjazz.recommendic.fileupload.service.DatabaseFileUploadService;
import com.divjazz.recommendic.fileupload.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class FileUploadConfig {

    @Value("${file.upload.implementation}")
    private static String fileUploadImplementation;
    @Bean
    FileService fileService() {
        if (Objects.isNull(fileUploadImplementation))
            return new DatabaseFileUploadService();
       if (fileUploadImplementation.equals("databaseFileUploadService")) {
           return new DatabaseFileUploadService();
       } else if (fileUploadImplementation.equals("cloudFileUploadService")) {
           return new CloudFileUploadService();
       } else {
           return new DatabaseFileUploadService();
       }
    }
}
