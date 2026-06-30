package com.backend_piano.checkin.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.checkin.dto.CheckInCreateRequest;
import com.backend_piano.checkin.dto.CheckInResponse;
import com.backend_piano.checkin.exception.CheckInErrorCode;
import com.backend_piano.checkin.model.CheckIn;
import com.backend_piano.checkin.repository.CheckInRepository;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.model.ReservationStatus;
import com.backend_piano.reservation.repository.ReservationRepository;
import com.backend_piano.restriction.service.RestrictionService;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.model.RoomFloor;
import com.backend_piano.room.repository.RoomRepository;
import com.backend_piano.student.model.Student;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private static final List<ReservationStatus> CHECK_IN_TARGET_STATUSES = List.of(
            ReservationStatus.RESERVED,
            ReservationStatus.CHECKED_IN
    );

    private final CheckInRepository checkInRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RestrictionService restrictionService;
    private final Clock clock;

    @Transactional
    public CheckInResponse createCheckIn(StudentDetails studentDetails, CheckInCreateRequest request) {
        Student student = studentDetails.getStudent();
        validateRestrictedStudent(student.getId());

        Room room = roomRepository.findByQrTokenAndActiveTrue(request.qrToken())
                .orElseThrow(() -> new ApiException(CheckInErrorCode.CHECK_IN_INVALID_QR_TOKEN));
        validateCheckInSupportedRoom(room);

        LocalDateTime now = LocalDateTime.now(clock);
        Reservation reservation = reservationRepository.findCheckInTargetReservationForUpdate(
                        student,
                        room,
                        now.toLocalDate(),
                        now.toLocalTime(),
                        CHECK_IN_TARGET_STATUSES
                )
                .orElseThrow(() -> new ApiException(CheckInErrorCode.CHECK_IN_RESERVATION_NOT_FOUND));

        validateAlreadyCheckedIn(reservation);
        validateCheckInTimeWindow(reservation, now.toLocalTime());

        CheckIn checkIn = CheckIn.create(student, reservation, room, now);
        reservation.markCheckedIn(now);
        checkInRepository.save(checkIn);

        return CheckInResponse.from(checkIn);
    }

    private void validateRestrictedStudent(Long studentId) {
        if (restrictionService.hasCurrentRestriction(studentId)) {
            throw new ApiException(CheckInErrorCode.CHECK_IN_RESTRICTED);
        }
    }

    private void validateCheckInSupportedRoom(Room room) {
        if (room.getFloor() == RoomFloor.BASEMENT) {
            throw new ApiException(CheckInErrorCode.CHECK_IN_ROOM_NOT_SUPPORTED);
        }
    }

    private void validateAlreadyCheckedIn(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN
                || checkInRepository.findByReservation(reservation).isPresent()) {
            throw new ApiException(CheckInErrorCode.CHECK_IN_ALREADY_COMPLETED);
        }
    }

    private void validateCheckInTimeWindow(Reservation reservation, LocalTime currentTime) {
        LocalTime startTime = reservation.getStartTime();
        LocalTime checkInDeadline = startTime.plusMinutes(15);

        if (currentTime.isBefore(startTime) || currentTime.isAfter(checkInDeadline)) {
            throw new ApiException(CheckInErrorCode.CHECK_IN_TIME_NOT_ALLOWED);
        }
    }
}
