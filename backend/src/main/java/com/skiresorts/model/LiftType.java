package com.skiresorts.model;

public enum LiftType {
    CHAIRLIFT("Chairlift"),
    GONDOLA("Gondola"),
    CABLE_CAR("Cable Car"),
    DRAG_LIFT("Drag Lift"),
    T_BAR("T-Bar"),
    MAGIC_CARPET("Magic Carpet"),
    FUNICULAR("Funicular");

    private final String displayName;

    LiftType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
