package com.diplom.qrBackend.Controllers;

import com.diplom.qrBackend.Models.Guest;
import com.diplom.qrBackend.Models.Student;
import com.diplom.qrBackend.Repositories.GuestRepository;
import com.diplom.qrBackend.Repositories.StudentRepository;
import com.diplom.qrBackend.Repositories.TimeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TimeTableRepository timeTableRepository;


    @Autowired
    private GuestRepository guestRepository;

    @GetMapping
    public List<Student> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Optional<Student> student = studentRepository.findById(id);
        return student.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("addstudent")
    public Student addStudent(@RequestBody Student student) {
        student.setUserType("Student");
        return studentRepository.save(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Optional<Student> existingStudentOptional = studentRepository.findById(id);

        if (existingStudentOptional.isPresent()) {
            Student existingStudent = existingStudentOptional.get();

            existingStudent.setFirstName(updatedStudent.getFirstName());
            existingStudent.setLastName(updatedStudent.getLastName());
            existingStudent.setGroupName(updatedStudent.getGroupName());
            existingStudent.setSpecialization(updatedStudent.getSpecialization());

            Student savedStudent = studentRepository.save(existingStudent);

            return new ResponseEntity<>(savedStudent, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/verifyguest")
    public ResponseEntity<String> verifyGuest(@RequestParam Long guestId) {
        Optional<Guest> guestOptional = guestRepository.findById(guestId);

        if (guestOptional.isPresent()) {
            Guest guest = guestOptional.get();
            guest.setVerified(true);
            guestRepository.save(guest);
            return ResponseEntity.ok("Guest verified successfully");
        } else {
            return new ResponseEntity<>("Guest not found", HttpStatus.NOT_FOUND);
        }
    }
}
