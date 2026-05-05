package com.karrardelivery.service.impl;

import com.karrardelivery.dto.OrderDto;
import com.karrardelivery.service.TraderFinancialReportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TraderFinancialReportServiceImpl implements TraderFinancialReportService {

    private static final int COL_TRADER_NAME   = 0;
    private static final int COL_TOTAL_ORDERS  = 1;
    private static final int COL_TRADER_AMOUNT = 2;
    private static final int COL_DELIVERY_FEE  = 3;
    private static final int COL_AGENT_FEE     = 4;
    private static final int COL_NET_COMPANY   = 5;
    private static final int COL_TOTAL         = 6;
    private static final int LAST_COL          = COL_TOTAL;

    private static final byte[] DARK_BLUE      = hex("1F3864");
    private static final byte[] MID_BLUE       = hex("2E75B6");
    private static final byte[] LIGHT_BLUE     = hex("BDD7EE");
    private static final byte[] GRAND_TOTAL_BG = hex("F2F2F2");

    public byte[] generateTraderFinancialExcelReport(Map<String, List<OrderDto>> grouped, Date reportDate, String[] headerKeys) throws IOException {

        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFSheet sheet = wb.createSheet("Sheet1");
            Styles styles   = new Styles(wb);

            int row = 0;
            row = writeTitleRow(sheet, styles, row, headerKeys);
            row = writeDateRow(sheet, styles, row, reportDate);
            row = writeHeaderRow(sheet, styles, row, headerKeys);

            for (Map.Entry<String, List<OrderDto>> entry : grouped.entrySet()) {
                row = writeTraderRow(sheet, styles, row, entry.getKey(), entry.getValue());
            }

            writeGrandTotalRow(sheet, styles, row, grouped);

            // column widths
            sheet.setColumnWidth(COL_TRADER_NAME,   28 * 256);
            sheet.setColumnWidth(COL_TOTAL_ORDERS,  22 * 256);
            sheet.setColumnWidth(COL_TRADER_AMOUNT, 18 * 256);
            sheet.setColumnWidth(COL_DELIVERY_FEE,  16 * 256);
            sheet.setColumnWidth(COL_AGENT_FEE,     14 * 256);
            sheet.setColumnWidth(COL_NET_COMPANY,   18 * 256);
            sheet.setColumnWidth(COL_TOTAL,         14 * 256);

            wb.write(out);
            return out.toByteArray();
        }
    }

    // ── row writers ───────────────────────────────────────────────────────────

    private int writeTitleRow(XSSFSheet sheet, Styles s, int rowIdx, String[] headerKeys) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(22);

        cell(row, COL_TRADER_NAME, "Report", s.titleLabel);
        cell(row, COL_TOTAL_ORDERS, headerKeys[headerKeys.length - 1], s.titleValue);
        for (int c = COL_DELIVERY_FEE; c <= LAST_COL; c++) row.createCell(c).setCellStyle(s.titleEmpty);

        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, COL_TOTAL_ORDERS, LAST_COL));
        return rowIdx + 1;
    }

    private int writeDateRow(XSSFSheet sheet, Styles s, int rowIdx, Date reportDate) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(18);

        cell(row, COL_TRADER_NAME, "Date", s.dateLabel);
        cell(row, COL_TOTAL_ORDERS, new SimpleDateFormat("dd-MM-yyyy").format(reportDate), s.dateValue);
        for (int c = COL_DELIVERY_FEE; c <= LAST_COL; c++) row.createCell(c).setCellStyle(s.dateEmpty);

        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, COL_TOTAL_ORDERS, LAST_COL));
        return rowIdx + 1;
    }

    private int writeHeaderRow(XSSFSheet sheet, Styles s, int rowIdx, String[] headers) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(20);
        for (int c = 0; c <= LAST_COL; c++) cell(row, c, headers[c], s.columnHeader);
        return rowIdx + 1;
    }

    private int writeTraderRow(XSSFSheet sheet, Styles s, int rowIdx,
                               String traderName, List<OrderDto> group) {
        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(16);

        double traderAmount = group.stream().mapToDouble(OrderDto::getTraderAmount).sum();
        double deliveryFee  = group.stream().mapToDouble(OrderDto::getDeliveryAmount).sum();
        double agentFee     = group.stream().mapToDouble(o -> o.getAgentAmount()      != null ? o.getAgentAmount()      : 0.0).sum();
        double netCompany   = group.stream().mapToDouble(o -> o.getNetCompanyAmount() != null ? o.getNetCompanyAmount() : 0.0).sum();
        double total        = traderAmount + deliveryFee;

        cell(row, COL_TRADER_NAME,   traderName,       s.dataText);
        cell(row, COL_TOTAL_ORDERS,  (double) group.size(), s.dataNumber);
        cell(row, COL_TRADER_AMOUNT, traderAmount,     s.dataCurrency);
        cell(row, COL_DELIVERY_FEE,  deliveryFee,      s.dataCurrency);
        cell(row, COL_AGENT_FEE,     agentFee,         s.dataCurrency);
        cell(row, COL_NET_COMPANY,   netCompany,       s.dataCurrency);
        cell(row, COL_TOTAL,         total,            s.dataCurrency);

        return rowIdx + 1;
    }

    private void writeGrandTotalRow(XSSFSheet sheet, Styles s, int rowIdx,
                                    Map<String, List<OrderDto>> grouped) {
        List<OrderDto> all = grouped.values().stream()
                .flatMap(Collection::stream).collect(Collectors.toList());

        Row row = sheet.createRow(rowIdx);
        row.setHeightInPoints(18);

        double traderAmount = all.stream().mapToDouble(OrderDto::getTraderAmount).sum();
        double deliveryFee  = all.stream().mapToDouble(OrderDto::getDeliveryAmount).sum();
        double agentFee     = all.stream().mapToDouble(o -> o.getAgentAmount()      != null ? o.getAgentAmount()      : 0.0).sum();
        double netCompany   = all.stream().mapToDouble(o -> o.getNetCompanyAmount() != null ? o.getNetCompanyAmount() : 0.0).sum();

        cell(row, COL_TRADER_NAME,   "Grand Total",           s.grandTotalLabel);
        cell(row, COL_TOTAL_ORDERS,  (double) all.size(),     s.grandTotalNumber);
        cell(row, COL_TRADER_AMOUNT, traderAmount,            s.grandTotalCurrency);
        cell(row, COL_DELIVERY_FEE,  deliveryFee,             s.grandTotalCurrency);
        cell(row, COL_AGENT_FEE,     agentFee,                s.grandTotalCurrency);
        cell(row, COL_NET_COMPANY,   netCompany,              s.grandTotalCurrency);
        cell(row, COL_TOTAL,         traderAmount + deliveryFee + agentFee + netCompany, s.grandTotalCurrency);
    }

    // ── cell helpers ──────────────────────────────────────────────────────────

    private void cell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value : "");
        c.setCellStyle(style);
    }

    private void cell(Row row, int col, double value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    // ── colour helpers ────────────────────────────────────────────────────────

    private static byte[] hex(String h) {
        return new byte[]{
                (byte) Integer.parseInt(h, 0, 2, 16),
                (byte) Integer.parseInt(h, 2, 4, 16),
                (byte) Integer.parseInt(h, 4, 6, 16)
        };
    }

    private static XSSFColor color(XSSFWorkbook wb, byte[] rgb) {
        return new XSSFColor(rgb, wb.getStylesSource().getIndexedColors());
    }

    // ── style holder ──────────────────────────────────────────────────────────

    private class Styles {

        final XSSFCellStyle titleLabel, titleValue, titleEmpty;
        final XSSFCellStyle dateLabel, dateValue, dateEmpty;
        final XSSFCellStyle columnHeader;
        final XSSFCellStyle dataText, dataNumber, dataCurrency;
        final XSSFCellStyle grandTotalLabel, grandTotalNumber, grandTotalCurrency;

        Styles(XSSFWorkbook wb) {
            DataFormat fmt = wb.createDataFormat();

            XSSFFont boldWhite = font(wb, true,  255, 255, 255, 12);
            XSSFFont boldDark  = font(wb, true,  0,   0,   0,   11);
            XSSFFont normal    = font(wb, false, 0,   0,   0,   11);

            titleLabel    = style(wb, DARK_BLUE,      boldWhite, HorizontalAlignment.LEFT,   null);
            titleValue    = style(wb, DARK_BLUE,      boldWhite, HorizontalAlignment.CENTER, null);
            titleEmpty    = style(wb, DARK_BLUE,      boldWhite, HorizontalAlignment.LEFT,   null);

            dateLabel     = style(wb, MID_BLUE,       boldWhite, HorizontalAlignment.LEFT,   null);
            dateValue     = style(wb, MID_BLUE,       boldWhite, HorizontalAlignment.LEFT,   null);
            dateEmpty     = style(wb, MID_BLUE,       boldWhite, HorizontalAlignment.LEFT,   null);

            columnHeader  = style(wb, LIGHT_BLUE,     boldDark,  HorizontalAlignment.CENTER, null);
            columnHeader.setWrapText(true);

            dataText      = style(wb, null,           normal,    HorizontalAlignment.LEFT,   null);
            dataNumber    = style(wb, null,           normal,    HorizontalAlignment.CENTER, fmt.getFormat("#,##0"));
            dataCurrency  = style(wb, null,           normal,    HorizontalAlignment.RIGHT,  fmt.getFormat("#,##0.00"));

            grandTotalLabel    = style(wb, GRAND_TOTAL_BG, boldDark, HorizontalAlignment.LEFT,   null);
            grandTotalNumber   = style(wb, GRAND_TOTAL_BG, boldDark, HorizontalAlignment.CENTER, fmt.getFormat("#,##0"));
            grandTotalCurrency = style(wb, GRAND_TOTAL_BG, boldDark, HorizontalAlignment.RIGHT,  fmt.getFormat("#,##0.00"));
        }

        private XSSFFont font(XSSFWorkbook wb, boolean bold, int r, int g, int b, int size) {
            XSSFFont f = wb.createFont();
            f.setBold(bold);
            f.setFontHeightInPoints((short) size);
            f.setColor(new XSSFColor(new byte[]{(byte)r,(byte)g,(byte)b},
                    wb.getStylesSource().getIndexedColors()));
            return f;
        }

        private XSSFCellStyle style(XSSFWorkbook wb, byte[] bg, XSSFFont font,
                                    HorizontalAlignment align, Short dataFmt) {
            XSSFCellStyle cs = wb.createCellStyle();
            if (bg != null) {
                cs.setFillForegroundColor(color(wb, bg));
                cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            cs.setFont(font);
            cs.setAlignment(align);
            cs.setVerticalAlignment(VerticalAlignment.CENTER);
            cs.setBorderTop(BorderStyle.THIN);
            cs.setBorderBottom(BorderStyle.THIN);
            cs.setBorderLeft(BorderStyle.THIN);
            cs.setBorderRight(BorderStyle.THIN);
            if (dataFmt != null) cs.setDataFormat(dataFmt);
            return cs;
        }
    }
}
