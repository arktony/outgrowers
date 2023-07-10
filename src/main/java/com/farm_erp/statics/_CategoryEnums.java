package com.farm_erp.statics;

public enum _CategoryEnums {
    DAILY("Daily"), WEEKLY("Weekly"), MONTHLY("Monthly"), QUARTERLY("Quarterly"), ANNUALLY("Annually");

    public final String label;

    private _CategoryEnums(String label) {
        this.label = label;
    }

    public static _CategoryEnums getEnum(String label) {
        _CategoryEnums gotten = null;
        for (_CategoryEnums eenum : _CategoryEnums.values()) {
            if (eenum.label.equals(label)) {
                gotten = eenum;
            }
        }
        return gotten;
    }
}
