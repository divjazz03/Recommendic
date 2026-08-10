package com.divjazz.recommendic.consultation.controller.payload;

public record JoinResponse(String apiKey, String token, String callId, User user) {

    public record User (String id, String name){}
}
