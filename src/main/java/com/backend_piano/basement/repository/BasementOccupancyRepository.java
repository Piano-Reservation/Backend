package com.backend_piano.basement.repository;

import com.backend_piano.basement.model.BasementOccupancy;
import com.backend_piano.basement.model.BasementOccupancyStatus;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.model.Room;
import com.backend_piano.student.model.Student;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BasementOccupancyRepository extends JpaRepository<BasementOccupancy, Long> {

    Optional<BasementOccupancy> findFirstByRoomAndStatus(Room room, BasementOccupancyStatus status);

    Optional<BasementOccupancy> findFirstByStudentAndStatus(Student student, BasementOccupancyStatus status);

    List<BasementOccupancy> findByStudentOrderByEnteredAtDesc(Student student);

    boolean existsByRoomAndStatus(Room room, BasementOccupancyStatus status);

    boolean existsByStudentAndStatus(Student student, BasementOccupancyStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select bo
            from BasementOccupancy bo
            where bo.id = :occupancyId
            """)
    Optional<BasementOccupancy> findByIdForUpdate(@Param("occupancyId") Long occupancyId);

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
