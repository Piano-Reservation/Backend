package com.backend_piano.basement.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.basement.dto.BasementOccupancyCreateRequest;
import com.backend_piano.basement.dto.BasementOccupancyResponse;
import com.backend_piano.basement.exception.BasementOccupancyErrorCode;
import com.backend_piano.basement.model.BasementOccupancy;
import com.backend_piano.basement.model.BasementOccupancyStatus;
import com.backend_piano.basement.repository.BasementOccupancyRepository;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.restriction.service.RestrictionService;
import com.backend_piano.room.exception.RoomErrorCode;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.repository.RoomRepository;
import com.backend_piano.student.model.Student;
import com.backend_piano.student.exception.StudentErrorCode;
import com.backend_piano.student.repository.StudentRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasementOccupancyService {

    private final BasementOccupancyRepository basementOccupancyRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;
    private final RestrictionService restrictionService;
    private final Clock clock;

    public BasementOccupancyResponse createOccupancy(
            StudentDetails studentDetails,
            BasementOccupancyCreateRequest request
    ) {
        Student lockedStudent = studentRepository.findByIdForUpdate(studentDetails.getStudent().getId())
                .orElseThrow(() -> new ApiException(StudentErrorCode.STUDENT_NOT_FOUND));
        validateRestrictedStudent(lockedStudent.getId());

        Room room = roomRepository.findByIdAndActiveTrueForUpdate(request.roomId())
                .orElseThrow(() -> new ApiException(RoomErrorCode.ROOM_NOT_FOUND));
        validateBasementRoom(room);
        validateNotOccupied(room);
        validateStudentNotAlreadyUsingBasement(lockedStudent);

        BasementOccupancy occupancy = basementOccupancyRepository.save(
                BasementOccupancy.create(lockedStudent, room, LocalDateTime.now(clock))
        );

        return BasementOccupancyResponse.from(occupancy);
    }

    public BasementOccupancyResponse exitOccupancy(StudentDetails studentDetails, Long occupancyId) {
        BasementOccupancy occupancy = basementOccupancyRepository.findById(occupancyId)
                .orElseThrow(() -> new ApiException(BasementOccupancyErrorCode.BASEMENT_OCCUPANCY_NOT_FOUND));

        if (!occupancy.getStudent().getId().equals(studentDetails.getStudent().getId())) {
            throw new ApiException(BasementOccupancyErrorCode.BASEMENT_OCCUPANCY_ACCESS_DENIED);
        }
        if (occupancy.getStatus() != BasementOccupancyStatus.IN_USE) {
            throw new ApiException(BasementOccupancyErrorCode.BASEMENT_OCCUPANCY_ALREADY_EXITED);
        }

        occupancy.exit(LocalDateTime.now(clock));
        return BasementOccupancyResponse.from(occupancy);
    }

    private void validateRestrictedStudent(Long studentId) {
        if (restrictionService.hasCurrentRestriction(studentId)) {
            throw new ApiException(BasementOccupancyErrorCode.BASEMENT_OCCUPANCY_RESTRICTED);
        }
    }

    private void validateBasementRoom(Room room) {
        if (room.getFloor() != RoomFloor.BASEMENT) {
            throw new ApiException(BasementOccupancyErrorCode.BASEMENT_ROOM_ONLY);
        }
    }

    private void validateNotOccupied(Room room) {
        if (basementOccupancyRepository.existsByRoomAndStatus(room, BasementOccupancyStatus.IN_USE)) {
            throw new ApiException(BasementOccupancyErrorCode.BASEMENT_ROOM_ALREADY_OCCUPIED);
        }
    }

    private void validateStudentNotAlreadyUsingBasement(Student student) {
        if (basementOccupancyRepository.existsByStudentAndStatus(student, BasementOccupancyStatus.IN_USE)) {
            throw new ApiException(BasementOccupancyErrorCode.BASEMENT_DUPLICATE_OCCUPANCY);
        }
    }
}
