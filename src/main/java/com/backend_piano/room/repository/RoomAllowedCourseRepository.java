package com.backend_piano.room.repository;

import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomAllowedCourse;
import com.backend_piano.student.model.PracticeCourse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomAllowedCourseRepository extends JpaRepository<RoomAllowedCourse, Long> {

    List<RoomAllowedCourse> findByRoom(Room room);

    List<RoomAllowedCourse> findByRoomIdIn(List<Long> roomIds);

    boolean existsByRoomAndPracticeCourse(Room room, PracticeCourse practiceCourse);
}
