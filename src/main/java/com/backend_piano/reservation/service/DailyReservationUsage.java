package com.backend_piano.reservation.service;

import com.backend_piano.reservation.exception.ReservationErrorCode;
import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.room.model.RoomFloor;
import java.time.Duration;
import java.util.List;

public record DailyReservationUsage(
        long firstFloorMinutes,
        long thirdFloorMinutes,
        long totalMinutes
) {

    private static final long FIRST_FLOOR_DAILY_LIMIT_MINUTES = 240;
    private static final long THIRD_FLOOR_DAILY_LIMIT_MINUTES = 360;
    private static final long TOTAL_DAILY_LIMIT_MINUTES = 600;

    public static DailyReservationUsage from(List<Reservation> reservations) {
        long firstFloorMinutes = reservations.stream()
                .filter(reservation -> reservation.getRoom().getFloor() == RoomFloor.FIRST)
                .mapToLong(DailyReservationUsage::calculateReservationMinutes)
                .sum();
        long thirdFloorMinutes = reservations.stream()
                .filter(reservation -> reservation.getRoom().getFloor() == RoomFloor.THIRD)
                .mapToLong(DailyReservationUsage::calculateReservationMinutes)
                .sum();

        return new DailyReservationUsage(
                firstFloorMinutes,
                thirdFloorMinutes,
                firstFloorMinutes + thirdFloorMinutes
        );
    }

    public ReservationErrorCode validateAdditionalUsage(RoomFloor floor, long requestedMinutes) {
        if (floor == RoomFloor.FIRST && firstFloorMinutes + requestedMinutes > FIRST_FLOOR_DAILY_LIMIT_MINUTES) {
            return ReservationErrorCode.FIRST_FLOOR_DAILY_LIMIT_EXCEEDED;
        }
        if (floor == RoomFloor.THIRD && thirdFloorMinutes + requestedMinutes > THIRD_FLOOR_DAILY_LIMIT_MINUTES) {
            return ReservationErrorCode.THIRD_FLOOR_DAILY_LIMIT_EXCEEDED;
        }
        if (totalMinutes + requestedMinutes > TOTAL_DAILY_LIMIT_MINUTES) {
            return ReservationErrorCode.TOTAL_DAILY_LIMIT_EXCEEDED;
        }
        return null;
    }

    private static long calculateReservationMinutes(Reservation reservation) {
        return Duration.between(reservation.getStartTime(), reservation.getEndTime()).toMinutes();
    }
}
