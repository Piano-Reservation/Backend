package com.backend_piano.reservation.service;

import com.backend_piano.auth.service.StudentDetails;
import com.backend_piano.global.exception.ApiException;
import com.backend_piano.reservation.dto.AvailabilitySlotStatus;
import com.backend_piano.reservation.dto.ReservationAvailabilityResponse;
import com.backend_piano.reservation.dto.ReservationAvailabilitySlotResponse;
import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.repository.ReservationRepository;
import com.backend_piano.room.exception.RoomErrorCode;
import com.backend_piano.room.model.Room;
import com.backend_piano.room.repository.RoomRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private static final int FIRST_SLOT_HOUR = 9;
    private static final int LAST_SLOT_HOUR = 23;

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public ReservationAvailabilityResponse getAvailability(
            StudentDetails studentDetails,
            Long roomId,
            LocalDate date
    ) {
        Room room = roomRepository.findByIdAndActiveTrue(roomId)
                .orElseThrow(() -> new ApiException(RoomErrorCode.ROOM_NOT_FOUND));

        List<Reservation> reservations = reservationRepository.findByRoomAndReservationDateOrderByStartTimeAsc(room, date);
        List<ReservationAvailabilitySlotResponse> slots = new ArrayList<>();

        for (int hour = FIRST_SLOT_HOUR; hour < LAST_SLOT_HOUR; hour++) {
            LocalTime startTime = LocalTime.of(hour, 0);
            LocalTime endTime = startTime.plusHours(1);
            Reservation overlappedReservation = findOverlappedReservation(reservations, startTime, endTime);

            if (overlappedReservation == null) {
                slots.add(ReservationAvailabilitySlotResponse.of(
                        startTime,
                        endTime,
                        AvailabilitySlotStatus.AVAILABLE,
                        null
                ));
                continue;
            }

            boolean isMyReservation = overlappedReservation.getStudent().getId()
                    .equals(studentDetails.getStudent().getId());

            slots.add(ReservationAvailabilitySlotResponse.of(
                    startTime,
                    endTime,
                    isMyReservation ? AvailabilitySlotStatus.RESERVED_BY_ME : AvailabilitySlotStatus.RESERVED,
                    isMyReservation ? overlappedReservation.getId() : null
            ));
        }

        return ReservationAvailabilityResponse.of(
                room.getId(),
                room.getName(),
                room.getFloor().level(),
                date,
                slots
        );
    }

    private Reservation findOverlappedReservation(
            List<Reservation> reservations,
            LocalTime startTime,
            LocalTime endTime
    ) {
        return reservations.stream()
                .filter(reservation -> reservation.getStartTime().isBefore(endTime)
                        && reservation.getEndTime().isAfter(startTime))
                .findFirst()
                .orElse(null);
    }
}
