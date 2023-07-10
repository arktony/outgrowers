package com.farm_erp.configurations.handler;

public enum _Messages_Enum {
    SUCCESS("Success!"),
    SAVED("Saved successfully!"),
    UPDATED("Updated successfully!"),
    DELETED("Deleted successfully!"),
    REMOVED("Removed successfully!"),
    ADDED("Added successfully!"),
    APPROVED("Approved successfully!"),
    DECLINED("Declined successfully!"),
    COMPLETED("Declined successfully!"),
    DISBURSED("Disbursed successfully!"),
    ACTIVATED("Activated successfully!"),
    VERIFIED("Verified successfully!"),
    SUSPENDED("Suspended successfully!"),
    REINSTATED("Reinstated successfully!"),
    FETCHED("Fetched successfully!"),
    REJECTED("Rejected successfully!"),
    REVERSED("Reversed successfully!"),
    DEACTIVATED("Deactivated successfully!"),
    INSPECTED("Inspected successfully!"),
    CLOSED("Closed successfully!");

    public final String label;

    private _Messages_Enum(String label) {
        this.label = label;
    }
}
