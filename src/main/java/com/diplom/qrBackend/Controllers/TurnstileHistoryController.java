package com.diplom.qrBackend.Controllers;

import com.diplom.qrBackend.Models.TurnstileHistory;
import com.diplom.qrBackend.Models.User;
import com.diplom.qrBackend.Repositories.TurnstileHistoryRepository;
import com.diplom.qrBackend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/turnstile")
public class TurnstileHistoryController {

    @Autowired
    private TurnstileHistoryRepository turnstileHistoryRepository;

    @PostMapping("/scan/{userId}")
    public TurnstileHistory scanUser(@PathVariable long userId) {
        TurnstileHistory turnstileHistory = new TurnstileHistory();
        turnstileHistory.setUserId(userId);
        turnstileHistory.setScanDateTime(new Date());

        TurnstileHistory lastTurnstileHistory = turnstileHistoryRepository.findTopByUserIdOrderByScanDateTimeDesc(userId);
        int lastScanCount = (lastTurnstileHistory == null) ? 0 : lastTurnstileHistory.getScanCount();
        turnstileHistory.setScanCount(lastScanCount + 1);

        turnstileHistory.setQrcode(generateQRCode());

        return turnstileHistoryRepository.save(turnstileHistory);
    }

    @GetMapping("/history/{userId}")
    public List<TurnstileHistory> getHistoryForUser(@PathVariable long userId) {
        return turnstileHistoryRepository.findAllByUserId(userId);
    }

    @GetMapping("/history")
    public List<TurnstileHistory> getAllUserHistory() {
        List<TurnstileHistory> allHistory = turnstileHistoryRepository.findAll();
        return allHistory;
    }

    @GetMapping("/getqrcode")
    public String getDynamicQRCode() {
        TurnstileHistory latestHistory = turnstileHistoryRepository.findTopByOrderByScanDateTimeDesc();
        if (latestHistory != null) {
            return latestHistory.getQrcode();
        } else {
            return "";
        }
    }

    private String generateQRCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}


