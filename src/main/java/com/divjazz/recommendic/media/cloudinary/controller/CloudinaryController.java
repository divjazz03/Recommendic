package com.divjazz.recommendic.media.cloudinary.controller;

import com.divjazz.recommendic.media.cloudinary.controller.payload.UploadSignature;
import com.divjazz.recommendic.media.cloudinary.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {


    private final CloudinaryService cloudinaryService;

    @GetMapping("/signature")
    public Set<UploadSignature> getUploadSignature(@RequestParam(value = "count",required = false) Integer count) {
        if(Objects.isNull(count)) {
            return Set.of(cloudinaryService.generateUploadSignature());
        }

        return cloudinaryService.generateUploadSignatures(count);
    }


}
