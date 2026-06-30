package com.backend_piano.checkin.repository;

import com.backend_piano.checkin.model.CheckIn;
import com.backend_piano.reservation.model.Reservation;
import com.backend_piano.student.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    Optional<CheckIn> findByReservation(Reservation reservation);

    List<CheckIn> findByStudentOrderByCheckedInAtDesc(Student student);
}
