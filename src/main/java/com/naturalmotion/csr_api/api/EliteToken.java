package com.naturalmotion.csr_api.api;

public enum EliteToken {

    GREEN(1),
    BLUE(2),
    RED(3),
    YELLOW(4);

    private final int grade;

    private EliteToken(int grade) {
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }
}
