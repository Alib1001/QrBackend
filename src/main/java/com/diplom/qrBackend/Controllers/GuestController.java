package com.diplom.qrBackend.Controllers;

import com.diplom.qrBackend.Models.Guest;
import com.diplom.qrBackend.Repositories.GuestRepository;
import com.diplom.qrBackend.Repositories.StudentRepository;
import com.diplom.qrBackend.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/guests")
public class    GuestController {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        Optional<Guest> guestOptional = guestRepository.findById(id);
        return guestOptional.map(guest -> ResponseEntity.ok(guest))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Guest> createGuest(@RequestBody Guest guest) {
        if (guest.getUser() != null && !userRepository.existsById(guest.getUser().getId())) {
            return ResponseEntity.badRequest().build();
        }
        Guest savedGuest = guestRepository.save(guest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGuest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guest> updateGuest(@PathVariable Long id, @RequestBody Guest updatedGuest) {
        Optional<Guest> guestOptional = guestRepository.findById(id);

        if (guestOptional.isPresent()) {
            Guest existingGuest = guestOptional.get();
            existingGuest.setFirstName(updatedGuest.getFirstName());
            existingGuest.setLastName(updatedGuest.getLastName());
            existingGuest.setVerified(updatedGuest.getVerified());
            existingGuest.setDescription(updatedGuest.getDescription());

            if (updatedGuest.getUser() != null && !userRepository.existsById(updatedGuest.getUser().getId())) {
                return ResponseEntity.badRequest().build();
            }
            existingGuest.setUser(updatedGuest.getUser());

            Guest savedGuest = guestRepository.save(existingGuest);
            return ResponseEntity.ok(savedGuest);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        if (guestRepository.existsById(id)) {
            guestRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
