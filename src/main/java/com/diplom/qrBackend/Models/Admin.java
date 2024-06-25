package com.diplom.qrBackend.Models;

import com.diplom.qrBackend.DTO.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "Admin")
public class Admin extends User {
}
