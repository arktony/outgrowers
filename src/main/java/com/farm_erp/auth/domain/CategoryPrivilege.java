package com.farm_erp.auth.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

@Entity
@Where(clause = "DELETED = 0")
public class CategoryPrivilege extends PanacheEntity {

    public String name;

    public String nameEnum;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    @ManyToOne
    @JoinColumn(nullable = false)
    public CategoryItem categoryItem;

    public CategoryPrivilege() {
    }

    public CategoryPrivilege(String name, String nameEnum, CategoryItem categoryItem) {
        this.name = name;
        this.nameEnum = nameEnum;
        this.categoryItem = categoryItem;
    }
}
