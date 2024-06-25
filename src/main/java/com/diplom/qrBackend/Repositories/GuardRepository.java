package com.diplom.qrBackend.Repositories;

import com.diplom.qrBackend.Models.Guard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuardRepository extends JpaRepository<Guard, Long> {
}