package com.farm_erp.dashboard.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.farm_erp.auth.domain.User;
import com.farm_erp.dashboard.service.payloads.SalesResponse;
import com.farm_erp.dashboard.service.payloads.SeriesDataModel;
import com.farm_erp.dashboard.service.payloads.SingleCardValue;
import com.farm_erp.settings.domains.Business;
import com.farm_erp.statics.DashboardPeriodGenerator;
import com.farm_erp.statics.Dashboard_Graph_Enum;
import com.farm_erp.statics.DatePeriod;
import com.farm_erp.statics.TimeConverter;
import com.farm_erp.statics._StatusTypes_Enum;

import io.agroal.api.AgroalDataSource;

@ApplicationScoped
public class DashboardService {

    @Inject
    AgroalDataSource dataSource;

    @Inject
    DashboardPeriodGenerator dashboardPeriodGenerator;


    public List<SingleCardValue> singleCard(User user) {
        Business business = user.business;

        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now();

        LocalDate startpast = LocalDate.now().withDayOfMonth(1).minusDays(1).withDayOfMonth(1);
        LocalDate endpast = LocalDate.now().withDayOfMonth(1).minusDays(1);

        List<SingleCardValue> sectionData = new ArrayList<>();

        // Active Farmers
        SingleCardValue v1 = new SingleCardValue();
        v1.name = "Active Farmers";
        v1.change = 0.0;
        v1.value = 0.0;
        String qry = """
                select count(*) as number from Farmer where"""
                + " status = '" + _StatusTypes_Enum.ACTIVE + "'";
        try (
                Connection conn = dataSource.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(qry);) {

            while (rs.next()) {
                v1.value = rs.getDouble("number");

                String qry1 = """
                        select count(*) as number from Farmer where"""
                        + " status = '" + _StatusTypes_Enum.ACTIVE + "'"
                        + " and entryTime > '" + start.atStartOfDay() + "'";
                try (
                        Statement st = conn.createStatement();
                        ResultSet rs1 = st.executeQuery(qry1);) {
                    while (rs1.next()) {
                        if (rs1.getDouble("number") != 0)
                            v1.change = ((rs.getDouble("number") - rs1.getDouble("number")) / rs1.getDouble("number")) * 100;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        sectionData.add(v1);

        // Pending Farmers
        SingleCardValue v11 = new SingleCardValue();
        v11.name = "Pending Farmers";
        v11.change = 0.0;
        v11.value = 0.0;
        String qry1 = """
                select count(*) as number from Farmer where"""
                + " status = '" + _StatusTypes_Enum.PENDING + "'";
        try (
                Connection conn = dataSource.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(qry1);) {

            while (rs.next()) {
                v11.value = rs.getDouble("number");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        sectionData.add(v11);

        // Active Blocks
        SingleCardValue v2 = new SingleCardValue();
        v2.name = "Active Blocks";
        v2.change = 0.0;
        v2.value = 0.0;
        String qry2 = """
                select count(*) as number from Block where"""
                + " status = '" + _StatusTypes_Enum.ACTIVE + "'";
        try (
                Connection conn = dataSource.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs2 = statement.executeQuery(qry2);) {

            while (rs2.next()) {
                v2.value = rs2.getDouble("number");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        sectionData.add(v2);

        // Pending Blocks
        SingleCardValue v21 = new SingleCardValue();
        v21.name = "Pending Blocks";
        v21.change = 0.0;
        v21.value = 0.0;
        String qry21 = """
                select count(*) as number from Block where"""
                + " status = '" + _StatusTypes_Enum.PENDING + "'";
        try (
                Connection conn = dataSource.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs2 = statement.executeQuery(qry21);) {

            while (rs2.next()) {
                v21.value = rs2.getDouble("number");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        sectionData.add(v21);

        // permits
        SingleCardValue v3 = new SingleCardValue();
        v3.name = "Active Permits";
        v3.change = 0.0;
        v3.value = 0.0;
        String qry3 = """
                select count(*) as number 
                from Permit where"""
                + " status = '" + _StatusTypes_Enum.ACTIVE + "'";
        try (
                Connection conn = dataSource.getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs3 = statement.executeQuery(qry3);) {

            while (rs3.next()) {
                v3.value = rs3.getDouble("number");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        sectionData.add(v3);

        return sectionData;
    }

    public SalesResponse blocks(Dashboard_Graph_Enum periodEnum, LocalDate startDate, LocalDate endDate,
                                User user) {
        Business business = user.business;

        DatePeriod period = dashboardPeriodGenerator.setPeriodDates(periodEnum, startDate, endDate);
        LocalDate end = period.end;
        LocalDate start = period.start;

        SalesResponse data = new SalesResponse();

        try (Connection connection = dataSource.getConnection();) {
            if (connection != null) {
                SeriesDataModel mod = new SeriesDataModel();
                mod.name = "Blocks";

                while (start.isBefore(end)) {

                    String datequery = switch (period.labelRange) {
                        case "DAYS" -> "DATE(d.entryTime) between '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayStart(start))
                                + "' and '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayEnd(start)) + "'";
                        case "MONTHS" -> "DATE(d.entryTime) between '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayStart(start))
                                + "' and '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayEnd(start.with(TemporalAdjusters.lastDayOfMonth())))
                                + "'";
                        case "WEEKS" -> "DATE(d.entryTime) between '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayStart(start))
                                + "' and '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayEnd(start.plusWeeks(1))) + "'";
                        case "YEARS" -> "DATE(d.entryTime) between '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayStart(start))
                                + "' and '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayEnd(start.with(TemporalAdjusters.lastDayOfYear())))
                                + "'";
                        default -> "";
                    };

                    Double res = 0.0;
                    String qry = """
                            select count(*) as blocks from Block d
                            where""" + " "+datequery;

                    try (
                            Statement statement = connection.createStatement();
                            ResultSet resultSet = statement.executeQuery(qry);) {
                        while (resultSet.next()) {
                            res = resultSet.getDouble("blocks");
                        }
                    } catch (Exception e) {
                    }

                    mod.data.add(res);

                    switch (period.labelRange) {
                        case "DAYS" -> {
                            data.labels.add(dashboardPeriodGenerator.labelGenerator(period.labelRange, start));
                            start = start.plusDays(1);
                        }
                        case "MONTHS" -> {
                            data.labels.add(dashboardPeriodGenerator.labelGenerator(period.labelRange, start));
                            start = start.plusMonths(1);
                        }
                        case "WEEKS" -> {
                            data.labels.add(dashboardPeriodGenerator.labelGenerator(period.labelRange, start));
                            start = start.plusWeeks(1);
                        }
                        case "YEARS" -> {
                            data.labels.add(dashboardPeriodGenerator.labelGenerator(period.labelRange, start));
                            start = start.plusYears(1);
                        }
                    }

                }
                data.series.add(mod);

            }
        } catch (Exception e) {
        }
        return data;
    }

    public SalesResponse haarvests(Dashboard_Graph_Enum periodEnum, LocalDate startDate, LocalDate endDate,
                                   User user) {
        Business business = user.business;

        DatePeriod period = dashboardPeriodGenerator.setPeriodDates(periodEnum, startDate, endDate);
        LocalDate end = period.end;
        LocalDate start = period.start;

        SalesResponse data = new SalesResponse();

        try (Connection connection = dataSource.getConnection();) {
            if (connection != null) {
                SeriesDataModel mod = new SeriesDataModel();
                mod.name = "Cane Harvests";

                while (start.isBefore(end)) {

                    String datequery = switch (period.labelRange) {
                        case "DAYS" -> "DATE(d.entryTime) between '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayStart(start))
                                + "' and '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayEnd(start)) + "'";
                        case "MONTHS" -> "DATE(d.entryTime) between '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayStart(start))
                                + "' and '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayEnd(start.with(TemporalAdjusters.lastDayOfMonth())))
                                + "'";
                        case "WEEKS" -> "DATE(d.entryTime) between '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayStart(start))
                                + "' and '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayEnd(start.plusWeeks(1))) + "'";
                        case "YEARS" -> "DATE(d.entryTime) between '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayStart(start))
                                + "' and '"
                                + TimeConverter.EpochMils_to_LocalDateTimeFormatted(TimeConverter.LocalDate_to_EpochMilli_DayEnd(start.with(TemporalAdjusters.lastDayOfYear())))
                                + "'";
                        default -> "";
                    };

                    Double res = 0.0;
                    String qry = """
                            select sum(deliveredQuantity) as harvest from DeliveryNote d
                            where """ + " "+datequery;

                    try (
                            Statement statement = connection.createStatement();
                            ResultSet resultSet = statement.executeQuery(qry);) {
                        while (resultSet.next()) {
                            res = resultSet.getDouble("harvest");
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    mod.data.add(res);

                    switch (period.labelRange) {
                        case "DAYS" -> {
                            data.labels.add(dashboardPeriodGenerator.labelGenerator(period.labelRange, start));
                            start = start.plusDays(1);
                        }
                        case "MONTHS" -> {
                            data.labels.add(dashboardPeriodGenerator.labelGenerator(period.labelRange, start));
                            start = start.plusMonths(1);
                        }
                        case "WEEKS" -> {
                            data.labels.add(dashboardPeriodGenerator.labelGenerator(period.labelRange, start));
                            start = start.plusWeeks(1);
                        }
                        case "YEARS" -> {
                            data.labels.add(dashboardPeriodGenerator.labelGenerator(period.labelRange, start));
                            start = start.plusYears(1);
                        }
                    }

                }
                data.series.add(mod);

            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return data;
    }

}
