package com.diplom.qrBackend.Controllers;

import com.diplom.qrBackend.Models.Teacher;
import com.diplom.qrBackend.Models.TimeTable;
import com.diplom.qrBackend.Repositories.TeacherRepository;
import com.diplom.qrBackend.Repositories.TimeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
    private final TeacherRepository teacherRepository;
    private final TimeTableRepository timeTableRepository;

    @Autowired
    public TeacherController(TeacherRepository teacherRepository, TimeTableRepository timeTableRepository) {
        this.teacherRepository = teacherRepository;
        this.timeTableRepository = timeTableRepository;
    }

    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Teacher> getTeacherById(@PathVariable Long id) {
        return teacherRepository.findById(id);
    }

    @PostMapping
    public Teacher createOrUpdateTeacher(@RequestBody Teacher teacher) {
        teacher.setUserType("Teacher");
        return teacherRepository.save(teacher);
    }

    @DeleteMapping("/{id}")
    public void deleteTeacher(@PathVariable Long id) {
        teacherRepository.deleteById(id);
    }

    @GetMapping("/{id}/timetable")
    public ResponseEntity<?> getTimeTableByTeacherId(@PathVariable Long id) {
        Optional<Teacher> teacherOptional = teacherRepository.findById(id);
        if (teacherOptional.isPresent()) {
            Teacher teacher = teacherOptional.get();
            List<TimeTable> timeTables = timeTableRepository.findAllByTeacher(teacher);
            return ResponseEntity.ok(timeTables);
        } else {
            return ResponseEntity.badRequest().body("Teacher with ID " + id + " does not exist.");
        }
    }

    @GetMapping("/timetable")
    public ResponseEntity<?> getTimeTableByTeacherName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        Optional<Teacher> teacherOptional = teacherRepository.findByFirstNameAndLastName(firstName, lastName);
        if (teacherOptional.isPresent()) {
            Teacher teacher = teacherOptional.get();
            List<TimeTable> timeTables = timeTableRepository.findAllByTeacher(teacher);
            return ResponseEntity.ok(timeTables);
        } else {
            return ResponseEntity.badRequest().body("Teacher with name " + firstName + " " + lastName + " does not exist.");
        }
    }
}
