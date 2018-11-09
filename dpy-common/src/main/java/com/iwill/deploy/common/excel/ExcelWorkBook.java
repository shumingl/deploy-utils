package com.iwill.deploy.common.excel;

import com.iwill.deploy.common.excel.ExcelHelper.*;

import com.iwill.deploy.common.utils.IOUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelWorkBook implements Closeable {
    private File excelFile;
    private Workbook workbook;
    private Extension extension;

    public ExcelWorkBook(String filename) throws IOException {
        File file = new File(filename);
        if (file.exists() && file.isFile())
            this.excelFile = file;
        else
            throw new FileNotFoundException("文件不存在或文件为目录：" + filename);
        FileInputStream inputStream = new FileInputStream(file);
        this.extension = Extension.getExtension(file.getName());
        if (extension.in(Extension.XLSX, Extension.XLSM, Extension.XLAM, Extension.XLTX))
            workbook = new XSSFWorkbook(inputStream);
        else if (extension.in(Extension.XLS, Extension.XLA, Extension.XLT))
            workbook = new HSSFWorkbook(inputStream);
        else
            throw new IllegalArgumentException("不支持的文件格式：" + filename);

    }

    public void close() {
        IOUtil.closeQuietly(workbook);
    }

    public ExcelWorkSheet getWorkSheet(int sheetIndex) {
        return new ExcelWorkSheet(workbook.getSheetAt(sheetIndex));
    }

    public ExcelWorkSheet getWorkSheet(String sheetName) {
        return new ExcelWorkSheet(workbook.getSheet(sheetName));
    }

    public int getSheetIndex(ExcelWorkSheet workSheet) {
        return workbook.getSheetIndex(workSheet.getSheet());
    }

    public int getSheetIndex(String sheetName) {
        return workbook.getSheetIndex(sheetName);
    }

    public int getActiveSheetIndex() {
        return workbook.getActiveSheetIndex();
    }

    public ExcelWorkSheet getActiveSheet() {
        return getWorkSheet(getActiveSheetIndex());
    }

    public void setActiveSheet(int sheetIndex) {
        workbook.setActiveSheet(sheetIndex);
    }

}
