package com.backend_piano.basement.repository;

import com.backend_piano.basement.model.BasementOccupancy;
import com.backend_piano.basement.model.BasementOccupancyStatus;
import com.backend_piano.room.model.Room;
import com.backend_piano.student.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasementOccupancyRepository extends JpaRepository<BasementOccupancy, Long> {

    Optional<BasementOccupancy> findFirstByRoomAndStatus(Room room, BasementOccupancyStatus status);

    Optional<BasementOccupancy> findFirstByStudentAndStatus(Student student, BasementOccupancyStatus status);

    List<BasementOccupancy> findByStudentOrderByEnteredAtDesc(Student student);
}
