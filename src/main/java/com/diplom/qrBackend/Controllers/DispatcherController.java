package com.diplom.qrBackend.Controllers;

import com.diplom.qrBackend.Models.Dispatcher;
import com.diplom.qrBackend.Models.TimeTable;
import com.diplom.qrBackend.Models.User;
import com.diplom.qrBackend.Repositories.DispatcherRepository;
import com.diplom.qrBackend.Repositories.TimeTableRepository;
import com.diplom.qrBackend.Repositories.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dispatchers")
public class DispatcherController {

    @Autowired
    private DispatcherRepository dispatcherRepository;

    @GetMapping
    public List<Dispatcher> getAllDispatchers() {
        return dispatcherRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dispatcher> getDispatcherById(@PathVariable Long id) {
        Optional<Dispatcher> dispatcher = dispatcherRepository.findById(id);
        return dispatcher.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public Dispatcher addDispatcher(@RequestBody Dispatcher dispatcher) {
        dispatcher.setUserType("Dispatcher");
        return dispatcherRepository.save(dispatcher);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Dispatcher> updateDispatcher(@PathVariable Long id, @RequestBody Dispatcher updatedDispatcher) {
        Optional<Dispatcher> existingDispatcherOptional = dispatcherRepository.findById(id);

        if (existingDispatcherOptional.isPresent()) {
            Dispatcher existingDispatcher = existingDispatcherOptional.get();

            existingDispatcher.setUsername(updatedDispatcher.getUsername());
            existingDispatcher.setFirstName(updatedDispatcher.getFirstName());
            existingDispatcher.setLastName(updatedDispatcher.getLastName());
            existingDispatcher.setPassword(updatedDispatcher.getPassword());

            Dispatcher savedDispatcher = dispatcherRepository.save(existingDispatcher);

            return ResponseEntity.ok(savedDispatcher);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDispatcher(@PathVariable Long id) {
        if (dispatcherRepository.existsById(id)) {
            dispatcherRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}