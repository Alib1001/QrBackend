package com.diplom.qrBackend.Repositories;

import com.diplom.qrBackend.Models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByFirstNameAndLastName(String firstName, String lastName);

}
