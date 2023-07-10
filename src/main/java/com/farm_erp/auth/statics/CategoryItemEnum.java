package com.farm_erp.auth.statics;

public enum CategoryItemEnum {

	//SETTINGS
	USERS("Users"),
	ROLES("Roles"),
	SETTINGS("General Business Settings"),
	DISTRICTS("Districts"),
	DISTRICT_OFFICES("District Offices"),
	VILLAGES("Villages"),


	//DASHBOARD
	DASHBOARD("Dashboard"),

	//OUTGROWER MANAGEMENT
	AID_BLOCK("Give aid to blocks"),
	BLOCKS("Blocks"),
	FARMERS("Farmers"),
	BLOCK_CYCLE("Block Ratoon"),
	PERMIT("Permits"),
	OUTGROWER_REPORTS("Out grower Reports"),

	//OUTGROWER SETTINGS
	CROP_TYPES("Crop Types"),
	AID("Aid"),
	CANE_VARIETY("Cane Varieties"),

	//WEIGHING BRIDGE
	VEHICLES("Vehicles"),
	ENTRY_GATE("Weigh bridge gate"),
	INSPECTION("Cane Inspection"),
	WEIGH_BRIDGE_MACHINE("Weigh Bridge Machine"),
	TRANSPORTERS("Transporters"),
	WB_REPORTS("Weigh Bridge Reports"),

	//PAYMENTS
	SESSIONS("Sessions"),
	TRANSACTIONS("Transactions"),

    //REPORTS

    BUSINESS("Business");
	public final String label;

	private CategoryItemEnum(String label) {
		this.label = label;
	}

	public static CategoryItemEnum getEnum(String label) {
		CategoryItemEnum enumString = null;
		for (CategoryItemEnum _enum : CategoryItemEnum.values()) {
			if (_enum.label.equals(label)) {
				enumString = _enum;
			}
		}
		return enumString;
	}

}
