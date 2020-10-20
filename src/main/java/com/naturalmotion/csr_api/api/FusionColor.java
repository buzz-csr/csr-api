package com.naturalmotion.csr_api.api;

public enum FusionColor {

    GREEN(1), BLUE(2), RED(3);

    private int grade;

    FusionColor(int grade) {
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }
}
