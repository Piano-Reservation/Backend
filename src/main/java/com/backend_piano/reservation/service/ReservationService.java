package com.backend_piano.reservation.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.reservation.dto.ReservationCreateRequest;
import com.backend_piano.reservation.dto.ReservationResponse;
import com.backend_piano.reservation.exception.ReservationErrorCode;
import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.model.ReservationStatus;
import com.backend_piano.reservation.repository.ReservationRepository;
import com.backend_piano.restriction.service.RestrictionService;
import com.backend_piano.room.exception.RoomErrorCode;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.repository.RoomRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private static final LocalTime RESERVATION_OPEN_TIME = LocalTime.of(8, 50);
    private static final LocalTime FIRST_SLOT_TIME = LocalTime.of(9, 0);
    private static final LocalTime LAST_SLOT_END_TIME = LocalTime.of(23, 0);
    private static final long MAX_RESERVATION_MINUTES = 120;
    private static final List<ReservationStatus> ACTIVE_STATUSES = List.of(
            ReservationStatus.RESERVED,
            ReservationStatus.CHECKED_IN
    );

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RestrictionService restrictionService;

    public ReservationResponse createReservation(
            StudentDetails studentDetails,
            ReservationCreateRequest request
    ) {
        validateRestrictedStudent(studentDetails.getStudent().getId());
        validateReservationDate(request.date());
        validateReservationOpenTime();
        validateReservationTimeRange(request.startTime(), request.endTime());

        Room room = roomRepository.findByIdAndActiveTrue(request.roomId())
                .orElseThrow(() -> new ApiException(RoomErrorCode.ROOM_NOT_FOUND));

        validateTimeConflict(room, request.date(), request.startTime(), request.endTime());

        Reservation reservation = reservationRepository.save(Reservation.create(
                studentDetails.getStudent(),
                room,
                request.date(),
                request.startTime(),
                request.endTime()
        ));

        return ReservationResponse.from(reservation);
    }

    private void validateRestrictedStudent(Long studentId) {
        if (restrictionService.hasCurrentRestriction(studentId)) {
            throw new ApiException(ReservationErrorCode.RESERVATION_RESTRICTED);
        }
    }

    private void validateReservationDate(LocalDate reservationDate) {
        if (!LocalDate.now().isEqual(reservationDate)) {
            throw new ApiException(ReservationErrorCode.RESERVATION_DATE_MUST_BE_TODAY);
        }
    }

    private void validateReservationOpenTime() {
        if (LocalTime.now().isBefore(RESERVATION_OPEN_TIME)) {
            throw new ApiException(ReservationErrorCode.RESERVATION_NOT_OPEN_YET);
        }
    }

    private void validateReservationTimeRange(LocalTime startTime, LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new ApiException(ReservationErrorCode.INVALID_RESERVATION_TIME_RANGE);
        }
        if (!startTime.equals(startTime.withMinute(0).withSecond(0).withNano(0))
                || !endTime.equals(endTime.withMinute(0).withSecond(0).withNano(0))) {
            throw new ApiException(ReservationErrorCode.INVALID_RESERVATION_TIME_RANGE);
        }
        if (startTime.isBefore(FIRST_SLOT_TIME) || endTime.isAfter(LAST_SLOT_END_TIME)) {
            throw new ApiException(ReservationErrorCode.INVALID_RESERVATION_TIME_RANGE);
        }
        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
        if (durationMinutes > MAX_RESERVATION_MINUTES) {
            throw new ApiException(ReservationErrorCode.RESERVATION_EXCEEDS_MAX_DURATION);
        }
    }

    private void validateTimeConflict(
            Room room,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        boolean existsConflict = reservationRepository
                .existsByRoomAndReservationDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        room,
                        reservationDate,
                        ACTIVE_STATUSES,
                        endTime,
                        startTime
                );

        if (existsConflict) {
            throw new ApiException(ReservationErrorCode.RESERVATION_TIME_CONFLICT);
        }
    }
}
