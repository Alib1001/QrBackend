package com.diplom.qrBackend.Repositories;

import com.diplom.qrBackend.Models.TurnstileHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TurnstileHistoryRepository extends JpaRepository<TurnstileHistory, Long> {
    List<TurnstileHistory> findAllByUserId(long userId);
    TurnstileHistory findTopByOrderByScanDateTimeDesc();

    TurnstileHistory findTopByUserIdOrderByScanDateTimeDesc(long userId);
}