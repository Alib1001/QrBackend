package com.diplom.qrBackend.Controllers;

import com.diplom.qrBackend.S3.S3Service;
import com.diplom.qrBackend.DTO.UserDTO;
import com.diplom.qrBackend.Models.*;
import com.diplom.qrBackend.Repositories.StudentRepository;
import com.diplom.qrBackend.Repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private S3Service s3Service;

    @Value("${jwt.secret}")
    private String jwtSecret;


    @GetMapping
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOList = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return userDTOList;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        return userOptional.map(user -> {
            UserDTO userDTO = convertToDTO(user);
            return ResponseEntity.ok(userDTO);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setPassword(user.getPassword());
        userDTO.setUsername(user.getUsername());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setImageUrl(user.getImageUrl());
        userDTO.setFcmToken(user.getFcmToken());

        if (user instanceof Student) {
            Student student = (Student) user;
            userDTO.setUserType("Student");
            userDTO.setGroupName(student.getGroupName());
            userDTO.setSpecialization(student.getSpecialization());
        } else if (user instanceof Teacher) {
            Teacher teacher = (Teacher) user;
            userDTO.setUserType("Teacher");
            userDTO.setTaughtSubject(teacher.getTaughtSubject());
        } else if (user instanceof Guest) {
            Guest guest = (Guest) user;
            userDTO.setUserType("Guest");
            userDTO.setVerified(guest.getVerified());
            userDTO.setDescription(guest.getDescription());
            userDTO.setUser(guest.getUser());
        } else if (user instanceof Admin) {
            userDTO.setUserType("Admin");
        } else {
            userDTO.setUserType("User");
        }

        return userDTO;
    }

    @PatchMapping("/updateLogin/{id}")
    public ResponseEntity<UserDTO> updateLogin(@PathVariable Long id, @RequestParam String newLogin) {
        return updateLoginAndPassword(id, null, newLogin);
    }

    @PatchMapping("/updatePassword/{id}")
    public ResponseEntity<UserDTO> updatePassword(@PathVariable Long id, @RequestParam String newPassword) {
        return updateLoginAndPassword(id, newPassword, null);
    }

    @PatchMapping("/updateLoginAndPassword/{id}")
    public ResponseEntity<UserDTO> updateLoginAndPassword(@PathVariable Long id,
                                                          @RequestParam(required = false) String newPassword,
                                                          @RequestParam(required = false) String newLogin) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            if (newLogin != null && !newLogin.isEmpty()) {
                existingUser.setUsername(newLogin);
            }

            if (newPassword != null && !newPassword.isEmpty()) {
                existingUser.setPassword(newPassword);
            }

            User savedUser = userRepository.save(existingUser);
            UserDTO savedUserDTO = convertToDTO(savedUser);
            return ResponseEntity.ok(savedUserDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestParam String username, @RequestParam String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getPassword().equals(password)) {
                String jwtToken = generateJwtToken(user);

                Map<String, String> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("token", jwtToken);
                response.put("userId", String.valueOf(user.getId()));
                response.put("userType",user.getUserType());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Incorrect password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    private String generateJwtToken(User user) {
        long expirationMillis = System.currentTimeMillis() + 3600000;

        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(expirationMillis))
                .signWith(key)
                .compact();
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User newUser = convertToEntity(userDTO);
        User savedUser = userRepository.save(newUser);
        UserDTO savedUserDTO = convertToDTO(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUserDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO updatedUserDTO) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            User updatedUser = updateExistingUser(existingUser, updatedUserDTO);
            User savedUser = userRepository.save(updatedUser);
            UserDTO savedUserDTO = convertToDTO(savedUser);
            return ResponseEntity.ok(savedUserDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setImageUrl(userDTO.getImageUrl());

        if ("Student".equals(userDTO.getUserType())) {
            Student student = new Student();
            student.setId(user.getId());
            student.setPassword(user.getPassword());
            student.setUsername(user.getUsername());
            student.setFirstName(user.getFirstName());
            student.setLastName(user.getLastName());
            student.setGroupName(userDTO.getGroupName());
            student.setSpecialization(userDTO.getSpecialization());
            user.setImageUrl(userDTO.getImageUrl());
            student.setUserType("Student");
            return student;
        } else if ("Teacher".equals(userDTO.getUserType())) {
            Teacher teacher = new Teacher();
            teacher.setId(user.getId());
            teacher.setPassword(user.getPassword());
            teacher.setUsername(user.getUsername());
            teacher.setFirstName(user.getFirstName());
            teacher.setLastName(user.getLastName());
            teacher.setTaughtSubject(userDTO.getTaughtSubject());
            user.setImageUrl(userDTO.getImageUrl());
            teacher.setUserType("Teacher");
            return teacher;
        }

        else if("Guard".equals(userDTO.getUserType())) {
            Guard guard = new Guard();

            guard.setId(user.getId());
            guard.setPassword(user.getPassword());
            guard.setUsername(user.getUsername());
            guard.setFirstName(user.getFirstName());
            guard.setLastName(user.getLastName());
            user.setImageUrl(userDTO.getImageUrl());
            guard.setUserType("Guard");
            return guard;
        }

        else if("Dispatcher".equals(userDTO.getUserType())) {
            Dispatcher dispatcher = new Dispatcher();

            dispatcher.setId(user.getId());
            dispatcher.setPassword(user.getPassword());
            dispatcher.setUsername(user.getUsername());
            dispatcher.setFirstName(user.getFirstName());
            dispatcher.setLastName(user.getLastName());
            user.setImageUrl(userDTO.getImageUrl());
            dispatcher.setUserType("Dispatcher");
            return dispatcher;
        }

        else if("Admin".equals(userDTO.getUserType())) {
            Admin admin = new Admin();
            admin.setId(user.getId());
            admin.setPassword(user.getPassword());
            admin.setUsername(user.getUsername());
            admin.setFirstName(user.getFirstName());
            admin.setLastName(user.getLastName());
            user.setImageUrl(userDTO.getImageUrl());
            admin.setUserType("Admin");

            return admin;
        }
        else if ("Guest".equals(userDTO.getUserType())) {
            Guest guest = new Guest();
            guest.setId(user.getId());
            guest.setPassword(user.getPassword());
            guest.setUsername(user.getUsername());
            guest.setFirstName(user.getFirstName());
            guest.setLastName(user.getLastName());
            user.setImageUrl(userDTO.getImageUrl());
            guest.setUserType("Admin");
            guest.setVerified(userDTO.getVerified());
            guest.setDescription(userDTO.getDescription());
            guest.setUser(userDTO.getUser());
            return guest;
        }  else {
            user.setUserType("User");
            return user;        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> editUser(@PathVariable Long id, @RequestBody UserDTO updatedUserDTO) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            User updatedUser = updateExistingUser(existingUser, updatedUserDTO);
            User savedUser = userRepository.save(updatedUser);
            UserDTO savedUserDTO = convertToDTO(savedUser);
            return ResponseEntity.ok(savedUserDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private User updateExistingUser(User existingUser, UserDTO updatedUserDTO) {
        existingUser.setUsername(updatedUserDTO.getUsername());
        existingUser.setPassword(updatedUserDTO.getPassword());
        existingUser.setFirstName(updatedUserDTO.getFirstName());
        existingUser.setLastName(updatedUserDTO.getLastName());
        existingUser.setImageUrl(updatedUserDTO.getImageUrl());
        existingUser.setFcmToken(updatedUserDTO.getFcmToken());

        if (existingUser instanceof Student) {
            Student student = (Student) existingUser;
            student.setGroupName(updatedUserDTO.getGroupName());
            student.setSpecialization(updatedUserDTO.getSpecialization());
        } else if (existingUser instanceof Teacher) {
            Teacher teacher = (Teacher) existingUser;
            teacher.setTaughtSubject(updatedUserDTO.getTaughtSubject());
        } else if (existingUser instanceof Guest) {
            Guest guest = (Guest) existingUser;
            guest.setVerified(updatedUserDTO.getVerified());
            guest.setDescription(updatedUserDTO.getDescription());
            guest.setUser(updatedUserDTO.getUser());
        }

        else if ("Dispatcher".equals(updatedUserDTO.getUserType())) {
            if (existingUser instanceof Dispatcher) {
                Dispatcher dispatcher = (Dispatcher) existingUser;
            } else {
                Dispatcher dispatcher = new Dispatcher();
                dispatcher.setUsername(updatedUserDTO.getUsername());
                dispatcher.setPassword(updatedUserDTO.getPassword());
                dispatcher.setFirstName(updatedUserDTO.getFirstName());
                dispatcher.setLastName(updatedUserDTO.getLastName());
                dispatcher.setImageUrl(updatedUserDTO.getImageUrl());
                dispatcher.setUserType("Dispatcher");
                existingUser = dispatcher;
            }
        }

        else if ("Guard".equals(updatedUserDTO.getUserType())) {
            if (existingUser instanceof Guard) {
                Guard guard = (Guard) existingUser;
            } else {
                Guard guard = new Guard();
                guard.setUsername(updatedUserDTO.getUsername());
                guard.setPassword(updatedUserDTO.getPassword());
                guard.setFirstName(updatedUserDTO.getFirstName());
                guard.setLastName(updatedUserDTO.getLastName());
                guard.setImageUrl(updatedUserDTO.getImageUrl());
                guard.setUserType("Dispatcher");
                existingUser = guard;
            }
        }

        return existingUser;
    }


    @PostMapping("/uploadImage/{userId}")
    public ResponseEntity<String> uploadImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (user.getImageUrl() != null) {
                    s3Service.deleteFile(user.getImageUrl());
                }
                String imageUrl = s3Service.uploadFile(file);
                user.setImageUrl(imageUrl);
                userRepository.save(user);
                return ResponseEntity.ok("Image uploaded successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }

    @GetMapping("/{userId}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getImageUrl() != null) {
                try {
                    byte[] imageBytes = s3Service.downloadFile(user.getImageUrl());
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .body(imageBytes);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/saveFCMToken/{userId}")
    public ResponseEntity<String> saveFCMToken(@PathVariable Long userId, @RequestParam String fcmToken) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFcmToken(fcmToken);
            userRepository.save(user);
            return ResponseEntity.ok("FCM token saved successfully for user: " + userId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}