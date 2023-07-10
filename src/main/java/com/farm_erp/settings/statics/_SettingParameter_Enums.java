package com.farm_erp.settings.statics;

public enum _SettingParameter_Enums {

    PERMIT_GRACE_PERIOD("Permit Grace Period"),
    MATURITY_PERIOD("Maturity Period"),
    AVERAGE_TONNES_PER_DAY("Average Tonnes per day"),
    FARMER_CODE_PREFIX("Farmer Code Prefix"),
    COMPULSORY_DEDUCTION("Compulsory deduction"),
    PAYMENT_PER_TONNE("Payment per tonne of canes")
    ;

    public final String label;

    private _SettingParameter_Enums(String label) {
        this.label = label;
    }

    public static _SettingParameter_Enums getEnum(String label) {
        _SettingParameter_Enums enumString = null;
        for ( _SettingParameter_Enums _enum :  _SettingParameter_Enums.values()) {
            if (_enum.label.equals(label)) {
                enumString = _enum;
            }
            break;
        }
        return enumString;
    }
}
