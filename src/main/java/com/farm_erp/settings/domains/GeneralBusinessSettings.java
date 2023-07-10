package com.farm_erp.settings.domains;

import com.farm_erp.settings.statics._SettingParameter_Enums;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Where(clause = "DELETED = 0")
public class GeneralBusinessSettings extends PanacheEntity {

    @Column(nullable = false)
    public String settingParameter;

    @Column(nullable = false)
    public String settingValue;

    @Column(name = "DELETED")
    public Integer deleted = 0;

    public LocalDateTime entryTime = LocalDateTime.now();

    public GeneralBusinessSettings() {
    }

    public GeneralBusinessSettings(String settingParameter, String settingValue) {
        this.settingParameter = settingParameter;
        this.settingValue = settingValue;
    }

    public static GeneralBusinessSettings single(String settingParameter) {
        return find("(?1 is null or settingParameter=?1)", settingParameter).firstResult();
    }

    public static List<GeneralBusinessSettings> search(String settingParameter) {
        return list("(?1 is null or settingParameter=?1)", settingParameter);
    }

    public String getSettingParameter() {
        return _SettingParameter_Enums.valueOf(settingParameter).label;
    }
}
