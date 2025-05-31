package com.karrardelivery.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Service
public class ExcelExportService {

    public <T> byte[] exportToExcel(String sheetName, String[] headers, List<T> data,
                                    Function<T, Object[]> rowMapper) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // Fonts & styles
            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldFont.setFontHeightInPoints((short) 11);

            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            setBorders(boldStyle);

            CellStyle normalStyle = workbook.createCellStyle();
            setBorders(normalStyle);

            int rowIdx = 0;

            // Header
            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(boldStyle);
            }

            // Data rows
            int counter = 1;
            for (T item : data) {
                Object[] values = rowMapper.apply(item);
                Row row = sheet.createRow(rowIdx++);

                for (int i = 0; i < values.length; i++) {
                    Cell cell = row.createCell(i);
                    if (values[i] instanceof Number number) {
                        cell.setCellValue(number.doubleValue());
                    } else {
                        cell.setCellValue(String.valueOf(values[i]));
                    }
                    cell.setCellStyle(normalStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file", e);
        }
    }

    private void setBorders(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}
