package com.farm_erp.payments.controllers.services.payloads;

import com.farm_erp.payments.statics.TransactionType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionRequest {

    public Long farmerId;

    public Long transporterId;

    @Schema(required = true)
    public BigDecimal amount;

    @Schema(required = true)
    public TransactionType type;

    public void setType(String type) {
        this.type = TransactionType.valueOf(type);

        if(type == null) throw new WebApplicationException("Invalid transaction type selected",404);
    }
}
