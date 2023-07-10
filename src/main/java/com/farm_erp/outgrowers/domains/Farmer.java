package com.farm_erp.outgrowers.domains;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.ws.rs.WebApplicationException;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import com.farm_erp.settings.domains.DistrictOffice;
import com.farm_erp.settings.domains.FarmerType;
import com.farm_erp.settings.domains.GeneralBusinessSettings;
import com.farm_erp.settings.statics._SettingParameter_Enums;
import com.farm_erp.statics._StatusTypes_Enum;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

@Entity
@Where(clause = "DELETED = 0")
public class Farmer extends PanacheEntity {

    // farmer details
    @Column(nullable = false)
    public String firstName;

    @Column(nullable = false)
    public String surName;

    public String otherName;

    @Column(nullable = false)
    public String registrationNumber;

    @Column(nullable = false)
    public String gender;

    public String identificationType;

    public String identificationNumber;

    // farmer contact
    public String airtel;

    public String mtn;

    @Email
    public String email;

    // farmer address
    public String village;

    public String parish;

    public String subcounty;

    public String district;

    // next of kin
    public String nokname;

    public String nokcontact;

    public String nokaddress;

    // statuses
    @Column(name = "DELETED")
    public Integer deleted = 0;

    public String status = _StatusTypes_Enum.PENDING.toString();

    public Boolean isVerified = Boolean.FALSE;

    public LocalDateTime entryTime = LocalDateTime.now();

    @OneToMany(mappedBy = "farmer")
    @JsonbTransient
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<Block> blocks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(nullable = false)
    public DistrictOffice districtOffice;

    @ManyToOne
    @JoinColumn(nullable = true)
    public FarmerType type;

    public Farmer() {
    }

    public Farmer(String firstName, String surName, String otherName, String gender, String identificationType,
            String identificationNumber, String airtel, String mtn, String email, String village, String parish,
            String subcounty, String district, String nokname, String nokcontact, String nokaddress,
            DistrictOffice districtOffice, FarmerType type) {
        this.firstName = firstName;
        this.surName = surName;
        this.otherName = otherName;
        this.gender = gender;
        this.identificationType = identificationType;
        this.identificationNumber = identificationNumber;
        this.airtel = airtel;
        this.mtn = mtn;
        this.email = email;
        this.village = village;
        this.parish = parish;
        this.subcounty = subcounty;
        this.district = district;
        this.nokname = nokname;
        this.nokcontact = nokcontact;
        this.nokaddress = nokaddress;
        this.registrationNumber = generateRegistrationNumber(districtOffice, type);
        this.districtOffice = districtOffice;
        this.type = type;
    }

    public static Farmer findByIDTypeNumber(String identificationType, String identificationNumber) {
        return find("identificationType=?1 and identificationNumber=?2", identificationType, identificationNumber)
                .firstResult();
    }

    public static Farmer findByIDTypeNumberExists(String identificationType, String identificationNumber, Long id) {
        return find("identificationType=?1 and identificationNumber=?2 and id!=?3", identificationType,
                identificationNumber, id).firstResult();
    }

    public static Farmer findByAirtelPhoneNumber(String airtel) {
        return find("airtel", airtel).firstResult();
    }

    public static Farmer findByAirtelPhoneNumberExists(String airtel, Long id) {
        return find("airtel=?1 and id !=?2", airtel, id).firstResult();
    }

    public static Farmer findByMtnPhoneNumber(String mtn) {
        return find("mtn", mtn).firstResult();
    }

    public static Farmer findByMtnPhoneNumberExists(String mtn, Long id) {
        return find("mtn=?1 and id !=?2", mtn, id).firstResult();
    }

    private String generateRegistrationNumber(DistrictOffice office, FarmerType type) {
        GeneralBusinessSettings set = GeneralBusinessSettings
                .single(_SettingParameter_Enums.FARMER_CODE_PREFIX.toString());
        if (set == null)
            throw new WebApplicationException("Farmer Prefix Code not set", 404);

        Optional<Farmer> zones = Farmer.findAll(Sort.by("id").descending()).firstResultOptional();

        String code = "";
        if (zones.isEmpty()) {
            code = set.settingValue + office.district.code + type.code + String.format("%04d", 1);
        } else {
            String ledgerT = zones.get().registrationNumber
                    .replace(set.settingValue + zones.get().districtOffice.district.code + zones.get().type.code, "");
            code = set.settingValue + office.district.code + type.code
                    + String.format("%04d", Integer.parseInt(ledgerT) + 1);
        }
        return code;
    }
}
