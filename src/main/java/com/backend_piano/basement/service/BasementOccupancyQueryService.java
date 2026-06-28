package com.backend_piano.basement.service;

import com.backend_piano.basement.dto.BasementOccupancyStatusResponse;
import com.backend_piano.basement.model.BasementOccupancy;
import com.backend_piano.basement.model.BasementOccupancyStatus;
import com.backend_piano.basement.repository.BasementOccupancyRepository;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.repository.RoomRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasementOccupancyQueryService {

    private final RoomRepository roomRepository;
    private final BasementOccupancyRepository basementOccupancyRepository;

    public List<BasementOccupancyStatusResponse> getCurrentOccupancies() {
        List<Room> basementRooms = roomRepository.findByFloorAndActiveTrueOrderByCodeAsc(RoomFloor.BASEMENT);
        Map<Long, BasementOccupancy> occupanciesByRoomId = basementOccupancyRepository
                .findCurrentOccupanciesByFloor(RoomFloor.BASEMENT, BasementOccupancyStatus.IN_USE)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        occupancy -> occupancy.getRoom().getId(),
                        Function.identity()
                ));

        return basementRooms.stream()
                .map(room -> toStatusResponse(room, occupanciesByRoomId.get(room.getId())))
                .toList();
    }

    private BasementOccupancyStatusResponse toStatusResponse(Room room, BasementOccupancy occupancy) {
        if (occupancy == null) {
            return BasementOccupancyStatusResponse.vacant(
                    room.getId(),
                    room.getFloor().level(),
                    room.getCode(),
                    room.getName()
            );
        }

        return BasementOccupancyStatusResponse.occupied(
                room.getId(),
                room.getFloor().level(),
                room.getCode(),
                room.getName(),
                occupancy.getStudent().getName(),
                occupancy.getId(),
                occupancy.getEnteredAt()
        );
    }
}
