    package com.diplom.qrBackend.Controllers;

    import com.diplom.qrBackend.Models.Room;
    import com.diplom.qrBackend.Models.Teacher;
    import com.diplom.qrBackend.Models.TimeTable;
    import com.diplom.qrBackend.Repositories.RoomRepository;
    import com.diplom.qrBackend.Repositories.StudentRepository;
    import com.diplom.qrBackend.Repositories.TeacherRepository;
    import com.diplom.qrBackend.Repositories.TimeTableRepository;
    import jakarta.transaction.Transactional;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.scheduling.annotation.Scheduled;
    import org.springframework.web.bind.annotation.*;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.time.LocalTime;
    import java.time.format.DateTimeFormatter;
    import java.util.*;

    @RestController
    @RequestMapping("/api/timetable")
    public class TimeTableController {

        @Autowired
        private TimeTableRepository timeTableRepository;

        @Autowired
        private TeacherRepository teacherRepository;

        @Autowired
        private StudentRepository studentRepository;

        @Autowired
        private RoomRepository roomRepository;


        @GetMapping
        public List<TimeTable> getAllTimeTables() {
            return timeTableRepository.findAll();
        }

        @GetMapping("/{id}")
        public ResponseEntity<TimeTable> getTimeTableById(@PathVariable Long id) {
            Optional<TimeTable> timeTable = timeTableRepository.findById(id);
            return timeTable.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @PostMapping("/addtimetable")
        public ResponseEntity<?> addTimeTableEntries(@RequestBody Map<String, Object> requestBody) {
            Integer scheduleGroupId = generateScheduleGroupId();

            String groupName = (String) requestBody.get("groupName");
            String subjectName = (String) requestBody.get("subjectName");
            String startTime = (String) requestBody.get("startTime");
            String endTime = (String) requestBody.get("endTime");

            List<Integer> studentIds = (List<Integer>) requestBody.get("studentIds");
            Integer teacherId = (Integer) requestBody.get("teacherId");
            Boolean scanable = false;
            Integer roomId = (Integer) requestBody.get("roomId");
            Optional<Room> optionalRoom = roomRepository.findById(roomId);

            if (optionalRoom.isEmpty()) {
                return ResponseEntity.badRequest().body("Room with id " + roomId + " does not exist. Please add an existing room.");
            }
            Room room = optionalRoom.get();

            Optional<Teacher> optionalTeacher = teacherRepository.findById(Long.valueOf(teacherId));
            if (optionalTeacher.isEmpty()) {
                return ResponseEntity.badRequest().body("Teacher with id " + teacherId + " does not exist. Please add an existing teacher.");
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss");

            Date date;
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = currentTime.format(formatter);
            try {
                String dateString = requestBody.get("date") + "T"+formattedTime;
                date = dateFormat.parse(dateString);

            } catch (ParseException e) {
                throw new RuntimeException("Invalid date format. Please use dd-MM-yyyy format.", e);
            }

            List<TimeTable> savedTimeTables = saveTimeTableEntries(scheduleGroupId, groupName, subjectName, startTime, endTime, room, studentIds, teacherId, date,scanable);


            return ResponseEntity.ok(savedTimeTables);
        }


        private List<TimeTable> saveTimeTableEntries(
                Integer scheduleGroupId, String groupName, String subjectName, String startTime,
                String endTime, Room room, List<Integer> studentIds, Integer teacherId,
                Date firstDate, Boolean scanable) {
            Optional<Teacher> teacher = teacherRepository.findById(Long.valueOf(teacherId));
            if (teacher.isEmpty()) {
                return new ArrayList<>();
            }

            List<TimeTable> timetableList = new ArrayList<>();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(firstDate);

            for (int weekNumber = 1; weekNumber <= 15; weekNumber++) {
                Date currentDate = calendar.getTime();
                String dayOfWeek = getDayOfWeek(currentDate);
                TimeTable timeTable = new TimeTable(scheduleGroupId, groupName, subjectName,
                        dayOfWeek, startTime, endTime, room, weekNumber, teacher.get(), currentDate, scanable);
                timeTable.setStudentIds(studentIds);
                timetableList.add(timeTableRepository.save(timeTable));
                calendar.add(Calendar.DAY_OF_YEAR, 7);

            }
            return timetableList;
        }

        private Integer generateScheduleGroupId() {
            return Integer.valueOf(Math.abs(new Random().nextInt()));
        }




        private String getDayOfWeek(Date date) {
            Calendar calendar = Calendar.getInstance(
            );
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            switch (dayOfWeek) {
                case Calendar.MONDAY:
                    return "Monday";
                case Calendar.TUESDAY:
                    return "Tuesday";
                case Calendar.WEDNESDAY:
                    return "Wednesday";
                case Calendar.THURSDAY:
                    return "Thursday";
                case Calendar.FRIDAY:
                    return "Friday";
                case Calendar.SATURDAY:
                    return "Saturday";
                case Calendar.SUNDAY:
                    return "Sunday";
                default:
                    return "";
            }
        }


        @Transactional
        @PutMapping("/edittimetable/{id}")
        public ResponseEntity<TimeTable> editTimeTableEntry(@PathVariable Long id, @RequestBody TimeTable updatedTimeTable) {
            Optional<TimeTable> existingTimeTable = timeTableRepository.findById(id);

            if (existingTimeTable.isPresent()) {
                TimeTable currentSchedule = existingTimeTable.get();

                if (updatedTimeTable.getGroupName() != null) {
                    currentSchedule.setGroupName(updatedTimeTable.getGroupName());
                }
                if (updatedTimeTable.getSubjectName() != null) {
                    currentSchedule.setSubjectName(updatedTimeTable.getSubjectName());
                }
                if (updatedTimeTable.getDayOfWeek() != null) {
                    currentSchedule.setDayOfWeek(updatedTimeTable.getDayOfWeek());
                }
                if (updatedTimeTable.getStartTime() != null) {
                    currentSchedule.setStartTime(updatedTimeTable.getStartTime());
                }
                if (updatedTimeTable.getEndTime() != null) {
                    currentSchedule.setEndTime(updatedTimeTable.getEndTime());
                }

                if (updatedTimeTable.getTeacher() != null){
                    currentSchedule.setTeacher(updatedTimeTable.getTeacher());
                }
                if (updatedTimeTable.getClassroom() != null) {
                    currentSchedule.setClassroom(updatedTimeTable.getClassroom());
                }

                timeTableRepository.save(currentSchedule);

                return ResponseEntity.ok(currentSchedule);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        @Transactional
        @DeleteMapping("/deletetimetable/{id}")
        public ResponseEntity<String> deleteTimeTableEntry(@PathVariable Long id) {
            Optional<TimeTable> timeTable = timeTableRepository.findById(id);

            if (timeTable.isPresent()) {
                timeTableRepository.deleteById(id);
                return ResponseEntity.ok("TimeTable entry deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        }


        @Transactional
        @PostMapping("/addStudentToTimetable/{timetableId}/{studentId}")
        public ResponseEntity<?> addStudentToTimetable(
                @PathVariable Long timetableId, @PathVariable Integer studentId) {

            Optional<TimeTable> optionalTimeTable = timeTableRepository.findById(timetableId);

            if (optionalTimeTable.isPresent()) {
                TimeTable timeTable = optionalTimeTable.get();
                if (timeTable.getStudentIds() == null) {
                    timeTable.setStudentIds(new ArrayList<>());
                }

                List<Integer> studentIds = timeTable.getStudentIds();
                if (!studentIds.contains(studentId)) {
                    studentIds.add(studentId);
                    timeTableRepository.save(timeTable);
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.badRequest().body("Student with ID " + studentId + " is already in the timetable.");
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/getStudents/{timetableId}")
        public ResponseEntity<List<Integer>> getStudentsFromTimetable(@PathVariable Long timetableId) {
            Optional<TimeTable> optionalTimeTable = timeTableRepository.findById(timetableId);
            if (optionalTimeTable.isPresent()) {
                TimeTable timeTable = optionalTimeTable.get();
                List<Integer> studentIds = timeTable.getStudentIds();

                if (studentIds != null && !studentIds.isEmpty()) {
                    return ResponseEntity.ok(studentIds);
                } else {
                    return ResponseEntity.ok(Collections.emptyList());
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @Transactional
        @PutMapping("/edittimetablelist/{scheduleGroupId}")
        public ResponseEntity<List<TimeTable>> editAllTimeTableEntries(@PathVariable Integer scheduleGroupId, @RequestBody TimeTable updatedTimeTable) {
            List<TimeTable> existingTimeTables = timeTableRepository.findAllByScheduleGroupId(scheduleGroupId);

            if (!existingTimeTables.isEmpty()) {
                for (TimeTable currentSchedule : existingTimeTables) {
                    if (updatedTimeTable.getGroupName() != null) {
                        currentSchedule.setGroupName(updatedTimeTable.getGroupName());
                    }
                    if (updatedTimeTable.getSubjectName() != null) {
                        currentSchedule.setSubjectName(updatedTimeTable.getSubjectName());
                    }
                    if (updatedTimeTable.getDayOfWeek() != null) {
                        currentSchedule.setDayOfWeek(updatedTimeTable.getDayOfWeek());
                    }
                    if (updatedTimeTable.getStartTime() != null) {
                        currentSchedule.setStartTime(updatedTimeTable.getStartTime());
                    }
                    if (updatedTimeTable.getEndTime() != null) {
                        currentSchedule.setEndTime(updatedTimeTable.getEndTime());
                    }
                    if (updatedTimeTable.getTeacher() != null) {
                        currentSchedule.setTeacher(updatedTimeTable.getTeacher());
                    }
                    if (updatedTimeTable.getClassroom() != null) {
                        currentSchedule.setClassroom(updatedTimeTable.getClassroom());
                    }
                    timeTableRepository.save(currentSchedule);
                }

                return ResponseEntity.ok(existingTimeTables);
            } else {
                return ResponseEntity.notFound().build();
            }
        }


        @Transactional
        @DeleteMapping("/deletetimetablelist/{scheduleGroupId}")
        public ResponseEntity<String> deleteAllTimeTableEntries(@PathVariable Integer scheduleGroupId) {
            List<TimeTable> timeTablesToDelete = timeTableRepository.findAllByScheduleGroupId(scheduleGroupId);

            if (!timeTablesToDelete.isEmpty()) {
                timeTableRepository.deleteAll(timeTablesToDelete);
                return ResponseEntity.ok("All TimeTable entries with scheduleGroupId " + scheduleGroupId + " deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @Transactional
        @Scheduled(fixedDelay = 30000)
        public void updateRoomStatusAutomatically() {
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat extendedDateFormat= new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss");
            String formatedCurrentDate = dateFormat.format(currentDate);

            List<TimeTable> timeTables = timeTableRepository.findAll();

            for (TimeTable timeTable : timeTables) {
                String startTime = timeTable.getStartTime();
                String endTime = timeTable.getEndTime();

                String formattedTableDate = dateFormat.format(timeTable.getDate());
                Room room = timeTable.getClassroom();

                if (formattedTableDate.equals(formatedCurrentDate)) {
                    int startHours = Integer.parseInt(startTime.split(":")[0]);
                    int startMinutes = Integer.parseInt(startTime.split(":")[1]);
                    int endHours = Integer.parseInt(endTime.split(":")[0]);
                    int endMinutes = Integer.parseInt(endTime.split(":")[1]);

                    Calendar calendar = Calendar.getInstance();

                    calendar.setTime(new Date());
                    calendar.set(Calendar.HOUR_OF_DAY, startHours);
                    calendar.set(Calendar.MINUTE, startMinutes);
                    Date lessonStart = calendar.getTime();

                    calendar.set(Calendar.HOUR_OF_DAY, endHours);
                    calendar.set(Calendar.MINUTE, endMinutes);
                    Date lessonEnd = calendar.getTime();

                    boolean isLessonInProgress = currentDate.after(lessonStart) && currentDate.before(lessonEnd);

                    room.setBusy(isLessonInProgress);

                    roomRepository.save(room);

                }
            }
        }

        @Transactional
        @PutMapping("/update-scanable/{id}")
        public ResponseEntity<TimeTable> updateScanable(@PathVariable Long id, @RequestBody Boolean scanable) {
            Optional<TimeTable> optionalTimeTable = timeTableRepository.findById(id);

            if (optionalTimeTable.isPresent()) {
                TimeTable timeTable = optionalTimeTable.get();
                timeTable.setScanable(scanable);
                timeTableRepository.save(timeTable);
                return ResponseEntity.ok(timeTable);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/checkScanable/{timetableId}")
        public ResponseEntity<Boolean> checkTimetableScanable(@PathVariable Long timetableId) {
            Optional<TimeTable> optionalTimeTable = timeTableRepository.findById(timetableId);
            if (optionalTimeTable.isPresent()) {
                TimeTable timeTable = optionalTimeTable.get();
                return ResponseEntity.ok(timeTable.isScanable());
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/getSubjectNamesForGroup/{groupName}")
        public ResponseEntity<List<String>> getSubjectNamesForGroup(@PathVariable String groupName) {
            List<TimeTable> timeTables = timeTableRepository.findAllByGroupName(groupName);
            if (!timeTables.isEmpty()) {
                List<String> subjectNames = new ArrayList<>();
                for (TimeTable timeTable : timeTables) {
                    String subjectName = timeTable.getSubjectName();
                    if (!subjectNames.contains(subjectName)) {
                        subjectNames.add(subjectName);
                    }
                }
                return ResponseEntity.ok(subjectNames);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/groups")
        public ResponseEntity<List<String>> getAllGroups() {
            List<TimeTable> timeTables = timeTableRepository.findAll();
            if (!timeTables.isEmpty()) {
                List<String> groups = new ArrayList<>();
                for (TimeTable timeTable : timeTables) {
                    String groupName = timeTable.getGroupName();
                    if (!groups.contains(groupName)) {
                        groups.add(groupName);
                    }
                }
                return ResponseEntity.ok(groups);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/getTimetable/{groupName}")
        public ResponseEntity<List<TimeTable>> getTimeTableForGroup(@PathVariable String groupName) {
            List<TimeTable> timeTables = timeTableRepository.findAllByGroupName(groupName);
            if (!timeTables.isEmpty()) {
                return ResponseEntity.ok(timeTables);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/teacher/{id}/getTimetable")
        public ResponseEntity<List<TimeTable>> getTimeTableByTeacherId(@PathVariable Long id) {
            Optional<Teacher> teacherOptional = teacherRepository.findById(id);
            if (teacherOptional.isPresent()) {
                Teacher teacher = teacherOptional.get();
                List<TimeTable> timeTables = timeTableRepository.findAllByTeacher(teacher);
                return ResponseEntity.ok(timeTables);
            } else {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }
        }

        @GetMapping("/teacher/getTimetable")
        public ResponseEntity<List<TimeTable>> getTimeTableByTeacherName(
                @RequestParam String firstName,
                @RequestParam String lastName) {
            Optional<Teacher> teacherOptional = teacherRepository.findByFirstNameAndLastName(firstName, lastName);
            if (teacherOptional.isPresent()) {
                Teacher teacher = teacherOptional.get();
                List<TimeTable> timeTables = timeTableRepository.findAllByTeacher(teacher);
                return ResponseEntity.ok(timeTables);
            } else {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }
        }
    }

