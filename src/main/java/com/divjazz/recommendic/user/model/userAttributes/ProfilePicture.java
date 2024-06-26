package com.divjazz.recommendic.user.model.userAttributes;

import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import com.divjazz.recommendic.user.model.User;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Entity
public final class ProfilePicture{
    @Id
    private UUID id;

    private String name;

    private String pictureUrl;

    public ProfilePicture(UUID id, UUID owner, String name, String pictureUrl) {
        this.id = id;
        this.name = name;
        this.pictureUrl = pictureUrl;
    }

    protected ProfilePicture() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
