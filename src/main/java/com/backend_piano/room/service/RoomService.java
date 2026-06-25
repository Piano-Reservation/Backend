package com.backend_piano.room.service;

import com.backend_piano.room.dto.RoomResponse;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.repository.RoomAllowedCourseRepository;
import com.backend_piano.room.repository.RoomRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomAllowedCourseRepository roomAllowedCourseRepository;

    public List<RoomResponse> getRooms(int floorLevel) {
        RoomFloor floor = RoomFloor.fromLevel(floorLevel);
        List<Room> rooms = roomRepository.findByFloorAndActiveTrueOrderByCodeAsc(floor);
        List<Long> roomIds = rooms.stream()
                .map(Room::getId)
                .toList();
        Map<Long, List<Integer>> majorPracticeTargetsByRoomId = roomAllowedCourseRepository.findByRoomIdIn(roomIds).stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        allowedCourse -> allowedCourse.getRoom().getId(),
                        java.util.stream.Collectors.mapping(
                                allowedCourse -> allowedCourse.getPracticeCourse().number(),
                                java.util.stream.Collectors.collectingAndThen(
                                        java.util.stream.Collectors.toList(),
                                        targets -> targets.stream()
                                                .sorted(Comparator.naturalOrder())
                                                .toList()
                                )
                        )
                ));

        return rooms.stream()
                .map(room -> RoomResponse.of(
                        room,
                        majorPracticeTargetsByRoomId.getOrDefault(room.getId(), List.of())
                ))
                .toList();
    }
}
