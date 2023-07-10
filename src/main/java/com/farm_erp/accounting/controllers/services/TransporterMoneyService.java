package com.farm_erp.accounting.controllers.services;

import com.farm_erp.accounting.controllers.services.payloads.TransporterMoney;
import com.farm_erp.accounting.domains.TransporterMoneyActivity;
import com.farm_erp.weigh_bridge.domains.Transporter;
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
public class TransporterMoneyService {
    @Inject
    AgroalDataSource dataSource;

    public List<TransporterMoney> getByTransporter(Long id, LocalDate start, LocalDate end) {

        Transporter transporter = Transporter.findById(id);
        if (transporter == null) throw new WebApplicationException("Invalid transporter selected", 404);

        if (start == null) start = LocalDate.now().withDayOfMonth(01);
        if (end == null) end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<TransporterMoneyActivity> acts = TransporterMoneyActivity.findByTransporter(transporter, start, end);


        List<TransporterMoney> data = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();) {

            BigDecimal balance = BigDecimal.ZERO;

            //opening balance
            BigDecimal openingBalance = BigDecimal.ZERO;
            String qry1 = """
                    select
                    sum(amount) as amount 
                    from TransporterMoneyActivity
                    where 
                    transporter_id =""" + id+
                    " and DATE(entryDate) < "+ Date.valueOf(start);
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(qry1);) {
                while (rs.next()) {
                    if(rs.getBigDecimal("amount") != null){
                        openingBalance = rs.getBigDecimal("amount");
                    }
                }
            }
            data.add(new TransporterMoney(openingBalance, "Opening Balance", "", start.minusDays(1), balance));

            //other entries
            String qry2 = """
                    select
                    amount,
                    description,
                    type,
                    entryDate
                    from TransporterMoneyActivity
                    where
                    transporter_id =""" + id+
                    " and DATE(entryDate) between '"+ start+"' and '"+end+"' order by entryDate";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(qry2);) {
                while (rs.next()) {
                    BigDecimal amount = BigDecimal.ZERO;
                    if(rs.getBigDecimal("amount") != null){
                        amount = rs.getBigDecimal("amount");
                        balance = balance.add(amount);
                    }

                    data.add(new TransporterMoney(amount, rs.getString("description"), rs.getString("type"), rs.getDate("entryDate").toLocalDate(), balance));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return data;
    }
}
