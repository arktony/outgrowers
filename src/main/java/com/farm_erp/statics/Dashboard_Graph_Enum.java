package com.farm_erp.statics;

public enum Dashboard_Graph_Enum {
	THIS_WEEK("This Week"),
	LAST_7_DAYS("Previous 7 Days"),
	THIS_MONTH("This Month"),
	PREVIOUS_MONTH("Previous Month"),
	BUSINESS_QUARTER("Previous Business Quarter"),
	PREVIOUS_QUARTER("Previous Quarter"),
	QUARTER("Quarterly Report"),
	FISCAL_YEAR("Fiscal Year"),
	PREVIOUS_12_MONTHS("Previous 12 Months"),
	CUSTOM("Custom Period");

	public final String label;

	private Dashboard_Graph_Enum(String label) {
		this.label = label;
	}

	public static Dashboard_Graph_Enum getEnum(String label) {
		Dashboard_Graph_Enum gotten = null;
		for (Dashboard_Graph_Enum eenum : Dashboard_Graph_Enum.values()) {
			if (eenum.label.equals(label)) {
				gotten = eenum;
			}
		}
		return gotten;
	}
}
