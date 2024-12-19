package com.divjazz.recommendic.chat.model;

import com.divjazz.recommendic.Auditable;
import jakarta.persistence.Entity;

@Entity
public class OfflineMessage extends Auditable {
    private String senderId;
    private String receiverId;
    private String content;
    private String timeStamp;
    private boolean viewed;

    protected OfflineMessage () {
    }

    public OfflineMessage(String senderId, String receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timeStamp = String.valueOf(System.currentTimeMillis());
    }

    public String getSenderId() {
        return senderId;
    }


    public String getReceiverId() {
        return receiverId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }
}
