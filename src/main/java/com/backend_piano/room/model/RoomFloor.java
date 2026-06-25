package com.backend_piano.room.model;

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
}
