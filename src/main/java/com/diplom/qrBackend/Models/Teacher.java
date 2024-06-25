package com.diplom.qrBackend.Models;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "teacher")
public class Teacher extends User {

    @Column(name = "taughtSubject")
    private String taughtSubject;



    public Teacher() {
        super();
    }

    public Teacher(String username, String password, String firstName, String lastName, String userType) {
        super(username, password, firstName, lastName, userType);
    }





    public String getTaughtSubject() {
        return taughtSubject;
    }

    public void setTaughtSubject(String taughtSubject) {
        this.taughtSubject = taughtSubject;
    }
}
