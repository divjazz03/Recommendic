package com.divjazz.recommendic.chat.dto;

public class ChatMessage {

    private final String senderId;
    private final String receiverId;
    private final String content;
    private final String timeStamp;



    public ChatMessage(String senderId, String receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        timeStamp = String.valueOf(System.currentTimeMillis());
    }

    public String getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getReceiverId() {
        return receiverId;
    }
}
