package com.iwill.deploy.common.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;

public class ExcelWorkSheet {
    private Sheet sheet;

    private static final int MAX_ROW = 65536;
    private static final int MAX_COL = 256;

    public ExcelWorkSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public Sheet getSheet() {
        return this.sheet;
    }

    public void setValue(int row, int column, String value) {
        Row dataRow = sheet.getRow(row);
        if (dataRow == null)
            dataRow = sheet.createRow(row);
        if (dataRow == null)
            throw new RuntimeException("无法访问数据行：" + row);
        Cell dataCell = dataRow.getCell(column);
        if (dataCell == null)
            dataCell = dataRow.createCell(column);
        if (dataCell == null)
            throw new RuntimeException("无法访问数据列：" + column);
        dataCell.setCellValue(value);
    }

    public void activeCell(int row, int column) {
        sheet.setActiveCell(new CellAddress(row, column));
    }

    public String getValue(int row, int column) {

        Row dataRow = sheet.getRow(row);
        if (dataRow == null) return null;

        Cell dataCell = dataRow.getCell(column);
        if (dataCell == null) return null;

        return dataCell.getStringCellValue();
    }

    public String[][] getUsedRangeData() {
        return getUsedRangeData(MAX_ROW, MAX_COL);
    }

    public String[][] getUsedRangeData(int rowCount, int colCount) {
        int rc = Math.min(rowCount, MAX_ROW);
        int cc = Math.min(colCount, MAX_COL);
        if (rc + cc <= 0)
            return new String[0][0];

        String data[][] = new String[rc][cc];

        for (int r = 0; r < rc; r++) {
            Row dataRow = sheet.getRow(r);
            if (dataRow != null) {
                for (int c = 0; c < cc; c++) {
                    Cell dataCell = dataRow.getCell(c);
                    if (dataCell == null) {
                        System.out.println("cell[" + r + "," + c + "] is null");
                        continue;
                    }
                    data[r][c] = dataCell.getStringCellValue();
                }
            } else {
                System.out.println("row[" + r + "] is null");
            }
        }
        return data;
    }

}
