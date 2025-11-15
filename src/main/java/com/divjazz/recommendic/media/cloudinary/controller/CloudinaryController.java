package com.divjazz.recommendic.media.cloudinary.controller;

import com.divjazz.recommendic.media.cloudinary.controller.payload.UploadSignature;
import com.divjazz.recommendic.media.cloudinary.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {


    private final CloudinaryService cloudinaryService;

    @GetMapping("/signature")
    public UploadSignature getUploadSignature() {
        return cloudinaryService.generateUploadSignature();
    }


}
