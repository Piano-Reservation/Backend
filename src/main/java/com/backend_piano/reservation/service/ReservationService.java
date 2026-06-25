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
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.repository.RoomAllowedCourseRepository;
import com.backend_piano.room.repository.RoomRepository;
import com.backend_piano.student.model.PracticeCourse;
import com.backend_piano.student.model.Student;
import java.time.Duration;
import java.time.LocalDate;
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
    private static final LocalTime THIRD_FLOOR_EVENING_START_TIME = LocalTime.of(18, 0);
    private static final LocalTime MAJOR_RELEASE_TIME = LocalTime.of(13, 0);
    private static final LocalTime LAST_SLOT_END_TIME = LocalTime.of(23, 0);
    private static final long MAX_RESERVATION_MINUTES = 120;
    private static final long FIRST_FLOOR_DAILY_LIMIT_MINUTES = 240;
    private static final long THIRD_FLOOR_DAILY_LIMIT_MINUTES = 360;
    private static final long TOTAL_DAILY_LIMIT_MINUTES = 600;
    private static final List<ReservationStatus> ACTIVE_STATUSES = List.of(
            ReservationStatus.RESERVED,
            ReservationStatus.CHECKED_IN
    );
    private static final List<ReservationStatus> DAILY_LIMIT_STATUSES = List.of(
            ReservationStatus.RESERVED,
            ReservationStatus.CHECKED_IN,
            ReservationStatus.COMPLETED
    );

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RoomAllowedCourseRepository roomAllowedCourseRepository;
    private final RestrictionService restrictionService;

    public ReservationResponse createReservation(
            StudentDetails studentDetails,
            ReservationCreateRequest request
    ) {
        Student student = studentDetails.getStudent();

        validateRestrictedStudent(student.getId());
        validateReservationDate(request.date());
        validateReservationOpenTime();
        validateReservationTimeRange(request.startTime(), request.endTime());

        Room room = roomRepository.findByIdAndActiveTrue(request.roomId())
                .orElseThrow(() -> new ApiException(RoomErrorCode.ROOM_NOT_FOUND));

        validateDailyReservationLimits(
                student,
                room,
                request.date(),
                request.startTime(),
                request.endTime()
        );
        validateThirdFloorEveningMajorRule(
                student.getPracticeCourse(),
                room,
                request.startTime(),
                request.endTime()
        );
        validateTimeConflict(room, request.date(), request.startTime(), request.endTime());

        Reservation reservation = reservationRepository.save(Reservation.create(
                student,
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

    private void validateDailyReservationLimits(
            Student student,
            Room room,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        long requestedMinutes = Duration.between(startTime, endTime).toMinutes();
        List<Reservation> existingReservations = reservationRepository
                .findByStudentAndReservationDateAndStatusInOrderByStartTimeAsc(
                        student,
                        reservationDate,
                        DAILY_LIMIT_STATUSES
                );

        long firstFloorReservedMinutes = existingReservations.stream()
                .filter(reservation -> reservation.getRoom().getFloor() == RoomFloor.FIRST)
                .mapToLong(this::calculateReservationMinutes)
                .sum();
        long thirdFloorReservedMinutes = existingReservations.stream()
                .filter(reservation -> reservation.getRoom().getFloor() == RoomFloor.THIRD)
                .mapToLong(this::calculateReservationMinutes)
                .sum();
        long totalReservedMinutes = firstFloorReservedMinutes + thirdFloorReservedMinutes;

        if (room.getFloor() == RoomFloor.FIRST
                && firstFloorReservedMinutes + requestedMinutes > FIRST_FLOOR_DAILY_LIMIT_MINUTES) {
            throw new ApiException(ReservationErrorCode.FIRST_FLOOR_DAILY_LIMIT_EXCEEDED);
        }
        if (room.getFloor() == RoomFloor.THIRD
                && thirdFloorReservedMinutes + requestedMinutes > THIRD_FLOOR_DAILY_LIMIT_MINUTES) {
            throw new ApiException(ReservationErrorCode.THIRD_FLOOR_DAILY_LIMIT_EXCEEDED);
        }
        if (totalReservedMinutes + requestedMinutes > TOTAL_DAILY_LIMIT_MINUTES) {
            throw new ApiException(ReservationErrorCode.TOTAL_DAILY_LIMIT_EXCEEDED);
        }
    }

    private void validateThirdFloorEveningMajorRule(
            PracticeCourse practiceCourse,
            Room room,
            LocalTime startTime,
            LocalTime endTime
    ) {
        if (room.getFloor() != RoomFloor.THIRD) {
            return;
        }
        if (!isEveningReservation(startTime, endTime)) {
            return;
        }
        if (!LocalTime.now().isBefore(MAJOR_RELEASE_TIME)) {
            return;
        }
        boolean allowed = roomAllowedCourseRepository.existsByRoomAndPracticeCourse(room, practiceCourse);
        boolean unrestrictedRoom = roomAllowedCourseRepository.findByRoom(room).isEmpty();
        if (!allowed && !unrestrictedRoom) {
            throw new ApiException(ReservationErrorCode.EVENING_ROOM_MAJOR_RESTRICTION);
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

    private boolean isEveningReservation(LocalTime startTime, LocalTime endTime) {
        return !startTime.isBefore(THIRD_FLOOR_EVENING_START_TIME)
                || endTime.isAfter(THIRD_FLOOR_EVENING_START_TIME);
    }

    private long calculateReservationMinutes(Reservation reservation) {
        return Duration.between(reservation.getStartTime(), reservation.getEndTime()).toMinutes();
    }
}
