package com.divjazz.recommendic.media.cloudinary.controller.payload;

public record UploadSignature(
        long timeStamp,
        String signature,
        String apiKey,
        String publicId,
        String cloudName,
        String folder
) {
}
