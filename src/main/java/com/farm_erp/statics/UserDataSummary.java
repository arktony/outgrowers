package com.farm_erp.statics;

public class UserDataSummary {
    public Long id;
    public String firstname;
    public String lastname;
    public String othername;
    public String email;
    public String type;
    public String phone;
    public String role;

    public UserDataSummary() {
    }

    public UserDataSummary(Long id, String firstname, String lastname, String othername, String email, String type, String phone, String role) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.othername = othername;
        this.email = email;
        this.type = type;
        this.phone = phone;
        this.role = role;
    }
}
