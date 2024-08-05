package com.divjazz.recommendic.user.model.userAttributes;


import com.divjazz.recommendic.Auditable;
import jakarta.persistence.*;


import java.util.UUID;

@Embeddable
public class ProfilePicture{
    @Column(name = "image_name", nullable = false)
    private String name;
    @Column(name = "image_url", nullable = false)
    private String pictureUrl;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

}
