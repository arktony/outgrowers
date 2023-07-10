package com.farm_erp.outgrowers.controllers.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.auth.domain.User;
import com.farm_erp.outgrowers.controllers.services.payloads.FarmerRequest;
import com.farm_erp.outgrowers.controllers.services.payloads.FarmerUpdateRequest;
import com.farm_erp.outgrowers.domains.Block;
import com.farm_erp.outgrowers.domains.BlockCropType;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.outgrowers.domains.FarmerSingle;
import com.farm_erp.settings.domains.CaneVariety;
import com.farm_erp.settings.domains.CropType;
import com.farm_erp.settings.domains.DistrictOffice;
import com.farm_erp.settings.domains.FarmerType;
import com.farm_erp.settings.domains.GeneralBusinessSettings;
import com.farm_erp.settings.domains.Village;
import com.farm_erp.settings.statics._SettingParameter_Enums;
import com.farm_erp.statics._StatusTypes_Enum;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;

import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class FarmerService {
    public Farmer create(FarmerRequest request, User user) {

        Farmer exists = Farmer.findByIDTypeNumber(request.identificationType, request.identificationNumber);
        if (exists != null)
            throw new WebApplicationException("ID number already registered!", 406);

        if (request.airtel != null && !request.airtel.startsWith("+256"))
            throw new WebApplicationException("Airtel number should start with +256!", 404);
        if (request.mtn != null && !request.mtn.startsWith("+256"))
            throw new WebApplicationException("MTN number should start with +256!", 404);

        if (request.airtel != null && request.airtel.length() != 13)
            throw new WebApplicationException("Invalid Airtel number!", 404);
        if (request.mtn != null && request.mtn.length() != 13)
            throw new WebApplicationException("Invalid MTN number!", 404);

        Farmer exists1 = Farmer.findByMtnPhoneNumber(request.mtn);
        if (exists1 != null)
            throw new WebApplicationException("MTN number already registered!", 406);

        Farmer exists2 = Farmer.findByAirtelPhoneNumber(request.airtel);
        if (exists2 != null)
            throw new WebApplicationException("Airtel number already registered!", 406);

        DistrictOffice office = DistrictOffice.findById(request.districtOfficeId);
        if (office == null)
            throw new WebApplicationException("Invalid district office selected", 404);

        FarmerType type = FarmerType.findById(request.typeId);
        if (type == null)
            throw new WebApplicationException("Invalid farmer type selected", 404);

        Farmer farmer = new Farmer(
                request.firstName,
                request.surName,
                request.otherName,
                request.gender,
                request.identificationType,
                request.identificationNumber,
                request.airtel,
                request.mtn,
                request.email,
                request.village,
                request.parish,
                request.subcounty,
                request.district,
                request.nokname,
                request.nokcontact,
                request.nokaddress,
                office,
                type);
        farmer.persist();

        // blocks
        request.blocks.forEach(req -> {
            Village village = Village.findById(req.villageId);
            if (village == null)
                throw new WebApplicationException("Invalid village selected", 404);

            CaneVariety variety = CaneVariety.findById(req.caneVarietyId);
            if (variety == null)
                throw new WebApplicationException("Invalid variety selected", 404);

            Block block = new Block(req.area, req.distance, req.landOwnership, farmer, village, variety);
            block.persist();

            CropType cropType = CropType.findById(req.cropTypeId);
            if (cropType == null)
                throw new WebApplicationException("Invalid crop type selected", 404);

            BlockCropType btype = new BlockCropType(req.plantingDate, block, cropType);
            btype.persist();

        });

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.FARMERS.toString(), farmer.id,
                null, jsonb.toJson(farmer), user);
        trail.persist();

        return farmer;
    }

    public Farmer update(Long id, FarmerUpdateRequest request, User user) {

        Farmer farmer = Farmer.findById(id);
        if (farmer == null)
            throw new WebApplicationException("Invalid farmer selected!", 404);

        Farmer exists = Farmer.findByIDTypeNumberExists(request.identificationType, request.identificationNumber, id);
        if (exists != null)
            throw new WebApplicationException("ID number already registered!", 406);

        DistrictOffice office = DistrictOffice.findById(request.districtOfficeId);
        if (office == null)
            throw new WebApplicationException("Invalid district office selected", 404);

        FarmerType type = FarmerType.findById(request.typeId);
        if (type == null)
            throw new WebApplicationException("Invalid farmer type selected", 404);

        Farmer oldData = farmer;

        farmer.surName = request.surName;
        farmer.firstName = request.firstName;
        farmer.identificationType = request.identificationType;
        farmer.identificationNumber = request.identificationNumber;
        farmer.otherName = request.otherName;
        farmer.gender = request.gender;
        farmer.airtel = request.airtel;
        farmer.mtn = request.mtn;
        farmer.email = request.email;
        farmer.village = request.village;
        farmer.parish = request.parish;
        farmer.subcounty = request.subcounty;
        farmer.district = request.district;
        farmer.nokname = request.nokname;
        farmer.nokcontact = request.nokcontact;
        farmer.nokaddress = request.nokaddress;

        if (farmer.districtOffice != office || farmer.type != type) {
            GeneralBusinessSettings set = GeneralBusinessSettings
                    .single(_SettingParameter_Enums.FARMER_CODE_PREFIX.toString());
            String ledgerT = farmer.registrationNumber
                    .replace(set.settingValue + farmer.districtOffice.district.code + farmer.type.code, "");

            farmer.registrationNumber = set.settingValue + office.district.code + type.code + ledgerT;

            farmer.districtOffice = office;
            farmer.type = type;

        }

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.FARMERS.toString(),
                farmer.id,
                jsonb.toJson(oldData), jsonb.toJson(farmer), user);
        trail.persist();

        return farmer;
    }

    public Farmer verify(Long id, User user) {
        Farmer farmer = Farmer.findById(id);
        if (farmer == null)
            throw new WebApplicationException("Invalid farmer selected!", 404);

        if (!farmer.status.equals(_StatusTypes_Enum.PENDING.toString()))
            throw new WebApplicationException("Only pending farmers can be approved", 406);

        Farmer oldData = farmer;
        farmer.isVerified = Boolean.TRUE;
        farmer.status = _StatusTypes_Enum.ACTIVE.toString();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.VERIFIED.toString(), _Section_Enums.FARMERS.toString(),
                farmer.id,
                jsonb.toJson(oldData), jsonb.toJson(farmer), user);
        trail.persist();

        return farmer;
    }

    public Farmer activate(Long id, User user) {
        Farmer farmer = Farmer.findById(id);
        if (farmer == null)
            throw new WebApplicationException("Invalid farmer selected!", 404);

        if (!farmer.status.equals(_StatusTypes_Enum.VERIFIED.toString()))
            throw new WebApplicationException("Only verified farmers can be activated", 406);

        Farmer oldData = farmer;

        farmer.status = _StatusTypes_Enum.ACTIVE.toString();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.ACTIVATED.toString(), _Section_Enums.FARMERS.toString(),
                farmer.id,
                jsonb.toJson(oldData), jsonb.toJson(farmer), user);
        trail.persist();

        return farmer;
    }

    public Farmer suspend(Long id, User user) {
        Farmer farmer = Farmer.findById(id);
        if (farmer == null)
            throw new WebApplicationException("Invalid farmer selected!", 404);

        if (!farmer.status.equals(_StatusTypes_Enum.ACTIVE.toString()))
            throw new WebApplicationException("Only active farmers can be suspended", 406);

        Farmer oldData = farmer;

        farmer.status = _StatusTypes_Enum.SUSPENDED.toString();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SUSPENDED.toString(), _Section_Enums.FARMERS.toString(),
                farmer.id,
                jsonb.toJson(oldData), jsonb.toJson(farmer), user);
        trail.persist();

        return farmer;
    }

    public Farmer reinstate(Long id, User user) {
        Farmer farmer = Farmer.findById(id);
        if (farmer == null)
            throw new WebApplicationException("Invalid farmer selected!", 404);

        if (!farmer.status.equals(_StatusTypes_Enum.SUSPENDED.toString()))
            throw new WebApplicationException("Only suspended farmers can be reinstated", 406);

        Farmer oldData = farmer;

        farmer.status = _StatusTypes_Enum.REINSTATED.toString();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.REINSTATED.toString(), _Section_Enums.FARMERS.toString(),
                farmer.id,
                jsonb.toJson(oldData), jsonb.toJson(farmer), user);
        trail.persist();

        return farmer;
    }

    public List<Farmer> get() {
        return Farmer.listAll(Sort.by("id"));
    }

    public FarmerSingle getSingle(Long id) {
        Farmer farmer = Farmer.findById(id);
        if (farmer == null)
            throw new WebApplicationException("Invalid farmer selected", 404);

        FarmerSingle single = new FarmerSingle();
        single.farmer = farmer;
        single.blocks = farmer.blocks;

        return single;
    }

    public Farmer delete(Long id, User user) {
        Farmer farmer = Farmer.findById(id);
        if (farmer == null)
            throw new WebApplicationException("Invalid farmer selected!", 404);

        Farmer oldData = farmer;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.FARMERS.toString(),
                farmer.id,
                jsonb.toJson(oldData), jsonb.toJson(farmer), user);
        trail.persist();

        farmer.deleted = 1;

        return farmer;
    }
}
