package com.karrardelivery.service.impl;

import com.karrardelivery.dto.OrderReportDto;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Service
@Data
public class ExcelExportService {

    public static final String SIGNATURE = "Account signature: _________________________Client Signature__________________________________";

    public static class Section<T> {
        public final String title;
        public final List<T> data;
        public final Function<T, Object[]> rowMapper;
        public final Function<List<T>, Object[]> totalMapper;
        public final Function<List<T>, Object[]> receivedByTrader;

        public Section(String title, List<T> data, Function<T, Object[]> rowMapper,
                       Function<List<T>, Object[]> totalMapper,
                       Function<List<T>, Object[]> receivedByTrader) {
            this.title = title;
            this.data = data;
            this.rowMapper = rowMapper;
            this.totalMapper = totalMapper;
            this.receivedByTrader = receivedByTrader;
        }
    }

    public <T> XSSFWorkbook createWorkbookWithSections(String sheetName, String[] headers, List<Section<T>> sections) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        CellStyle boldStyle = createCellStyle(workbook, true);
        CellStyle normalStyle = createCellStyle(workbook, false);

        int rowIdx = 0;

        rowIdx = writeSectionMetadata(sheet, sections.get(0), rowIdx, boldStyle);

        for (Section<T> section : sections) {
            rowIdx = writeHeader(sheet, headers, rowIdx, boldStyle);
            rowIdx = writeDataRows(sheet, section.data, section.rowMapper, rowIdx, normalStyle);
            rowIdx = writeTotals(sheet, section.totalMapper, section.data, rowIdx, boldStyle);
            rowIdx = writeReceivedByTrader(sheet, section.receivedByTrader, section.data, rowIdx, boldStyle);

            rowIdx++; // spacing between sections
        }

        writeSheetFooter(sheet, null, rowIdx, boldStyle);
        autoSizeAndAlignColumns(sheet, headers.length);

        return workbook;
    }

    private CellStyle createCellStyle(Workbook workbook, boolean bold) {
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setBold(bold);
        font.setFontHeightInPoints((short) 11);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        setBorders(style);
        return style;
    }

    private void setBorders(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private void autoSizeAndAlignColumns(Sheet sheet, int columnCount) {
        // Create a default cell style with center alignment
        CellStyle centerStyle = sheet.getWorkbook().createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Apply center alignment to all existing cells in the sheet
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell != null) {
                    // Create a new style that preserves existing formatting
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    if (cell.getCellStyle() != null) {
                        newStyle.cloneStyleFrom(cell.getCellStyle());
                    }
                    // Set center alignment
                    newStyle.setAlignment(HorizontalAlignment.CENTER);
                    newStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    cell.setCellStyle(newStyle);
                }
            }
        }

        // Auto-size columns
        for (int i = 1; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private int writeSectionMetadata(Sheet sheet, Section<?> section, int rowIdx, CellStyle style) {
        Row metaRow = sheet.createRow(rowIdx++);
        int dateStartCol = 2;
        int dateEndCol = 6;
        int statusStartCol = dateEndCol + 1;
        int statusEndCol = statusStartCol + 3;

        // Date cell
        Cell dateCell = metaRow.createCell(dateStartCol - 2);
        dateCell.setCellValue(((OrderReportDto)section.data.get(0)).getDeliveryDate());
        sheet.addMergedRegion(new CellRangeAddress(metaRow.getRowNum(), metaRow.getRowNum(), dateStartCol, dateEndCol));
        applyMergedStyle(metaRow, dateStartCol - 2, dateEndCol - 2, style);

        // Status cell
        Cell statusCell = metaRow.createCell(statusStartCol - 2);
        statusCell.setCellValue(getTraderNameFromSection(section));
        sheet.addMergedRegion(new CellRangeAddress(metaRow.getRowNum(), metaRow.getRowNum(), statusStartCol, statusEndCol));
        applyMergedStyle(metaRow, statusStartCol - 2, statusEndCol - 2, style);

        return rowIdx;
    }

    private void applyMergedStyle(Row row, int startCol, int endCol, CellStyle style) {
        for (int col = startCol; col <= endCol; col++) {
            Cell cell = row.getCell(col);
            if (cell == null) cell = row.createCell(col);
            cell.setCellStyle(style);
        }
    }

    private int writeHeader(Sheet sheet, String[] headers, int rowIdx, CellStyle style) {
        Row row = sheet.createRow(rowIdx++);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
        return rowIdx;
    }

    private <T> int writeDataRows(Sheet sheet, List<T> data, Function<T, Object[]> mapper, int rowIdx, CellStyle style) {
        for (T item : data) {
            Object[] values = mapper.apply(item);
            Row row = sheet.createRow(rowIdx++);
            for (int i = 0; i < values.length; i++) {
                Cell cell = row.createCell(i);
                if (values[i] instanceof Number number) {
                    cell.setCellValue(number.doubleValue());
                } else {
                    cell.setCellValue(values[i] == null? "" : String.valueOf(values[i]));
                }
                cell.setCellStyle(style);
            }
        }
        return rowIdx;
    }

    private <T> int writeTotals(Sheet sheet, Function<List<T>, Object[]> mapper, List<T> data, int rowIdx, CellStyle style) {
        if (mapper == null) return rowIdx;

        Object[] values = mapper.apply(data);
        Row row = sheet.createRow(rowIdx++);
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            if (values[i] instanceof Number number) {
                cell.setCellValue(number.doubleValue());
            } else {
                cell.setCellValue(String.valueOf(values[i]));
            }
            cell.setCellStyle(style);
        }
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 5));
        return rowIdx;
    }

    private <T> int writeReceivedByTrader(Sheet sheet, Function<List<T>, Object[]> mapper, List<T> data, int rowIdx, CellStyle style) {
        writeTotals(sheet, mapper, data, rowIdx, style);
        Row row = sheet.getRow(rowIdx);
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, 8));
        return rowIdx + 1;
    }

    private String getTraderNameFromSection(Section<?> section) {
        if (section.data != null && !section.data.isEmpty()) {
            Object first = section.data.get(0);
            if (first instanceof OrderReportDto dto && dto.getTraderName() != null) {
                return dto.getTraderName() + " - " + dto.getTraderCode();
            }
        }
        return "";
    }

    private int writeSheetFooter(Sheet sheet, Section<?> section, int rowIdx, CellStyle style) {
        rowIdx--;
        Row metaRow = sheet.createRow(rowIdx++);
        int dateStartCol = 2;
        int dateEndCol = 10;

        int startRow = metaRow.getRowNum();
        int endRow = startRow + 1; // merge with next row

        Cell dateCell = metaRow.createCell(dateStartCol - 2);
        dateCell.setCellValue(SIGNATURE);

        // Create the merged region
        CellRangeAddress mergedRegion = new CellRangeAddress(startRow, endRow, dateStartCol, dateEndCol);
        sheet.addMergedRegion(mergedRegion);

        // Create a new style with center alignment
        CellStyle centeredStyle = sheet.getWorkbook().createCellStyle();
        centeredStyle.cloneStyleFrom(style); // Copy existing style properties

        // Set center alignment (both horizontal and vertical)
        centeredStyle.setAlignment(HorizontalAlignment.CENTER);
        centeredStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        applyMergedStyle(metaRow, dateStartCol - 2, dateEndCol - 2, centeredStyle);
        Row nextRow = sheet.createRow(rowIdx++); // create the second row that's part of the merge

        // Apply borders around the merged region using RegionUtil
        CellRangeAddress styleMergedRegion = new CellRangeAddress(startRow, endRow, dateStartCol - 2, dateEndCol - 2);
        RegionUtil.setBorderTop(BorderStyle.THICK, styleMergedRegion, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THICK, styleMergedRegion, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THICK, styleMergedRegion, sheet);
        RegionUtil.setBorderRight(BorderStyle.THICK, styleMergedRegion, sheet);

        // Set border colors to black
        RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(), styleMergedRegion, sheet);
        RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(), styleMergedRegion, sheet);
        RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(), styleMergedRegion, sheet);
        RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(), styleMergedRegion, sheet);

        return rowIdx;
    }
}
