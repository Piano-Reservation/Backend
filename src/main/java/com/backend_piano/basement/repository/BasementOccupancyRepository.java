package com.backend_piano.basement.repository;

import com.backend_piano.basement.model.BasementOccupancy;
import com.backend_piano.basement.model.BasementOccupancyStatus;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.model.Room;
import com.backend_piano.student.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BasementOccupancyRepository extends JpaRepository<BasementOccupancy, Long> {

    Optional<BasementOccupancy> findFirstByRoomAndStatus(Room room, BasementOccupancyStatus status);

    Optional<BasementOccupancy> findFirstByStudentAndStatus(Student student, BasementOccupancyStatus status);

    List<BasementOccupancy> findByStudentOrderByEnteredAtDesc(Student student);

    @Query("""
            select bo
            from BasementOccupancy bo
            join fetch bo.student s
            join fetch bo.room r
            where bo.status = :status
              and r.floor = :floor
            order by r.code asc
            """)
    List<BasementOccupancy> findCurrentOccupanciesByFloor(
            @Param("floor") RoomFloor floor,
            @Param("status") BasementOccupancyStatus status
    );
}
