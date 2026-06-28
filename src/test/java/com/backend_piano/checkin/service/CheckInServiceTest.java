package com.backend_piano.checkin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.backend_piano.student.model.Grade;
import com.backend_piano.student.model.PracticeCourse;
import com.backend_piano.student.model.Student;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CheckInServiceTest {

    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RestrictionService restrictionService;

    private Clock clock;
    private CheckInService checkInService;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(
                Instant.parse("2026-06-28T05:03:00Z"),
                ZoneId.of("Asia/Seoul")
        );
        checkInService = new CheckInService(
                checkInRepository,
                reservationRepository,
                roomRepository,
                restrictionService,
                clock
        );
    }

    @Test
    void QR_인증을_생성한다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(2L, RoomFloor.THIRD, "301", "예술체육대학2-301호", "ROOM_301_QR_TOKEN");
        Reservation reservation = createReservation(
                10L,
                student,
                room,
                LocalDate.now(clock),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                ReservationStatus.RESERVED
        );
        CheckInCreateRequest request = new CheckInCreateRequest(room.getQrToken());

        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByQrTokenAndActiveTrue(room.getQrToken())).thenReturn(Optional.of(room));
        when(reservationRepository.findCheckInTargetReservationForUpdate(
                eq(student),
                eq(room),
                eq(LocalDate.now(clock)),
                eq(LocalTime.now(clock)),
                any()
        )).thenReturn(Optional.of(reservation));
        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.empty());
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> {
            CheckIn saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 100L);
            return saved;
        });

        CheckInResponse response = checkInService.createCheckIn(studentDetails, request);

        assertThat(response.checkInId()).isEqualTo(100L);
        assertThat(response.reservationId()).isEqualTo(reservation.getId());
        assertThat(response.roomId()).isEqualTo(room.getId());
        assertThat(response.checkedInAt()).isEqualTo(LocalDateTime.now(clock));
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CHECKED_IN);
        assertThat(reservation.getCheckedInAt()).isEqualTo(LocalDateTime.now(clock));

        ArgumentCaptor<CheckIn> captor = ArgumentCaptor.forClass(CheckIn.class);
        verify(checkInRepository).save(captor.capture());
        assertThat(captor.getValue().getStudent()).isEqualTo(student);
        assertThat(captor.getValue().getReservation()).isEqualTo(reservation);
        assertThat(captor.getValue().getRoom()).isEqualTo(room);
    }

    @Test
    void 지하_연습실_QR로는_인증할_수_없다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(8L, RoomFloor.BASEMENT, "B115", "예술체육대학2-B115호", "ROOM_B115_QR_TOKEN");
        CheckInCreateRequest request = new CheckInCreateRequest(room.getQrToken());

        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByQrTokenAndActiveTrue(room.getQrToken())).thenReturn(Optional.of(room));

        assertThatThrownBy(() -> checkInService.createCheckIn(studentDetails, request))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(CheckInErrorCode.CHECK_IN_ROOM_NOT_SUPPORTED);

        verify(reservationRepository, never()).findCheckInTargetReservationForUpdate(any(), any(), any(), any(), any());
    }

    @Test
    void 인증_가능한_예약이_없으면_예외가_발생한다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(2L, RoomFloor.THIRD, "301", "예술체육대학2-301호", "ROOM_301_QR_TOKEN");
        CheckInCreateRequest request = new CheckInCreateRequest(room.getQrToken());

        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByQrTokenAndActiveTrue(room.getQrToken())).thenReturn(Optional.of(room));
        when(reservationRepository.findCheckInTargetReservationForUpdate(
                eq(student),
                eq(room),
                eq(LocalDate.now(clock)),
                eq(LocalTime.now(clock)),
                any()
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checkInService.createCheckIn(studentDetails, request))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(CheckInErrorCode.CHECK_IN_RESERVATION_NOT_FOUND);
    }

    @Test
    void 이미_인증된_예약은_다시_QR_인증할_수_없다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(2L, RoomFloor.THIRD, "301", "예술체육대학2-301호", "ROOM_301_QR_TOKEN");
        Reservation reservation = createReservation(
                10L,
                student,
                room,
                LocalDate.now(clock),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                ReservationStatus.CHECKED_IN
        );
        CheckInCreateRequest request = new CheckInCreateRequest(room.getQrToken());

        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByQrTokenAndActiveTrue(room.getQrToken())).thenReturn(Optional.of(room));
        when(reservationRepository.findCheckInTargetReservationForUpdate(
                eq(student),
                eq(room),
                eq(LocalDate.now(clock)),
                eq(LocalTime.now(clock)),
                any()
        )).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> checkInService.createCheckIn(studentDetails, request))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(CheckInErrorCode.CHECK_IN_ALREADY_COMPLETED);
    }

    @Test
    void 예약_시작_후_15분이_지나면_QR_인증할_수_없다() {
        Student student = createStudent(1L, "202312001");
        StudentDetails studentDetails = new StudentDetails(student);
        Room room = createRoom(2L, RoomFloor.THIRD, "301", "예술체육대학2-301호", "ROOM_301_QR_TOKEN");
        Reservation reservation = createReservation(
                10L,
                student,
                room,
                LocalDate.now(clock),
                LocalTime.of(13, 30),
                LocalTime.of(16, 0),
                ReservationStatus.RESERVED
        );
        CheckInCreateRequest request = new CheckInCreateRequest(room.getQrToken());

        when(restrictionService.hasCurrentRestriction(student.getId())).thenReturn(false);
        when(roomRepository.findByQrTokenAndActiveTrue(room.getQrToken())).thenReturn(Optional.of(room));
        when(reservationRepository.findCheckInTargetReservationForUpdate(
                eq(student),
                eq(room),
                eq(LocalDate.now(clock)),
                eq(LocalTime.now(clock)),
                any()
        )).thenReturn(Optional.of(reservation));
        when(checkInRepository.findByReservation(reservation)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checkInService.createCheckIn(studentDetails, request))
                .isInstanceOf(ApiException.class)
                .extracting("errorCode")
                .isEqualTo(CheckInErrorCode.CHECK_IN_TIME_NOT_ALLOWED);
    }

    private Student createStudent(Long id, String studentNumber) {
        Student student = Student.create(
                studentNumber,
                "김테스트",
                "encoded-password",
                LocalDate.of(2005, 3, 15),
                Grade.FRESHMAN,
                PracticeCourse.PRACTICE_3
        );
        ReflectionTestUtils.setField(student, "id", id);
        return student;
    }

    private Room createRoom(Long id, RoomFloor floor, String code, String name, String qrToken) {
        Room room = Room.create(floor, code, name);
        room.assignQrToken(qrToken);
        ReflectionTestUtils.setField(room, "id", id);
        return room;
    }

    private Reservation createReservation(
            Long id,
            Student student,
            Room room,
            LocalDate reservationDate,
            LocalTime startTime,
            LocalTime endTime,
            ReservationStatus status
    ) {
        Reservation reservation = Reservation.create(student, room, reservationDate, startTime, endTime);
        ReflectionTestUtils.setField(reservation, "id", id);
        ReflectionTestUtils.setField(reservation, "status", status);
        return reservation;
    }
}
