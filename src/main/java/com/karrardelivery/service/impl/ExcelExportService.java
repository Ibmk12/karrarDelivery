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

    public static class Section<T> {
        public final String title;
        public final List<T> data;
        public final Function<T, Object[]> rowMapper;
        public final Function<List<T>, Object[]> totalMapper;

        public Section(String title, List<T> data, Function<T, Object[]> rowMapper, Function<List<T>, Object[]> totalMapper) {
            this.title = title;
            this.data = data;
            this.rowMapper = rowMapper;
            this.totalMapper = totalMapper;
        }
    }

    public <T> byte[] exportSectionsToExcel(String sheetName, String[] headers, List<Section<T>> sections) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldFont.setFontHeightInPoints((short) 11);

            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            setBorders(boldStyle);

            CellStyle normalStyle = workbook.createCellStyle();
            setBorders(normalStyle);

            int rowIdx = 0;

            for (Section<T> section : sections) {
                // Section title
                Row titleRow = sheet.createRow(rowIdx++);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue(section.title);
                titleCell.setCellStyle(boldStyle);

                // Header
                Row headerRow = sheet.createRow(rowIdx++);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(boldStyle);
                }

                // Data rows
                for (T item : section.data) {
                    Object[] values = section.rowMapper.apply(item);
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

                // Total row
                if (section.totalMapper != null) {
                    Object[] totals = section.totalMapper.apply(section.data);
                    Row totalRow = sheet.createRow(rowIdx++);
                    for (int i = 0; i < totals.length; i++) {
                        Cell cell = totalRow.createCell(i);
                        if (totals[i] instanceof Number number) {
                            cell.setCellValue(number.doubleValue());
                        } else {
                            cell.setCellValue(String.valueOf(totals[i]));
                        }
                        cell.setCellStyle(boldStyle);
                    }
                }

                rowIdx++; // Empty row between sections
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
