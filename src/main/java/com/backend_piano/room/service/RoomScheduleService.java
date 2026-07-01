package com.backend_piano.room.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.reservation.dto.ReservationAvailabilityResponse;
import com.backend_piano.reservation.service.ReservationQueryService;
import com.backend_piano.room.exception.RoomErrorCode;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.repository.RoomRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomScheduleService {

    private final RoomRepository roomRepository;
    private final ReservationQueryService reservationQueryService;

    public List<ReservationAvailabilityResponse> getSchedulesByFloor(
            StudentDetails studentDetails, int floorLevel, LocalDate date) {
        RoomFloor floor = RoomFloor.fromLevel(floorLevel);
        if (floor == RoomFloor.BASEMENT) {
            throw new ApiException(RoomErrorCode.SCHEDULE_NOT_SUPPORTED_FLOOR);
        }

        List<Room> rooms = roomRepository.findByFloorAndActiveTrueOrderByCodeAsc(floor);
        return rooms.stream()
                .map(room -> reservationQueryService.getAvailability(studentDetails, room.getId(), date))
                .toList();
    }
}
