package com.backend_piano.student.model;

public enum Grade {
    FRESHMAN(1, "1학년"),
    SOPHOMORE(2, "2학년"),
    JUNIOR(3, "3학년"),
    SENIOR(4, "4학년");

    private final int number;
    private final String label;

    Grade(int number, String label) {
        this.number = number;
        this.label = label;
    }

    public int number() { return number; }
    public String label() { return label; }

    public static Grade ofNumber(int n) {
        for (Grade g : values()) {
            if (g.number == n) return g;
        }
        throw new IllegalArgumentException("Invalid grade: " + n);
    }
}