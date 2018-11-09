package com.iwill.deploy.common.excel;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExcelHelperTest {

    @Test
    public void getArray() {
        String filename = "E:\\shumingl\\temp\\deploy-utils\\test.xlsx";
        String sheetName = "Sheet1";
        String data[][] = ExcelHelper.getArray(filename, sheetName);
        if (data != null) {
            for (int r = 0; r < data.length; r++) {
                for (int c = 0; c < data[r].length; c++) {
                    System.out.printf("%s\t ", data[r][c]);
                }
                System.out.println();
            }
        } else {
            throw new RuntimeException("data is null");
        }
    }
}