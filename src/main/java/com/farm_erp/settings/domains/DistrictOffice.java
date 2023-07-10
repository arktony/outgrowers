package com.farm_erp.settings.domains;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import org.hibernate.annotations.Where;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Where(clause = "DELETED = 0")
public class DistrictOffice extends PanacheEntity {
    @Column(nullable = false)
    public String name;

    public String code;

    @OneToOne
    @JsonbTransient
    @JoinColumn(nullable = false)
    public District district;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    public LocalDateTime entryTime = LocalDateTime.now();

    @Transient
    public Dist districtData;

    public DistrictOffice() {
    }

    public DistrictOffice(String name, District district) {
        this.name = name;
        this.district = district;
        this.code = generateCode(name);
    }

    public Dist getDistrictData() {
        return new Dist(this.district.id, this.district.name, this.district.code);
    }

    public static DistrictOffice find(String name){
        return find("name", name).firstResult();
    }

    public static DistrictOffice findByCode(String code){
        return find("code", code).firstResult();
    }

    public static DistrictOffice exists(Long id, String name){
        return find("name=?1 and id!=?2", name, id).firstResult();
    }

    public static List<DistrictOffice> findByZone(District district){
        return list("?1 is null or district=?1", district);
    }

    public String generateCode(String name){
        String code = "";
        boolean available = Boolean.TRUE;
        int len  = 3;

        while (available){
            String cod = name.substring(0, len);
            DistrictOffice exists = DistrictOffice.findByCode(cod);
            if (exists == null){
                code = cod;
                available = Boolean.FALSE;
            } else {
                len += 1;
            }
        }
        return code.toUpperCase();
    }

}

class Dist{
    public Long id;
    public String name;
    public String code;

    public Dist(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public Dist() {
    }

}
