package com.backend_piano.reservation.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.reservation.dto.ReservationCreateRequest;
import com.backend_piano.reservation.dto.ReservationResponse;
import com.backend_piano.reservation.exception.ReservationErrorCode;
import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.repository.ReservationRepository;
import com.backend_piano.room.exception.RoomErrorCode;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.repository.RoomRepository;
import com.backend_piano.student.model.Student;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final ReservationCreateValidator reservationCreateValidator;
    private final ReservationCancelValidator reservationCancelValidator;
    private final Clock clock;

    public ReservationResponse createReservation(
            StudentDetails studentDetails,
            ReservationCreateRequest request
    ) {
        Student student = studentDetails.getStudent();
        reservationCreateValidator.validateBeforeRoomLookup(student, request);

        Room room = roomRepository.findByIdAndActiveTrueForUpdate(request.roomId())
                .orElseThrow(() -> new ApiException(RoomErrorCode.ROOM_NOT_FOUND));

        reservationCreateValidator.validateAfterRoomLookup(student, room, request);

        Reservation reservation = reservationRepository.save(Reservation.create(
                student,
                room,
                request.date(),
                request.startTime(),
                request.endTime()
        ));

        return ReservationResponse.from(reservation);
    }

    public void cancelReservation(StudentDetails studentDetails, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        reservationCancelValidator.validate(studentDetails.getStudent(), reservation);
        reservation.cancel("USER_CANCELLED", LocalDateTime.now(clock));
    }
}
