package com.farm_erp.settings.domains;

import com.farm_erp.auth.domain.User;
import com.farm_erp.statics.UserDataSummary;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Where(clause = "DELETED = 0")
public class CanePrice extends PanacheEntity {
    @Column(nullable = false)
    public BigDecimal oldPrice;

    @Column(nullable = false)
    public BigDecimal newPrice;

    public LocalDateTime entryTime = LocalDateTime.now();

    @Column(name = "DELETED")
    public Integer deleted = 0;

    @ManyToOne
    @JsonbTransient
    @JoinColumn(nullable = false)
    public User user;

    @Transient
    public UserDataSummary userData;

    @Transient
    public String entryDateTime;

    public CanePrice() {
    }

    public CanePrice(BigDecimal oldPrice, BigDecimal newPrice, User user) {
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.user = user;
    }

    public UserDataSummary getUserData() {
        return new UserDataSummary(
                this.user.id,
                this.user.firstname,
                this.user.lastname,
                this.user.othername,
                this.user.email,
                this.user.role.type,
                this.user.phone,
                this.user.role.name
        );
    }

    public String getEntryDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(entryTime);
    }
}
