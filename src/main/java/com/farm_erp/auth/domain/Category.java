package com.farm_erp.auth.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

@Entity
@Where(clause = "DELETED = 0")
public class Category extends PanacheEntity {

    @NotNull
    @Column(nullable = false)
    public String name;

    @NotNull
    @Column(nullable = false)
    public String nameEnum;

    public String description;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    @OneToMany(mappedBy = "category")
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<CategoryItem> items = new ArrayList<>();

    public Category() {
    }

    public Category(String name, String nameEnum, String description) {
        this.name = name;
        this.nameEnum = nameEnum;
        this.description = description;
    }
}
