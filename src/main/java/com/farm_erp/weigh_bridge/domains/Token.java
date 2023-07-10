package com.farm_erp.weigh_bridge.domains;

import com.farm_erp.weigh_bridge.statics.WeighBridgeStatusEnums;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import org.hibernate.annotations.Where;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.Optional;

@Entity
@Where(clause = "DELETED = 0")
public class Token extends PanacheEntity {
    @Column(name = "DELETED")
    public Integer deleted = 0;

    @Column(nullable = false)
    public String token;
    
    public String status = WeighBridgeStatusEnums.NEW.toString();
    
    public Integer priority;
    
    @OneToOne
    @JsonbTransient
    @JoinColumn(nullable = false)
    public WeighBridgeTicket ticket;

    public Token() {
    }

    public Token(WeighBridgeTicket ticket) {
        this.token = generateToken();
        this.ticket = ticket;

//        switch (ticket.vehicle.priority){
//            case "IMPORTANT":
//                this.priority = 1;
//                break;
//            case "CRITICAL":
//                this.priority = 2;
//                break;
//            case "NORMAL":
//                this.priority = 3;
//                break;
//            default:
//                break;
//        }
    }

    public static List<Token> getNewTokens(){
        return list("status", Sort.by("priority", "token").ascending(), WeighBridgeStatusEnums.NEW.toString());
    }

    public static List<Token> getUnloadedTokens(){
        return list("status", Sort.by("priority", "token").ascending(), WeighBridgeStatusEnums.WEIGHED.toString());
    }

    private String generateToken(){
        Optional<Token> zones = Token.findAll(Sort.by("token").ascending()).firstResultOptional();

        String code = "";
        if (zones.isEmpty()) {
            code = String.format("%04d", 1);
        } else {
            code = String.format("%04d", Integer.parseInt(zones.get().token) + 1);
        }

        return code;
    }
}
