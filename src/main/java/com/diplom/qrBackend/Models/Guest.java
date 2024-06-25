package com.diplom.qrBackend.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "Guest")
public class Guest extends User {
    @Column(name = "verified")
    private boolean verified;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "description")
    private String description;

    public Guest() {}

    public Guest(boolean verified, User user) {
        this.verified = verified;
        this.user = user;
    }



    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean isVerified) {
        this.verified = isVerified;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
