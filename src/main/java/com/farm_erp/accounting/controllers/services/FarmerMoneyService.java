package com.farm_erp.accounting.controllers.services;

import com.farm_erp.accounting.controllers.services.payloads.FarmerMoney;
import com.farm_erp.accounting.domains.FarmerMoneyActivity;
import com.farm_erp.accounting.statics.PaymentStatus;
import com.farm_erp.auth.domain.User;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;
import io.agroal.api.AgroalDataSource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FarmerMoneyService {

    @Inject
    AgroalDataSource dataSource;

    public List<FarmerMoney> getByFarmer(Long id, LocalDate start, LocalDate end) {

        Farmer farmer = Farmer.findById(id);
        if (farmer == null) throw new WebApplicationException("Invalid farmer selected", 404);

        if (start == null) start = LocalDate.now().withDayOfMonth(01);
        if (end == null) end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<FarmerMoneyActivity> acts = FarmerMoneyActivity.findByFarmer(farmer, start, end);


        List<FarmerMoney> data = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();) {

            BigDecimal balance = BigDecimal.ZERO;

            //opening balance
            BigDecimal openingBalance = BigDecimal.ZERO;
            String qry1 = """
                    select
                    sum(amount) as amount 
                    from FarmerMoneyActivity
                    where 
                    farmer_id =""" + id+
                    " and DATE(entryDate) < "+ Date.valueOf(start);
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(qry1);) {
                while (rs.next()) {
                    if(rs.getBigDecimal("amount") != null){
                        openingBalance = rs.getBigDecimal("amount");
                    }
                }
            }
            data.add(new FarmerMoney(openingBalance, "Opening Balance", "", start.minusDays(1), balance));

            //other entries
            String qry2 = """
                    select
                    amount,
                    description,
                    type,
                    entryDate
                    from FarmerMoneyActivity
                    where
                    farmer_id =""" + id+
                    " and DATE(entryDate) between '"+ start+"' and '"+end+"' order by entryDate";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(qry2);) {
                while (rs.next()) {
                    BigDecimal amount = BigDecimal.ZERO;
                    if(rs.getBigDecimal("amount") != null){
                        amount = rs.getBigDecimal("amount");
                        balance = balance.add(amount);
                    }

                    data.add(new FarmerMoney(amount, rs.getString("description"), rs.getString("type"), rs.getDate("entryDate").toLocalDate(), balance));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return data;
    }

}
