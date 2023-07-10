package com.farm_erp.outgrowers.controllers.services;

import com.farm_erp.outgrowers.domains.*;
import com.farm_erp.settings.domains.CaneVariety;
import com.farm_erp.settings.domains.CropType;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class OutgrowerReportService {

    public ExpectedHarvestData expectedHarverst(LocalDate startDate, LocalDate endDate, Long varietyId, Long cropTypeId, Integer startAgeInMonths) throws IOException {
        CaneVariety variety = null;
        if (varietyId != null) {
            variety = CaneVariety.findById(varietyId);
            if (variety == null) throw new WebApplicationException("Invalid cane variety selected", 404);
        }

        CropType type = null;
        if (cropTypeId != null) {
            type = CropType.findById(cropTypeId);
            if (type == null) throw new WebApplicationException("Invalid crop type selected", 404);
        }
        List<Block> b = Block.searchByVarietyCropType(variety, type);

        List<ExpectedHarvest> harv = new ArrayList<>();


        byte[] data = null;
        String base64 = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Expected Harvest ");

        XSSFFont headingfont = workbook.createFont();
        headingfont.setBold(true);
        headingfont.setColor(new XSSFColor(Color.RED, new DefaultIndexedColorMap()));

        Row headerrow = sheet.createRow(0);

        Cell heading = headerrow.createCell(0);

        sheet.addMergedRegion(new CellRangeAddress(0, // first row (0-based)
                0, // last row (0-based)
                0, // first column (0-based)
                4 // last column (0-based)
        ));

        XSSFRichTextString title = new XSSFRichTextString("Expected Harvest between " + startDate.toString() + " and " + endDate.toString());

        title.applyFont(headingfont);

        heading.setCellValue(title);

        Row rowheader = sheet.createRow(1);
        rowheader.createCell(0).setCellValue("Block number");
        rowheader.createCell(1).setCellValue("Village");
        rowheader.createCell(2).setCellValue("Farmer Registration Number");
        rowheader.createCell(3).setCellValue("Farmer name");
        rowheader.createCell(4).setCellValue("Crop Type Name");
        rowheader.createCell(5).setCellValue("Crop Type Code");
        rowheader.createCell(6).setCellValue("Expected Tonnage");
        rowheader.createCell(7).setCellValue("Age");

        AtomicInteger i = new AtomicInteger(2);
        CropType finalType = type;
        b.forEach(block -> {
            if (cropTypeId != null && block.getActiveRatoon().cropType == finalType) {
                if (block.getActiveRatoon() != null && (block.getActiveRatoon().getExpectedHarvestingDate().isBefore(endDate.plusDays(1)) &&
                        block.getActiveRatoon().getExpectedHarvestingDate().isAfter(startDate.minusDays(1)))) {
                    Period p = Period.between(block.getActiveRatoon().plantingDate, LocalDate.now());
                    if (startAgeInMonths == null) {
                        ExpectedHarvest har = expectedHarvest(block, p);

                        Row row = sheet.createRow(i.get());
                        row.createCell(0).setCellValue(har.blockCode);
                        row.createCell(1).setCellValue(har.village);
                        row.createCell(2).setCellValue(har.farmerCode);
                        row.createCell(3).setCellValue(har.farmerName);
                        row.createCell(4).setCellValue(har.ratoonName);
                        row.createCell(5).setCellValue(har.ratoonCode);
                        row.createCell(6).setCellValue(har.expectedTonnage);
                        row.createCell(7).setCellValue(har.age);

                        harv.add(har);
                        i.getAndIncrement();
                    } else {
                        if (p.toTotalMonths() >= startAgeInMonths) {
                            ExpectedHarvest har = expectedHarvest(block, p);

                            Row row = sheet.createRow(i.get());
                            row.createCell(0).setCellValue(har.blockCode);
                            row.createCell(1).setCellValue(har.village);
                            row.createCell(2).setCellValue(har.farmerCode);
                            row.createCell(3).setCellValue(har.farmerName);
                            row.createCell(4).setCellValue(har.ratoonName);
                            row.createCell(5).setCellValue(har.ratoonCode);
                            row.createCell(6).setCellValue(har.expectedTonnage);
                            row.createCell(7).setCellValue(har.age);

                            harv.add(har);
                            i.getAndIncrement();
                        }
                    }
                }
            } else {
                if (block.getActiveRatoon() != null && (block.getActiveRatoon().getExpectedHarvestingDate().isBefore(endDate.plusDays(1)) &&
                        block.getActiveRatoon().getExpectedHarvestingDate().isAfter(startDate.minusDays(1)))) {
                    Period p = Period.between(block.getActiveRatoon().plantingDate, LocalDate.now());
                    if (startAgeInMonths == null) {
                        ExpectedHarvest har = expectedHarvest(block, p);

                        Row row = sheet.createRow(i.get());
                        row.createCell(0).setCellValue(har.blockCode);
                        row.createCell(1).setCellValue(har.village);
                        row.createCell(2).setCellValue(har.farmerCode);
                        row.createCell(3).setCellValue(har.farmerName);
                        row.createCell(4).setCellValue(har.ratoonName);
                        row.createCell(5).setCellValue(har.ratoonCode);
                        row.createCell(6).setCellValue(har.expectedTonnage);
                        row.createCell(7).setCellValue(har.age);

                        harv.add(har);
                        i.getAndIncrement();
                    } else {
                        if (p.toTotalMonths() >= startAgeInMonths) {
                            ExpectedHarvest har = expectedHarvest(block, p);

                            Row row = sheet.createRow(i.get());
                            row.createCell(0).setCellValue(har.blockCode);
                            row.createCell(1).setCellValue(har.village);
                            row.createCell(2).setCellValue(har.farmerCode);
                            row.createCell(3).setCellValue(har.farmerName);
                            row.createCell(4).setCellValue(har.ratoonName);
                            row.createCell(5).setCellValue(har.ratoonCode);
                            row.createCell(6).setCellValue(har.expectedTonnage);
                            row.createCell(7).setCellValue(har.age);

                            harv.add(har);
                            i.getAndIncrement();
                        }
                    }
                }
            }
        });

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);

        workbook.write(bos);
        data = bos.toByteArray();
        base64 = Base64.encodeBase64String(data);

        workbook.close();

        ExpectedHarvestData datar = new ExpectedHarvestData();
        datar.data = harv;
        datar.excel = base64;

        return datar;
    }

    private ExpectedHarvest expectedHarvest(Block block, Period p) {
        ExpectedHarvest dat = new ExpectedHarvest();
        dat.blockCode = block.blockNumber;
        dat.village = block.village.name;
        dat.farmerCode = block.farmer.registrationNumber;
        dat.farmerName = block.farmer.firstName + " " + block.farmer.surName + " " + block.farmer.otherName;
        dat.ratoonName = block.getActiveRatoon().cropType.name;
        dat.ratoonCode = block.getActiveRatoon().cropType.code;
        dat.expectedTonnage = block.getActiveRatoon().cropType.expectedTonnesPerAcre * block.area;
        dat.age = p.getYears() + " years, " + p.getMonths() + " months, " + p.getDays() + " days";

        return dat;
    }

    public AidedBlockData aidedBlocks() throws IOException {

        List<Block> blocks = Block.listAll();
        List<BlockAidData> datar = new ArrayList<>();

        byte[] data = null;
        String base64 = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Aided Blocks ");

        XSSFFont headingfont = workbook.createFont();
        headingfont.setBold(true);
        headingfont.setColor(new XSSFColor(Color.RED, new DefaultIndexedColorMap()));

        Row headerrow = sheet.createRow(0);

        Cell heading = headerrow.createCell(0);

        sheet.addMergedRegion(new CellRangeAddress(0, // first row (0-based)
                0, // last row (0-based)
                0, // first column (0-based)
                4 // last column (0-based)
        ));

        XSSFRichTextString title = new XSSFRichTextString("Aided Blocks");

        title.applyFont(headingfont);

        heading.setCellValue(title);

        Row rowheader = sheet.createRow(1);
        rowheader.createCell(0).setCellValue("Block number");
        rowheader.createCell(1).setCellValue("Block number");
        rowheader.createCell(2).setCellValue("Farmer Registration Number");
        rowheader.createCell(3).setCellValue("Farmer name");
        rowheader.createCell(4).setCellValue("Total Aid");
        rowheader.createCell(5).setCellValue("Paid Aid");

        AtomicInteger i = new AtomicInteger(2);
        blocks.forEach(block -> {

            if (block.getActiveRatoon() != null && block.getIsAided()) {
                BlockAidData dat = new BlockAidData();
                dat.village = block.village.name;
                dat.blockNumber = block.blockNumber;
                dat.registrationNumber = block.farmer.registrationNumber;
                dat.farmerName = block.farmer.firstName + " " + block.farmer.surName + " " + block.farmer.otherName;
                dat.totalAid = block.getActiveRatoon().getTotalAid();
                dat.paidTotalAid = BigDecimal.ZERO;

                Row row = sheet.createRow(i.get());
                row.createCell(0).setCellValue(dat.blockNumber);
                row.createCell(1).setCellValue(dat.village);
                row.createCell(2).setCellValue(dat.registrationNumber);
                row.createCell(3).setCellValue(dat.farmerName);
                row.createCell(4).setCellValue(dat.totalAid.toString());
                row.createCell(5).setCellValue(dat.paidTotalAid.toString());

                datar.add(dat);
                i.getAndIncrement();
            }
        });

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);

        workbook.write(bos);
        data = bos.toByteArray();
        base64 = Base64.encodeBase64String(data);

        workbook.close();

        AidedBlockData dataq = new AidedBlockData();
        dataq.data = datar;
        dataq.excel = base64;

        return dataq;
    }
}

