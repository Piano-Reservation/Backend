package com.backend_piano.room.model;

import com.backend_piano.global.exception.ApiException;
import com.backend_piano.room.exception.RoomErrorCode;

public enum RoomFloor {
    BASEMENT(0),
    FIRST(1),
    THIRD(3);

    private final int level;

    RoomFloor(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }

    public static RoomFloor fromLevel(int level) {
        for (RoomFloor floor : values()) {
            if (floor.level == level) {
                return floor;
            }
        }
        throw new ApiException(RoomErrorCode.INVALID_ROOM_FLOOR);
    }
}
