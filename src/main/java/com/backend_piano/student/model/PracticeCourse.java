package com.backend_piano.student.model;

public enum PracticeCourse{
        PRACTICE_1, PRACTICE_2,   // 1학년 권장
        PRACTICE_3, PRACTICE_4,   // 2학년 권장
        PRACTICE_5, PRACTICE_6,   // 3학년 권장
        PRACTICE_7, PRACTICE_8;   // 4학년 권장

        public int number() {
            return ordinal() + 1;
        }

        public int recommendedGrade() {
            return (ordinal() / 2) + 1;
        }

        public ContactGroup contactGroup() {
            return number() <= 4 ? ContactGroup.VICE_PRESIDENT : ContactGroup.PRESIDENT;
        }

        public enum ContactGroup {
            VICE_PRESIDENT,
            PRESIDENT
        }
    }