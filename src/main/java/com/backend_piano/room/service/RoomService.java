package com.backend_piano.room.service;

import com.backend_piano.global.exception.ApiException;
import com.backend_piano.room.dto.RoomResponse;
import com.backend_piano.room.exception.RoomErrorCode;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomAllowedCourse;
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

    public RoomResponse getRoom(Long roomId) {
        Room room = roomRepository.findByIdAndActiveTrue(roomId)
                .orElseThrow(() -> new ApiException(RoomErrorCode.ROOM_NOT_FOUND));
        List<Integer> majorPracticeTargets = roomAllowedCourseRepository
                .findAllowedCoursesByRoomIds(List.of(roomId)).stream()
                .map(this::toPracticeCourseNumber)
                .sorted()
                .toList();
        return RoomResponse.of(room, majorPracticeTargets);
    }

    public List<RoomResponse> getRooms(int floorLevel) {
        RoomFloor floor = RoomFloor.fromLevel(floorLevel);
        List<Room> rooms = roomRepository.findByFloorAndActiveTrueOrderByCodeAsc(floor);
        Map<Long, List<Integer>> majorPracticeTargetsByRoomId = buildMajorPracticeTargetsByRoomId(rooms);

        return rooms.stream()
                .map(room -> RoomResponse.of(
                        room,
                        majorPracticeTargetsByRoomId.getOrDefault(room.getId(), List.of())
                ))
                .toList();
    }

    private Map<Long, List<Integer>> buildMajorPracticeTargetsByRoomId(List<Room> rooms) {
        List<Long> roomIds = rooms.stream()
                .map(Room::getId)
                .toList();
        if (roomIds.isEmpty()) {
            return Map.of();
        }

        return roomAllowedCourseRepository.findAllowedCoursesByRoomIds(roomIds).stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        allowedCourse -> allowedCourse.getRoom().getId(),
                        java.util.stream.Collectors.mapping(
                                this::toPracticeCourseNumber,
                                java.util.stream.Collectors.collectingAndThen(
                                        java.util.stream.Collectors.toList(),
                                        this::sortTargets
                                )
                        )
                ));
    }

    private int toPracticeCourseNumber(RoomAllowedCourse allowedCourse) {
        return allowedCourse.getPracticeCourse().number();
    }

    private List<Integer> sortTargets(List<Integer> targets) {
        return targets.stream()
                .sorted(Comparator.naturalOrder())
                .toList();
    }
}
