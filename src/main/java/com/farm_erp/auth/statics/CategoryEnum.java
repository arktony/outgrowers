package com.farm_erp.auth.statics;

public enum CategoryEnum {

	SETTINGS("Settings"),

	OUTGROWER_MANAGEMENT("Outgrower Management"),
	OUTGROWER_SETTINGS("Outgrower Settings"),

	WEIGHING_BRIDGE("Weighing Bridge"),

	PAYMENTS("Payments"),

	DASHBOARD("Dashboard"),

	ACCOUNTING("Accounting"),

	REPORTS("Reports")

	;
	public final String label;

	private  CategoryEnum(String label) {
		this.label = label;
	}

	public static  CategoryEnum getEnum(String label) {
		 CategoryEnum enumString = null;
		for ( CategoryEnum _enum :  CategoryEnum.values()) {
			if (_enum.label.equals(label)) {
				enumString = _enum;
			}
		}
		return enumString;
	}

}
