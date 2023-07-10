package com.farm_erp.statics;

public enum Dashboard_Card_Enum {
	LAST_7_DAYS("7D"),
	LAST_1_MONTH("1M"),
	LAST_1_YEAR("1Y"),
	START_OF_BUSINESS("Max");

	public final String label;

	private Dashboard_Card_Enum(String label) {
		this.label = label;
	}

	public static Dashboard_Card_Enum getEnum(String label) {
		Dashboard_Card_Enum gotten = null;
		for (Dashboard_Card_Enum eenum : Dashboard_Card_Enum.values()) {
			if (eenum.label.equals(label)) {
				gotten = eenum;
			}
		}
		return gotten;
	}
}
