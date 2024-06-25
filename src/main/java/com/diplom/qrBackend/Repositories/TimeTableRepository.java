package com.diplom.qrBackend.Repositories;


import com.diplom.qrBackend.Models.Teacher;
import com.diplom.qrBackend.Models.TimeTable;
import com.diplom.qrBackend.Models.TurnstileHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    List<TimeTable> findByGroupName(String groupName);
    List<TimeTable> findBySubjectName(String subjectName);
    List<TimeTable> findAllByGroupName(String groupName);
    List<TimeTable> findAllByScheduleGroupId(Integer scheduleGroupId);
    List<TimeTable> findAllByTeacher(Teacher teacher);


}