package com.backend_piano.room.repository;

import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByFloorAndActiveTrueOrderByCodeAsc(RoomFloor floor);

    Optional<Room> findByCode(String code);
}
