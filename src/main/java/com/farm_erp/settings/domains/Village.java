package com.farm_erp.settings.domains;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Where;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

@Entity
@Where(clause = "DELETED = 0")
public class Village extends PanacheEntity {
    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String code;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    @ManyToOne
    @JoinColumn(nullable = false)
    public District district;

    public LocalDateTime entryTime = LocalDateTime.now();

    public Village() {
    }

    public Village(String name, District district) {
        this.name = name;
        this.code = generateCode(district);
        this.district = district;
    }

    public static Village findByName(String name) {
        return find("name", name).firstResult();
    }

    public static Village findByCode(String code) {
        return find("code", code).firstResult();
    }

    public static Village findByNameExists(Long id, String name) {
        return find("name=?1 and id!=?2", name, id).firstResult();
    }

    public static List<Village> findByDistrict(District district) {
        return list("?1 is null or district=?1", district);
    }

    public String generateCode(District district) {
        Optional<Village> zones = Village.findAll(Sort.by("id").descending()).firstResultOptional();

        String code = "";
        if (zones.isEmpty()) {
            code = district.code + String.format("%01d", 1);
        } else {
            String ledgerT = zones.get().code.replace(zones.get().district.code, "");
            code = district.code + String.format("%01d", Integer.parseInt(ledgerT) + 1);
        }
        return code;
    }
}