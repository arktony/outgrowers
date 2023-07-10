package com.farm_erp.auth.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Where(clause = "DELETED = 0")
public class RoleCategoryPrivilege extends PanacheEntity {

    public Boolean access = Boolean.FALSE;

    public String name;

    public String nameEnum;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonbTransient
    public RoleCategoryItem categoryItem;

    public RoleCategoryPrivilege() {
    }

    public RoleCategoryPrivilege(String name, String nameEnum, RoleCategoryItem categoryItem) {
        this.name = name;
        this.nameEnum = nameEnum;
        this.categoryItem = categoryItem;
    }
}
