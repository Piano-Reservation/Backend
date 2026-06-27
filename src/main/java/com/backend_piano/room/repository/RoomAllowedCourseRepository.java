package com.backend_piano.room.repository;

import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomAllowedCourse;
import com.backend_piano.student.model.PracticeCourse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomAllowedCourseRepository extends JpaRepository<RoomAllowedCourse, Long> {

    @Query("""
            select rac
            from RoomAllowedCourse rac
            where rac.room = :room
            """)
    List<RoomAllowedCourse> findAllowedCoursesByRoom(@Param("room") Room room);

    @Query("""
            select rac
            from RoomAllowedCourse rac
            where rac.room.id in :roomIds
            """)
    List<RoomAllowedCourse> findAllowedCoursesByRoomIds(@Param("roomIds") List<Long> roomIds);

    @Query("""
            select count(rac) > 0
            from RoomAllowedCourse rac
            where rac.room = :room
              and rac.practiceCourse = :practiceCourse
            """)
    boolean isPracticeCourseAllowed(
            @Param("room") Room room,
            @Param("practiceCourse") PracticeCourse practiceCourse
    );
}
