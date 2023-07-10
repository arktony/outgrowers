package com.farm_erp.settings.domains;

import com.farm_erp.fileresources.domain.FileResource;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class Business extends PanacheEntity {

    public String name;

    @OneToOne
    @JoinColumn
    public FileResource logo;

    public Business() {
    }

    public Business(String name) {
        this.name = name;
    }
}
