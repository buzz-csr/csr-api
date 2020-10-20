package com.naturalmotion.csr_api.api;

public enum CarElement {

    ENGINE(0),
    TURBO(1),
    AIR(2),
    NOS(3),
    CHASSIS(4),
    TIRE(5),
    SHIFT(6);

    private int partType;

    CarElement(int partType) {
        this.partType = partType;
    }

    public int getPartType() {
        return partType;
    }
}
