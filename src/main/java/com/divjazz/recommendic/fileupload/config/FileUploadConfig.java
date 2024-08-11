package com.divjazz.recommendic.fileupload.config;

import com.divjazz.recommendic.fileupload.service.CloudFileUploadService;
import com.divjazz.recommendic.fileupload.service.DatabaseFileUploadService;
import com.divjazz.recommendic.fileupload.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class FileUploadConfig {

    @Value("${file.upload.implementation}")
    private String fileUploadImplementation;

    @Bean
    FileService fileService() {
        var fileUploadImpl = Optional.ofNullable(fileUploadImplementation);
        return fileUploadImpl.filter(s -> (!s.equals("databaseFileUploadService")))
                .<FileService>map(_ -> new CloudFileUploadService())
                .orElseGet(DatabaseFileUploadService::new);
    }
}
