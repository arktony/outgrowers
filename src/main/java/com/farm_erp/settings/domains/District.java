package com.farm_erp.settings.domains;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Where(clause = "DELETED = 0")
public class District extends PanacheEntity {
    @Column(nullable = false)
    public String name;

    public String code;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    public LocalDateTime entryTime = LocalDateTime.now();

    @OneToOne(mappedBy = "district")
    public DistrictOffice districtOffice;

    public District() {
    }

    public District(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static District findByCode(Long id, String name, String code){
        return find("(?1 is null or id!=?1) and (name=?2 or code=?3)", id, name, code).firstResult();
    }

    public static District find(String name){
        return find("name",name).firstResult();
    }

    public static District exists(String name, Long id){
        return find("name=?1 and id!=?2",name, id).firstResult();
    }
}

