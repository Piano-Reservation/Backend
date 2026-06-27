package com.backend_piano.room.repository;

import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByFloorAndActiveTrueOrderByCodeAsc(RoomFloor floor);

    Optional<Room> findByIdAndActiveTrue(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select r
            from Room r
            where r.id = :id
              and r.active = true
            """)
    Optional<Room> findByIdAndActiveTrueForUpdate(@Param("id") Long id);

    Optional<Room> findByCode(String code);
}
