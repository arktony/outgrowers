package com.farm_erp.statics;

public enum _StatusTypes_Enum {
    //general
    ACTIVE, DEACTIVATED, OPEN, 

    //action opiniated
    PENDING, APPROVED, VERIFIED, REJECTED, EXPIRED, ENDED,
    
    //journals
    DRAFT, PUBLISHED, DECLINED, DELETED,
    
    // inventory 
    TRANSFERRED, CLOSED, ISSUE, RECEIVED,

    //banking
    REVERSED,
    
    //loan application
    NEW,PROCESSING,
    
    // email
    SENT,

    //contract
    SUSPENDED,REINSTATED,
    

}

