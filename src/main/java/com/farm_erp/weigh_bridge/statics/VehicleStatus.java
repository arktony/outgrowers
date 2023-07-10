package com.farm_erp.weigh_bridge.statics;

public enum VehicleStatus {
    IMPORTANT("Important"),
    CRITICAL("Critical"),
    NORMAL("Normal");

    public final String label;

    private VehicleStatus(String label) {
        this.label = label;
    }

    public static VehicleStatus getEnum(String label) {
        VehicleStatus gotten = null;
        for (VehicleStatus eenum : VehicleStatus.values()) {
            if (eenum.label.equals(label)) {
                gotten = eenum;
            }
        }
        return gotten;
    }
}
