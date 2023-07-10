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
public class RoleCategory extends PanacheEntity {

    public Boolean access = Boolean.FALSE;

    @NotNull
    @Column(nullable = false)
    public String name;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    @NotNull
    @Column(nullable = false)
    public String nameEnum;

    public String description;

    @OneToMany(mappedBy = "category")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<RoleCategoryItem> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonbTransient
    public Role role;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonbTransient
    public Category category;

    public RoleCategory() {
    }

    public RoleCategory(String name, String nameEnum, String description, Role role, Category category) {
        this.name = name;
        this.nameEnum = nameEnum;
        this.description = description;
        this.role = role;
        this.category = category;
    }
}
