package com.farm_erp.outgrowers.controllers.services;

import com.farm_erp.auth.domain.User;
import com.farm_erp.configurations.handler.Base64DataResponse;
import com.farm_erp.outgrowers.controllers.services.payloads.PermitGene;
import com.farm_erp.outgrowers.controllers.services.payloads.PermitGenerator;
import com.farm_erp.outgrowers.controllers.services.payloads.PermitRequest;
import com.farm_erp.outgrowers.domains.*;
import com.farm_erp.settings.domains.*;
import com.farm_erp.settings.statics._SettingParameter_Enums;
import com.farm_erp.statics.TimeConverter;
import com.farm_erp.statics._StatusTypes_Enum;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;
import com.farm_erp.utilities.GeneralPDFMethods;
import com.farm_erp.weigh_bridge.domains.DeliveryNote;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.agroal.api.AgroalDataSource;
import jxl.format.Border;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class PermitService {

    @Inject
    public AgroalDataSource dataSource;

    public void generate(PermitGene request, User user) {
        GeneralBusinessSettings grace = GeneralBusinessSettings.single(_SettingParameter_Enums.PERMIT_GRACE_PERIOD.toString());
        GeneralBusinessSettings expTone = GeneralBusinessSettings.single(_SettingParameter_Enums.AVERAGE_TONNES_PER_DAY.toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy");

        LocalDate date = Objects.requireNonNullElseGet(request.issueDate, LocalDate::now);
        List<Permit> permits = new ArrayList<>();

        DistrictOffice office = null;
        if (request.districtOfficeId != null) {
            office = DistrictOffice.findById(request.districtOfficeId);
            if (office == null) throw new WebApplicationException("Invalid district office selected", 404);

        }

        Village village = null;
        if (request.villageId != null) {
            village = Village.findById(request.villageId);
            if (village == null) throw new WebApplicationException("Invalid village selected", 404);

        }

        CaneVariety variety = null;
        if (request.varietyId != null) {
            variety = CaneVariety.findById(request.varietyId);
            if (variety == null) throw new WebApplicationException("Invalid cane variety selected", 404);

        }

        CropType type = null;
        if (request.cropTypeId != null) {
            type = CropType.findById(request.cropTypeId);
            if (type == null) throw new WebApplicationException("Invalid crop type selected", 404);

        }

        Boolean isAided = null;
        if (request.isAided != null) {
            isAided = request.isAided;
        }

        for (Long id : request.ids) {
            Block block = Block.findById(id);
            if (block == null) throw new WebApplicationException("Invalid block selected", 404);

            if (block.hasActivePermit) throw new WebApplicationException("Block has an active permit", 406);

            if (block.getActiveRatoon() == null)
                throw new WebApplicationException("Block does not have an active ratoon", 404);

            Double expectedYield = block.getActiveRatoon().getEstimatedYield();

            long validity = (long) (((expectedYield) / Double.parseDouble(expTone.settingValue)) + Double.parseDouble(grace.settingValue));

            LocalDate expiry = date.plusDays(validity);

            String validityPeriod = formatter.format(date) + " - " + formatter.format(expiry);

            Permit permit = new Permit(validityPeriod, date, expiry, expectedYield, block);
            if (request.issueDate != null) {
                permit.issueDate = request.issueDate;
                permit.isIssued = Boolean.TRUE;
            }
            permit.persist();

            permits.add(permit);

            block.hasActivePermit = Boolean.TRUE;
            block.getActiveRatoon().hasPermit = Boolean.TRUE;
            block.getActiveRatoon().actualHarvestingDate = date;

            Jsonb jsonb = JsonbBuilder.create();
            AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.PERMIT.toString(), permit.id,
                    null, jsonb.toJson(permit), user);
            trail.persist();
        }

        PermitBatch batch = new PermitBatch(
                request.generationDate,
                request.issueDate,
                request.notes,
                request.startDate,
                request.endDate,
                request.startAgeInMonths,
                isAided,
                variety,
                village,
                type,
                office,
                permits,
                request.plantPercentage,
                request.ratoonOnePercentage,
                request.ratoonTwoPercentage,
                request.ratoonThreePercentage,
                request.totalExpectedTonnage
        );
        batch.persist();

    }

    public void issue(List<Long> ids, User user) {
        for (Long id : ids) {
            Permit permit = Permit.findById(id);
            if (permit == null) throw new WebApplicationException("Invalid permit selected", 404);

            Permit old = permit;

            permit.issueDate = LocalDate.now();
            permit.isIssued = Boolean.TRUE;

            Jsonb jsonb = JsonbBuilder.create();
            AuditTrail trail = new AuditTrail(_Action_Enums.ISSUED.toString(), _Section_Enums.PERMIT.toString(), permit.id,
                    jsonb.toJson(old), jsonb.toJson(permit), user);
            trail.persist();
        }
    }

    public Permit extendPermit(Long id, PermitRequest request, User user) {
        Permit permit = Permit.findById(id);
        if (permit == null) throw new WebApplicationException("Invalid permit selected", 404);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy");

        Permit old = permit;

        permit.validityPeriod = formatter.format(permit.generationDate) + " - " + formatter.format(permit.expiryDate.plusDays(request.numberOfDays));
        permit.expiryDate = permit.expiryDate.plusDays(request.numberOfDays);
        permit.status = _StatusTypes_Enum.ACTIVE.toString();
        permit.isExtended = Boolean.TRUE;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.EXTENDED.toString(), _Section_Enums.PERMIT.toString(), permit.id,
                jsonb.toJson(old), jsonb.toJson(permit), user);
        trail.persist();

        return permit;
    }

    public Permit endPermit(Long id, User user) {
        Permit permit = Permit.findById(id);
        if (permit == null) throw new WebApplicationException("Invalid permit selected", 404);

        Permit old = permit;

        permit.status = _StatusTypes_Enum.ENDED.toString();
        permit.block.hasActivePermit = Boolean.FALSE;
        permit.block.getActiveRatoon().hasPermit = Boolean.TRUE;

        permit.block.getActiveRatoon().status = _StatusTypes_Enum.ENDED.toString();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.ENDED.toString(), _Section_Enums.PERMIT.toString(), permit.id,
                jsonb.toJson(old), jsonb.toJson(permit), user);
        trail.persist();

        return permit;
    }

    public List<Permit> fetchPermits(String status, Boolean isIssued, Boolean isExtended) {
        if (status != null) {
            if (!status.equals("ENDED") && !status.equals("ACTIVE")) {
                throw new WebApplicationException("Invalid status selected", 404);
            }
        }

        return Permit.findPermits(status, isIssued, isExtended);
    }

    public List<PermitBatch> fetchPermitBatches(LocalDate startDate, LocalDate endDate) {
        if (startDate == null)
            startDate = LocalDate.now().withDayOfMonth(1);
        if (endDate == null)
            endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        return PermitBatch.list("entryTime between ?1 and ?2", startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    public Permit getDetails(Long id) {
        Permit permit = Permit.findById(id);
        if (permit == null) throw new WebApplicationException("Invalid permit selected", 404);

        return permit;
    }

    public Base64DataResponse generatePdf(Long id, User user) {
        Permit permit = Permit.findById(id);
        if (permit == null) throw new WebApplicationException("Invalid permit selected", 404);

        byte[] pdfdata = null;
        String pdfBase64 = "";
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

        // START Create PDF document
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, pdfStream);
            document.setPageSize(PageSize.A5);

            Font titleFont = GeneralPDFMethods.pageTitleFont();

            Font smallBold = GeneralPDFMethods.smallFont();

            Font paper2 = GeneralPDFMethods.tableGroupHeaderSmallFont();

            Font paperFont = GeneralPDFMethods.tableTextFont();

            Font paper = GeneralPDFMethods.tableGroupHeaderFont();

            document.open();

            GeneralPDFMethods.addBusinessInformation(user.business, document);

            Paragraph preface1 = new Paragraph();

            // Paragraph title1 = new Paragraph("G.M. SUGAR LIMITED", titleFont);
            // title1.setAlignment(Element.ALIGN_CENTER);
            // preface1.add(title1);

            Paragraph title2 = new Paragraph("P.O. BOX 1490, NAKIBIZZI JINJA", paperFont);
            title2.setAlignment(Element.ALIGN_CENTER);
            preface1.add(title2);

            GeneralPDFMethods.addEmptyLine(preface1, 1);
            document.add(preface1);

            Paragraph preface = new Paragraph();
            GeneralPDFMethods.addEmptyLine(preface, 1);

            PdfPTable tab = new PdfPTable(2);
            tab.setHorizontalAlignment(0);
            tab.setWidthPercentage(100.0f);
            tab.setWidths(new int[]{30, 70});

            PdfPCell cell1 = new PdfPCell(new Phrase("Permit No: " + permit.serialNumber, smallBold));
            cell1.setBorder(0);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

            Font font = new Font();
            font.setStyle(Font.BOLD);
            font.setSize(13);

            PdfPCell cell2 = new PdfPCell(new Phrase("Printed on: "
                    + TimeConverter.LocalDateTime_to_String(TimeConverter
                    .EpochMils_to_LocalDate(TimeConverter.EpochMillis_Now())
                    .atTime(LocalTime.now())), paperFont));

            cell2.setBorder(0);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tab.addCell(cell1);
            tab.addCell(cell2);

            document.add(tab);

            Paragraph title = new Paragraph("SUGARCANE HARVESTING PERMIT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            preface.add(title);

            // GeneralPDFMethods.addEmptyLine(preface, 1);
            document.add(preface);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{40, 60});

            PdfPCell headerCell = new PdfPCell(new Phrase("Farmer / Supplier:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(permit.getFarmerData().firstName + " " + permit.getFarmerData().lastName, paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("District:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(permit.getBlockDataSummary().village.district.name, paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Village:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(permit.getBlockDataSummary().village.name, paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Expected Tonnes:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(String.valueOf(permit.estimatedYield), paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Area (in acres):", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(String.valueOf(permit.getBlockDataSummary().area), paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Crop Cycle:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(permit.block.getActiveRatoon().cropType.name, paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Age (in months):", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(permit.block.getActiveRatoon().getAge(), paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Distance (KM):", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(String.valueOf(permit.block.distance), paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Brix Reading:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(".........................................", paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Valid from:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(TimeConverter.LocalDate_to_String(permit.generationDate), paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Valid until:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(TimeConverter.LocalDate_to_String(permit.expiryDate), paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Signature of Supplier:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase("______________________________________", paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            document.add(table);

            Paragraph nb = new Paragraph();
            GeneralPDFMethods.addEmptyLine(nb, 1);
            Paragraph q = new Paragraph("N.B. If cane is young with tops, or trash, then it is subject to reject or 60% " +
                    "additional deduction. We are not accepting red color cane.", paperFont);
            nb.add(q);

            GeneralPDFMethods.addEmptyLine(nb, 2);
            document.add(nb);

            PdfPTable table1 = new PdfPTable(2);
            table1.setWidthPercentage(100);
            table1.setWidths(new int[]{50, 50});

            PdfPCell headerCell1 = new PdfPCell(new Phrase("APPROVED BY:", paper));
            headerCell1.setBorder(0);
            headerCell1.setPadding(4);
            table1.addCell(headerCell1);

            headerCell1 = new PdfPCell();
            headerCell1.setPhrase(new Phrase("SUPERVISOR IN CHARGE:", paper));
            headerCell1.setBorder(0);
            headerCell1.setPadding(4);
            table1.addCell(headerCell1);

            headerCell1 = new PdfPCell();
            headerCell1.setPhrase(new Phrase("Sign:___________________________", paper2));
            headerCell1.setBorder(0);
            headerCell1.setPadding(4);
            table1.addCell(headerCell1);

            headerCell1 = new PdfPCell();
            headerCell1.setPhrase(new Phrase("Sign:___________________________", paper2));
            headerCell1.setBorder(0);
            headerCell1.setPadding(4);
            table1.addCell(headerCell1);

            document.add(table1);

            document.close();
            pdfdata = pdfStream.toByteArray();
            pdfBase64 = Base64.encodeBase64String(pdfdata);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

//        byte[] newdata = Base64.decodeBase64(pdfBase64);
//        try (OutputStream stream = new FileOutputStream("/home/richard/Downloads/Permit.pdf")) {
//            try {
//                stream.write(newdata);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }

        Base64DataResponse data = new Base64DataResponse();
        data.data = pdfBase64;

        return data;
    }

    public List<PermitGenerator> expectedHarvest(LocalDate startDate, LocalDate endDate, Long varietyId, Long villageId,
                                                 Long cropTypeId, Long districtOfficeId, Integer startAgeInMonths, Boolean isAided,
                                                 Double plantPercentage, Double ratoonOnePercentage, Double ratoonTwoPercentage,
                                                 Double ratoonThreePercentage, Double totalExpectedTonnage) {

        if (totalExpectedTonnage != null && (plantPercentage == null || ratoonOnePercentage == null || ratoonTwoPercentage == null || ratoonThreePercentage == null)) {
            throw new WebApplicationException("When you select a total expected tonnage, you must select the plant and ratoon percentages", 406);
        }

        if (plantPercentage != null && ratoonOnePercentage != null && ratoonTwoPercentage != null && ratoonThreePercentage != null &&
                plantPercentage + ratoonOnePercentage + ratoonTwoPercentage + ratoonThreePercentage != 100)
            throw new WebApplicationException("The plant and ratoon percentages should sum up to 100%", 406);

        List<PermitGenerator> data = new ArrayList<>();

        String districtOfficeQuery = " ";
        if (districtOfficeId != null) {
            DistrictOffice office = DistrictOffice.findById(districtOfficeId);
            if (office == null) throw new WebApplicationException("Invalid district office selected", 404);

            districtOfficeQuery = " and D.id=" + office.id;
        }

        String villageQuery = " ";
        if (villageId != null) {
            Village village = Village.findById(villageId);
            if (village == null) throw new WebApplicationException("Invalid village selected", 404);

            villageQuery = " and V.id=" + village.id;
        }

        String varietyQuery = " ";
        if (varietyId != null) {
            CaneVariety variety = CaneVariety.findById(varietyId);
            if (variety == null) throw new WebApplicationException("Invalid cane variety selected", 404);

            varietyQuery = " and CV.id = " + variety.id;
        }

        String cropTypeQuery = " ";
        if (cropTypeId != null) {
            CropType type = CropType.findById(cropTypeId);
            if (type == null) throw new WebApplicationException("Invalid crop type selected", 404);

            cropTypeQuery = " and CT.id=" + type.id;
        }

        String isAidedQuery = " ";
        if (isAided != null) {
            isAidedQuery = " and A.isAided=" + isAided;
        }

        GeneralBusinessSettings setq = GeneralBusinessSettings.single(_SettingParameter_Enums.MATURITY_PERIOD.toString());
        if (setq == null) throw new WebApplicationException("First set the maturity period in settings", 404);

        String qry = "" +
                "select " +
                "B.id as id, " +
                "concat(F.firstName,' ', F.surName,' ', F.otherName) as farmer, " +
                "V.name as village, " +
                "D.name as districtOffice, " +
                "F.airtel as airtel, " +
                "F.mtn as mtn, " +
                "B.blockNumber as blockNumber, " +
                "(CT.expectedTonnesPerAcre * B.area) as estimatedYield, " +
                "CT.name as CTName, " +
                "DATE(A.plantingDate) as plantingDate, " +
                "DATE_ADD(A.plantingDate, INTERVAL " + Long.parseLong(setq.settingValue) + " MONTH) as harvestingDate " +
                "from BlockCropType A " +
                "inner join Block B on A.block_id = B.id " +
                "inner join Farmer F on B.farmer_id = F.id " +
                "inner join Village V on B.village_id = V.id " +
                "inner join DistrictOffice D on B.districtOffice_id = D.id " +
                "inner join CropType CT on A.cropType_id = CT.id " +
                "inner join CaneVariety CV on B.caneVariety_id = CV.id " +
                "where " +
                "B.hasActivePermit = '" + Boolean.FALSE + "' and " +
                "A.hasPermit = '" + Boolean.FALSE + "' " +
                "and DATE(DATE_ADD(A.plantingDate, INTERVAL " + Long.parseLong(setq.settingValue) + " MONTH)) between DATE('" + startDate + "') and DATE('" + endDate + "')" +
                districtOfficeQuery +
                villageQuery +
                varietyQuery +
                cropTypeQuery +
                isAidedQuery;

        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet set = stmt.executeQuery(qry);
        ) {
            Double totals = totalExpectedTonnage;
            Double PP = plantPercentage;
            Double RR1 = ratoonOnePercentage;
            Double RR2 = ratoonTwoPercentage;
            Double RR3 = ratoonThreePercentage;

            if (totals == null) totals = 0.0;
            if (PP == null) PP = 0.0;
            if (RR1 == null) RR1 = 0.0;
            if (RR2 == null) RR2 = 0.0;
            if (RR3 == null) RR3 = 0.0;

            double totalPL = totals * PP;
            double totalR1 = totals * RR1;
            double totalR2 = totals * RR2;
            double totalR3 = totals * RR3;

            double PL = 0.0;
            double R1 = 0.0;
            double R2 = 0.0;
            double R3 = 0.0;

            while (set.next()) {
                LocalDate harvesting = set.getDate("plantingDate").toLocalDate().plusMonths(Long.parseLong(setq.settingValue));

                Period p = Period.between(set.getDate("plantingDate").toLocalDate(), LocalDate.now());

                String cycle = set.getString("CTName");

                PermitGenerator gen = new PermitGenerator(
                        set.getLong("id"),
                        set.getString("blockNumber"),
                        set.getString("farmer"),
                        set.getString("village"),
                        set.getString("districtOffice"),
                        set.getString("airtel") + "/" + set.getString("mtn"),
                        set.getDouble("estimatedYield"),
                        TimeConverter.LocalDate_to_String(harvesting),
                        p.getYears() + " years, " + p.getMonths() + " months, " + p.getDays() + " days",
                        cycle
                );

                if (startAgeInMonths != null) {
                    if (p.toTotalMonths() >= startAgeInMonths) {
                        if (totalExpectedTonnage != null) {
                            switch (cycle) {
                                case "Plant" -> {
                                    if (PL < totalPL) {
                                        data.add(gen);
                                        PL += set.getDouble("estimatedYield");
                                    }
                                }
                                case "Ratoon 1" -> {
                                    if (R1 < totalR1) {
                                        data.add(gen);
                                        R1 += set.getDouble("estimatedYield");
                                    }
                                }
                                case "Ratoon 2" -> {
                                    if (R2 < totalR2) {
                                        data.add(gen);
                                        R2 += set.getDouble("estimatedYield");
                                    }
                                }
                                case "Ratoon 3" -> {
                                    if (R3 < totalR3) {
                                        data.add(gen);
                                        R3 += set.getDouble("estimatedYield");
                                    }
                                }
                            }
                        } else {
                            data.add(gen);
                        }
                    }
                } else {
                    if (totalExpectedTonnage != null) {
                        switch (cycle) {
                            case "Plant" -> {
                                if (PL < totalPL) {
                                    data.add(gen);
                                    PL += set.getDouble("estimatedYield");
                                }
                            }
                            case "Ratoon 1" -> {
                                if (R1 < totalR1) {
                                    data.add(gen);
                                    R1 += set.getDouble("estimatedYield");
                                }
                            }
                            case "Ratoon 2" -> {
                                if (R2 < totalR2) {
                                    data.add(gen);
                                    R2 += set.getDouble("estimatedYield");
                                }
                            }
                            case "Ratoon 3" -> {
                                if (R3 < totalR3) {
                                    data.add(gen);
                                    R3 += set.getDouble("estimatedYield");
                                }
                            }
                        }
                    } else {
                        data.add(gen);
                    }
                }

//                if (totalExpectedTonnage != null) {
//                    if (PL < totalPL)
//                        throw new WebApplicationException("There isn't enough to cover the plant percentage", 406);
//                    if (R1 < totalR1)
//                        throw new WebApplicationException("There isn't enough to cover the R1 percentage", 406);
//                    if (R2 < totalR2)
//                        throw new WebApplicationException("There isn't enough to cover the R2 percentage", 406);
//                    if (R3 < totalR3)
//                        throw new WebApplicationException("There isn't enough to cover the R3 percentage", 406);
//                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public List<DeliveryNote> getDeliveryNotes(Long id) {
        Permit permit = Permit.findById(id);
        if (permit == null) throw new WebApplicationException("Invalid permit selected", 404);

        return DeliveryNote.findByPermit(permit);
    }

    public List<DeliveryNote> getDeliveryNotesBulk(LocalDate startDate, LocalDate endDate) {
        if (startDate == null)
            startDate = LocalDate.now();
        if (endDate == null)
            endDate = LocalDate.now();

        return DeliveryNote.list("entryTime between ?1 and ?2", startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    public Base64DataResponse generateDeliveryNotPdf(Long id, User user) {
        DeliveryNote permit = DeliveryNote.findById(id);
        if (permit == null) throw new WebApplicationException("Invalid delivery note selected", 404);

        byte[] pdfdata = null;
        String pdfBase64 = "";
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

        // START Create PDF document
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, pdfStream);
            document.setPageSize(PageSize.A5);

            Font titleFont = GeneralPDFMethods.pageTitleFont();

            Font smallBold = GeneralPDFMethods.smallFont();

            Font paper2 = GeneralPDFMethods.tableGroupHeaderSmallFont();

            Font paperFont = GeneralPDFMethods.tableTextFont();

            Font paper = GeneralPDFMethods.tableGroupHeaderFont();

            document.open();

            GeneralPDFMethods.addBusinessInformation(user.business, document);

            Paragraph preface1 = new Paragraph();

            Paragraph title1 = new Paragraph("G.M. SUGAR LIMITED", titleFont);
            title1.setAlignment(Element.ALIGN_CENTER);
            preface1.add(title1);

            Paragraph title2 = new Paragraph("P.O. BOX 1490, NAKIBIZZI JINJA", paperFont);
            title2.setAlignment(Element.ALIGN_CENTER);
            preface1.add(title2);

            GeneralPDFMethods.addEmptyLine(preface1, 1);
            document.add(preface1);

            Paragraph preface = new Paragraph();
            GeneralPDFMethods.addEmptyLine(preface, 1);

            PdfPTable tab = new PdfPTable(2);
            tab.setHorizontalAlignment(0);
            tab.setWidthPercentage(100.0f);
            tab.setWidths(new int[]{30, 70});

            PdfPCell cell1 = new PdfPCell(new Phrase("Delivery Note No: " + permit.deliveryNoteNumber, smallBold));
            cell1.setBorder(0);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

            Font font = new Font();
            font.setStyle(Font.BOLD);
            font.setSize(13);

            PdfPCell cell2 = new PdfPCell(new Phrase("Printed on: "
                    + TimeConverter.LocalDateTime_to_String(TimeConverter
                    .EpochMils_to_LocalDate(TimeConverter.EpochMillis_Now())
                    .atTime(LocalTime.now())), paperFont));

            cell2.setBorder(0);
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tab.addCell(cell1);
            tab.addCell(cell2);

            document.add(tab);

            Paragraph title = new Paragraph("SUGARCANE DELIVERY NOTE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            preface.add(title);

            GeneralPDFMethods.addEmptyLine(preface, 1);
            document.add(preface);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{40, 60});

            PdfPCell headerCell = new PdfPCell(new Phrase("Farmer / Supplier:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(permit.weighBridgeTicket.permit.getFarmerData().firstName + " " + permit.weighBridgeTicket.permit.getFarmerData().lastName, paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Delivered Quantity:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(permit.deliveredQuantity + " tonnes (" + permit.deliveredQuantity * 1000 + " kgs)", paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Remaining Quantity:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(permit.remainingQuantity + " tonnes (" + permit.remainingQuantity * 1000 + " kgs)", paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            DecimalFormat df = new DecimalFormat("#,###.##");

            headerCell = new PdfPCell(new Phrase("Farmer payment:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase("UGX "+df.format(permit.payment), paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Transporter payment:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            String tp = "-";
            if (permit.transportCost != null && (permit.transportCost.compareTo(BigDecimal.ZERO) > 0))
                tp = "UGX "+df.format(permit.transportCost);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(tp, paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Delivery time:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(TimeConverter.LocalDateTime_to_String(permit.weighBridgeTicket.loadTime), paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Delivery Vehicle No:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase(permit.weighBridgeTicket.vehicleNumber, paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table.addCell(headerCell);

            document.add(table);

            Paragraph preface11 = new Paragraph();
            GeneralPDFMethods.addEmptyLine(preface11, 2);

            document.add(preface11);

            PdfPTable table1q = new PdfPTable(2);
            table1q.setWidthPercentage(100);
            table1q.setWidths(new int[]{40, 60});

            headerCell = new PdfPCell(new Phrase("Signature of Supplier:", paper));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table1q.addCell(headerCell);

            headerCell = new PdfPCell();
            headerCell.setPhrase(new Phrase("______________________________________", paper2));
            headerCell.setBorder(0);
            headerCell.setPadding(4);
            table1q.addCell(headerCell);

            document.add(table1q);

            Paragraph preface11s = new Paragraph();
            GeneralPDFMethods.addEmptyLine(preface11s, 2);

            document.add(preface11s);

            PdfPTable table1 = new PdfPTable(2);
            table1.setWidthPercentage(100);
            table1.setWidths(new int[]{50, 50});

            PdfPCell headerCell1 = new PdfPCell(new Phrase("APPROVED BY:", paper));
            headerCell1.setBorder(0);
            headerCell1.setPadding(4);
            table1.addCell(headerCell1);

            headerCell1 = new PdfPCell();
            headerCell1.setPhrase(new Phrase("SUPERVISOR IN CHARGE:", paper));
            headerCell1.setBorder(0);
            headerCell1.setPadding(4);
            table1.addCell(headerCell1);

            headerCell1 = new PdfPCell();
            headerCell1.setPhrase(new Phrase("Sign:___________________________", paper2));
            headerCell1.setBorder(0);
            headerCell1.setPadding(4);
            table1.addCell(headerCell1);

            headerCell1 = new PdfPCell();
            headerCell1.setPhrase(new Phrase("Sign:___________________________", paper2));
            headerCell1.setBorder(0);
            headerCell1.setPadding(4);
            table1.addCell(headerCell1);

            document.add(table1);

            document.close();
            pdfdata = pdfStream.toByteArray();
            pdfBase64 = Base64.encodeBase64String(pdfdata);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        byte[] newdata = Base64.decodeBase64(pdfBase64);
        try (OutputStream stream = new FileOutputStream("/home/richard/Downloads/Note.pdf")) {
            try {
                stream.write(newdata);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Base64DataResponse data = new Base64DataResponse();
        data.data = pdfBase64;

        return data;
    }
}
