package com.divjazz.recommendic.media.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.divjazz.recommendic.media.cloudinary.controller.payload.UploadSignature;
import com.divjazz.recommendic.security.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final AuthUtils authUtils;

    public UploadSignature generateUploadSignature () {
        var user = authUtils.getCurrentUser();

        long timeStamp = System.currentTimeMillis() / 1000;

        String publicId = "users/%s/profile_%s".formatted(user.userId(), UUID.randomUUID());
        String folder = "users/%s".formatted(user.userId());
        Map<String, Object> params = Map.of(
                "timestamp", timeStamp,
                "public_id", publicId,
                "folder", folder
        );

        String signature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);

        return new UploadSignature(timeStamp,signature,cloudinary.config.apiKey, publicId, cloudinary.config.cloudName, folder);
    }
    public Set<UploadSignature> generateUploadSignatures (Integer count) {
        var user = authUtils.getCurrentUser();

        return IntStream.range(0, count).mapToObj(value -> {
            long timeStamp = System.currentTimeMillis()/1000;
            String publicId = "users/%s/profile_%s".formatted(user.userId(), UUID.randomUUID());
            String folder = "users/%s".formatted(user.userId());
            Map<String, Object> params = Map.of(
                    "timestamp", timeStamp,
                    "public_id", publicId,
                    "folder", folder
            );

            String signature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);

            return new UploadSignature(timeStamp,signature,cloudinary.config.apiKey, publicId, cloudinary.config.cloudName, folder);
        }).collect(Collectors.toSet());


    }

}
