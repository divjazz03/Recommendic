package com.divjazz.recommendic.consultation.controller.payload;

public record StartResponse(String callId, String token, String apiKey,User user) {

    public record User(String userId, String name){}
}
