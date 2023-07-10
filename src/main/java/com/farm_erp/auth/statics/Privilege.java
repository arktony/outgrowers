package com.farm_erp.auth.statics;

public enum Privilege {
	VIEW("View"),
	CREATE("Create"),
	UPDATE("Update"),
	DELETE("Delete"),

	APPROVE("Approve"),
	PUBLISH("Publish"),
	DECLINE("Decline"),
	REJECT("Reject"),
	REVERSEREJECT("Un-Reject"),
	REVERSEDECLINE("Un-Decline"),

	ACTIVATE("Activate"),
	DEACTIVATE("De-activate"),

	VERIFY("Verify"),
	REINSTATE("Reinstate"),
	SUSPEND("Suspend"),
	CHANGE_PRIORITY("Change Priority"),
	VERIFY_PERMIT("Verify Permit"),
	SCAN_RFID("Scan Car Code"),
	INSPECT("Inspect"),
	MARK_AS_PENDING("Mark token as pending"),
	MARK_AS_AVAILABLE("Mark token as available"),
	PAY("Mark payments as paid"),
	END("End Permit"),
	EXTEND("Extend Permit"),
	CLOSE("Close Session")

	;

	public final String label;

	private Privilege(String label) {
		this.label = label;
	}

	public static Privilege getEnum(String label) {
		Privilege enumString = null;
		for (Privilege _enum : Privilege.values()) {
			if (_enum.label.equals(label)) {
				enumString = _enum;
			}
		}
		return enumString;
	}

}
