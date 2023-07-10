package com.farm_erp.weigh_bridge.domains;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Where(clause = "DELETED = 0")
public class Transporter extends PanacheEntity {
    @Column(name = "DELETED")
    public Integer deleted = 0;

    @Column(nullable = false)
    public String name;

    @Column(nullable = true)
    public String accountName;

    @Column(nullable = true)
    public String accountNumber;

    @OneToMany(mappedBy = "transporter")
    @JsonbTransient
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<Vehicle> vehicles = new ArrayList<>();

    public LocalDateTime entryTime = LocalDateTime.now();

    public Transporter() {
    }

    public Transporter(String name, String accountName, String accountNumber) {
        this.name = name;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }

    public static Transporter findByNameAccount(String accountName, String accountNumber, Long id){
        return find("accountName=?1 and accountNumber=?2 and (?3 is null or id != ?3)",accountName,accountNumber,id).firstResult();
    }
}
