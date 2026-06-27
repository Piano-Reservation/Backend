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
    private static final List<com.backend_piano.reservation.model.ReservationStatus> OCCUPIED_STATUSES = List.of(
            com.backend_piano.reservation.model.ReservationStatus.RESERVED,
            com.backend_piano.reservation.model.ReservationStatus.CHECKED_IN
    );

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public ReservationAvailabilityResponse getAvailability(
            StudentDetails studentDetails,
            Long roomId,
            LocalDate date
    ) {
        Room room = roomRepository.findByIdAndActiveTrue(roomId)
                .orElseThrow(() -> new ApiException(RoomErrorCode.ROOM_NOT_FOUND));

        List<Reservation> reservations = reservationRepository.findActiveReservationsByRoomAndDate(
                room,
                date,
                OCCUPIED_STATUSES
        );
        List<ReservationAvailabilitySlotResponse> slots = createAvailabilitySlots(reservations, studentDetails.getStudent().getId());

        return ReservationAvailabilityResponse.of(
                room.getId(),
                room.getName(),
                room.getFloor().level(),
                date,
                slots
        );
    }

    private List<ReservationAvailabilitySlotResponse> createAvailabilitySlots(
            List<Reservation> reservations,
            Long studentId
    ) {
        return java.util.stream.IntStream.range(FIRST_SLOT_HOUR, LAST_SLOT_HOUR)
                .mapToObj(hour -> createAvailabilitySlot(reservations, studentId, hour))
                .toList();
    }

    private ReservationAvailabilitySlotResponse createAvailabilitySlot(
            List<Reservation> reservations,
            Long studentId,
            int hour
    ) {
        LocalTime startTime = LocalTime.of(hour, 0);
        LocalTime endTime = startTime.plusHours(1);
        Reservation overlappedReservation = findOverlappedReservation(reservations, startTime, endTime);

        if (overlappedReservation == null) {
            return ReservationAvailabilitySlotResponse.of(
                    startTime,
                    endTime,
                    AvailabilitySlotStatus.AVAILABLE,
                    null
            );
        }

        return toReservedSlotResponse(overlappedReservation, studentId, startTime, endTime);
    }

    private ReservationAvailabilitySlotResponse toReservedSlotResponse(
            Reservation reservation,
            Long studentId,
            LocalTime startTime,
            LocalTime endTime
    ) {
        boolean isMyReservation = reservation.getStudent().getId().equals(studentId);

        return ReservationAvailabilitySlotResponse.of(
                startTime,
                endTime,
                isMyReservation ? AvailabilitySlotStatus.RESERVED_BY_ME : AvailabilitySlotStatus.RESERVED,
                isMyReservation ? reservation.getId() : null
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
