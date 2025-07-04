package com.karrardelivery.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class ExcelFormattingService {

    private static final int START_ROW = 2;
    private static final int START_COL = 2;

    public void centerSheetLayout(Sheet sheet) {
        int maxCol = getMaxColumnCount(sheet);
        int lastRow = sheet.getLastRowNum();

        sheet.shiftRows(0, lastRow, START_ROW);

        for (int r = START_ROW; r <= lastRow + START_ROW; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            for (int c = maxCol - 1; c >= 0; c--) {
                Cell oldCell = row.getCell(c);
                if (oldCell == null) continue;
                Cell newCell = row.createCell(c + START_COL);
                cloneCell(oldCell, newCell);
                row.removeCell(oldCell);
            }
        }
    }

    private int getMaxColumnCount(Sheet sheet) {
        int max = 0;
        for (Row row : sheet) {
            max = Math.max(max, row.getLastCellNum());
        }
        return max;
    }

    private void cloneCell(Cell oldCell, Cell newCell) {
        newCell.setCellStyle(oldCell.getCellStyle());
        switch (oldCell.getCellType()) {
            case STRING -> newCell.setCellValue(oldCell.getStringCellValue());
            case NUMERIC -> newCell.setCellValue(oldCell.getNumericCellValue());
            case BOOLEAN -> newCell.setCellValue(oldCell.getBooleanCellValue());
            case FORMULA -> newCell.setCellFormula(oldCell.getCellFormula());
            case BLANK -> newCell.setBlank();
            default -> {}
        }
    }

    public void insertLogo(Workbook workbook, Sheet sheet, String logoPath) {
        try (InputStream is = getClass().getResourceAsStream(logoPath)) {
            if (is == null) return;
            byte[] bytes = IOUtils.toByteArray(is);
            int picIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            CreationHelper helper = workbook.getCreationHelper();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(0);
            Picture picture = drawing.createPicture(anchor, picIdx);
            picture.resize(3, 3);
        } catch (IOException e) {
            log.error("Failed to insert logo from path: {}", logoPath, e);
        } catch (Exception e) {
            log.error("Unexpected error while inserting logo: {}", logoPath, e);
        }
    }
}
