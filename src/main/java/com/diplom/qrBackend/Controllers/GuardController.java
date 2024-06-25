package com.diplom.qrBackend.Controllers;

import com.diplom.qrBackend.Config.FCMService;
import com.diplom.qrBackend.Models.*;
import com.diplom.qrBackend.Repositories.GuardRepository;
import com.diplom.qrBackend.Repositories.GuestRepository;
import com.diplom.qrBackend.Repositories.StudentRepository;
import com.diplom.qrBackend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@RestController
@RequestMapping("/api/guards")
public class GuardController {

    @Autowired
    private GuardRepository guardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GuestRepository guestRepository;



    @GetMapping
    public List<Guard> getAllGuards() {
        return guardRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guard> getGuardById(@PathVariable Long id) {
        Optional<Guard> guard = guardRepository.findById(id);
        return guard.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public Guard addGuard(@RequestBody Guard guard) {
        guard.setUserType("Guard");
        return guardRepository.save(guard);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Guard> updateGuard(@PathVariable Long id, @RequestBody Guard updatedGuard) {
        Optional<Guard> existingGuardOptional = guardRepository.findById(id);

        if (existingGuardOptional.isPresent()) {
            Guard existingGuard = existingGuardOptional.get();

            existingGuard.setUsername(updatedGuard.getUsername());
            existingGuard.setFirstName(updatedGuard.getFirstName());
            existingGuard.setLastName(updatedGuard.getLastName());
            existingGuard.setPassword(updatedGuard.getPassword());

            Guard savedGuard = guardRepository.save(existingGuard);

            return new ResponseEntity<>(savedGuard, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**@GetMapping("/sendnotification/{userId}")
    public ResponseEntity<String> sendNotificationToUserById(@PathVariable Long userId, @RequestParam String guestName, @RequestParam String description) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent() && userOptional.get() instanceof Student || userOptional.get() instanceof Teacher ) {
            Student student = (Student) userOptional.get();
            Guest guest = new Guest();
            guest.setFirstName(guestName);
            guest.setDescription(description);
            guest.setVerified(false);

            student.getGuests().add(guest);
            studentRepository.save(student);

            FCMService.sendNotificationToToken(guestName, description, student.getFcmToken());
            return ResponseEntity.ok("Notification sent and guest added to user with ID: " + userId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/sendnotification")
    public ResponseEntity<String> sendNotificationToUserByUsername(@RequestParam String username, @RequestParam String guestName, @RequestParam String description) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent() && userOptional.get() instanceof Student || userOptional.get() instanceof Teacher) {
            Student student = (Student) userOptional.get();
            Guest guest = new Guest();
            guest.setFirstName(guestName);
            guest.setDescription(description);
            guest.setVerified(false);

            student.getGuests().add(guest);
            studentRepository.save(student);

            FCMService.sendNotificationToToken(guestName, description, student.getFcmToken());
            return ResponseEntity.ok("Notification sent and guest added to user: " + username);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
     **/


    @GetMapping("/assign")
    public ResponseEntity<String> assignGuestToUser(@RequestParam Long guestId, @RequestParam Long userId) {
        Optional<Guest> guestOptional = guestRepository.findById(guestId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (guestOptional.isPresent() && userOptional.isPresent()) {
            Guest guest = guestOptional.get();
            User user = userOptional.get();
            guest.setUser(user);
            guest.setVerified(false);
            guestRepository.save(guest);

            try {
                FCMService.sendNotificationToToken("Request from Guest to go through the turnstile",
                        guest.getFirstName() + " " + guest.getLastName(), user.getFcmToken());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok("Guest assigned to user successfully, but the user has not yet logged in to the Android device.");
            }

            return ResponseEntity.ok("Guest assigned to user successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
