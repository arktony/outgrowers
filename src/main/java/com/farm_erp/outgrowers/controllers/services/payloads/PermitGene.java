package com.farm_erp.outgrowers.controllers.services.payloads;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class PermitGene {

    @Schema(required = true)
    public List<Long> ids;

    @JsonbDateFormat("dd/MM/yyyy")
    public LocalDate generationDate;

    @JsonbDateFormat("dd/MM/yyyy")
    public LocalDate issueDate;

    public String notes;

    @JsonbDateFormat("dd/MM/yyyy")
    public LocalDate startDate;

    @JsonbDateFormat("dd/MM/yyyy")
    public LocalDate endDate;

    public Integer startAgeInMonths;

    public Boolean isAided;

    public Long varietyId;
    public Long villageId;
    public Long cropTypeId;
    public Long districtOfficeId;

    public Double plantPercentage;
    public Double ratoonOnePercentage;
    public Double ratoonTwoPercentage;
    public Double ratoonThreePercentage;
    public Double totalExpectedTonnage;
}
