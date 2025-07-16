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
        try {
            // Try multiple ways to load the resource
            InputStream is = null;

            // Method 1: Class resource (for resources in classpath)
            is = getClass().getResourceAsStream(logoPath);

            // Method 2: ClassLoader resource (alternative approach)
            if (is == null) {
                is = getClass().getClassLoader().getResourceAsStream(logoPath);
            }

            // Method 3: Try without leading slash if path starts with /
            if (is == null && logoPath.startsWith("/")) {
                is = getClass().getResourceAsStream(logoPath.substring(1));
            }

            // Method 4: Try with leading slash if path doesn't start with /
            if (is == null && !logoPath.startsWith("/")) {
                is = getClass().getResourceAsStream("/" + logoPath);
            }

            if (is == null) {
                log.error("Logo file not found at path: {}", logoPath);
                return;
            }

            byte[] bytes = IOUtils.toByteArray(is);
            is.close();

            if (bytes.length == 0) {
                log.error("Logo file is empty at path: {}", logoPath);
                return;
            }

            // Determine picture type based on file extension
            int pictureType = Workbook.PICTURE_TYPE_PNG; // default
            String lowerPath = logoPath.toLowerCase();
            if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
                pictureType = Workbook.PICTURE_TYPE_JPEG;
            } else if (lowerPath.endsWith(".png")) {
                pictureType = Workbook.PICTURE_TYPE_PNG;
            }

            int picIdx = workbook.addPicture(bytes, pictureType);
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            CreationHelper helper = workbook.getCreationHelper();
            ClientAnchor anchor = helper.createClientAnchor();

            // Set anchor position (columns C and D)
            anchor.setCol1(0);  // Start column C (0-indexed, so 2 = column C)
            anchor.setRow1(0);  // Start row
            anchor.setCol2(2);  // End column (column D + 1, so 4 = after column D)
            anchor.setRow2(8);  // End row (spans 3 rows)

            // Set anchor type to move and resize with cells
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);

            Picture picture = drawing.createPicture(anchor, picIdx);

            // Try different resize approaches
            try {
                // Method 1: Use resize with scale factor
                picture.resize(1); // 50% of original size
            } catch (Exception e) {
                try {
                    // Method 2: Use resize with width/height factors
                    picture.resize(0.5, 0.5);
                } catch (Exception e2) {
                    log.warn("Could not resize logo, using default size");
                }
            }

            log.info("Logo inserted successfully from path: {}", logoPath);

        } catch (IOException e) {
            log.error("IO error while inserting logo from path: {}", logoPath, e);
        } catch (Exception e) {
            log.error("Unexpected error while inserting logo from path: {}", logoPath, e);
        }
    }

    public void addTableBordersAndBoldHeader(Sheet sheet) {
        Workbook workbook = sheet.getWorkbook();

        // Create border style
        CellStyle borderedStyle = workbook.createCellStyle();
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);

        // Clone original font and make it bold for header
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.cloneStyleFrom(borderedStyle);
        headerStyle.setFont(headerFont);

        int lastRowNum = sheet.getLastRowNum();

        for (int rowIndex = 0; rowIndex <= lastRowNum; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) continue;

                // Apply header style to first row, border style otherwise
                cell.setCellStyle(rowIndex == 0 ? headerStyle : borderedStyle);
            }
        }
    }

}
