package com.farm_erp.auth.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Where(clause = "DELETED = 0")
public class RoleCategoryItem extends PanacheEntity {
    public Boolean access = Boolean.FALSE;

    @NotNull
    @Column(nullable = false)
    public String name;

    @NotNull
    @Column(nullable = false)
    public String nameEnum;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    public String description;

    @OneToMany(mappedBy = "categoryItem")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<RoleCategoryPrivilege> privileges = new ArrayList<>();

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonbTransient
    public RoleCategory category;

    public RoleCategoryItem() {
    }

    public RoleCategoryItem(String name, String nameEnum, String description, RoleCategory category) {
        this.name = name;
        this.nameEnum = nameEnum;
        this.description = description;
        this.category = category;
    }
}
