package com.divjazz.recommendic.user.model.userAttributes;

import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.Entity;
import com.divjazz.recommendic.user.model.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.Type;

@Entity
public class ProfilePicture extends AbstractEntity<UserId> {
    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "tt_user_id", nullable = false)
    private User user;

    private String name;

    @Lob
    private byte[] pictureData;

    public ProfilePicture(UserId profilePictureID, User user, String name, byte[] pictureData) {
        super(profilePictureID);
        this.user = user;
        this.name = name;
        this.pictureData = pictureData;
    }

    protected ProfilePicture() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPictureData() {
        return pictureData;
    }

    public void setPictureData(byte[] pictureData) {
        this.pictureData = pictureData;
    }
}
