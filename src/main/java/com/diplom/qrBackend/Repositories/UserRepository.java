package com.diplom.qrBackend.Repositories;

import com.diplom.qrBackend.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findAllByIdIn(List<Long> userIds);
    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);
}
