package com.backend_piano.room.model;

import com.backend_piano.global.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "rooms",
        indexes = {
                @Index(name = "idx_rooms_floor", columnList = "floor"),
                @Index(name = "idx_rooms_code", columnList = "code", unique = true)
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomFloor floor;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 100)
    private String qrToken;

    @Column(nullable = false)
    private boolean active;

    public static Room create(RoomFloor floor, String code, String name) {
        Room room = new Room();
        room.floor = floor;
        room.code = code;
        room.name = name;
        room.active = true;
        return room;
    }

    public void assignQrToken(String qrToken) {
        if (Objects.nonNull(qrToken) && qrToken.isBlank()) {
            throw new IllegalArgumentException("QR 토큰은 공백일 수 없습니다.");
        }
        if (Objects.nonNull(qrToken) && qrToken.length() > 100) {
            throw new IllegalArgumentException("QR 토큰은 100자를 초과할 수 없습니다.");
        }
        this.qrToken = qrToken;
    }

    public void deactivate() {
        this.active = false;
    }
}
