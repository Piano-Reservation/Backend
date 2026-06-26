package com.backend_piano.reservation.service;

import com.backend_piano.global.exception.ApiException;
import com.backend_piano.reservation.exception.ReservationErrorCode;
import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.reservation.model.ReservationStatus;
import com.backend_piano.student.model.Student;
import org.springframework.stereotype.Component;

@Component
public class ReservationCancelValidator {

    public void validate(Student student, Reservation reservation) {
        if (!reservation.getStudent().getId().equals(student.getId())) {
            throw new ApiException(ReservationErrorCode.RESERVATION_ACCESS_DENIED);
        }
        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new ApiException(ReservationErrorCode.RESERVATION_CANNOT_BE_CANCELLED);
        }
    }
}
