package com.iwill.deploy.common.excel;

import com.iwill.deploy.common.utils.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ExcelHelper {

    private static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class);

    public static String[][] getArray(String filename, String sheetName) {
        try (ExcelWorkBook workBook = new ExcelWorkBook(filename)) {
            ExcelWorkSheet sheet = workBook.getWorkSheet(sheetName);
            return sheet.getUsedRangeData(20, 12);
        } catch (IOException e) {
            logger.error("读取Excel数据异常", e);
        }
        return null;
    }

    public enum Extension {

        XLS(".xls", VersionFlag.OLD),
        XLT(".xlt", VersionFlag.OLD),
        XLA(".xla", VersionFlag.OLD),
        XLSX(".xlsx", VersionFlag.NEW),
        XLTX(".xltx", VersionFlag.NEW),
        XLAM(".xlam", VersionFlag.NEW),
        XLSM(".xlsm", VersionFlag.NEW),

        UNKNOWN(".unknown", VersionFlag.UNKNOWN);

        private String suffix;
        private VersionFlag versionFlag;

        Extension(String suffix, VersionFlag versionFlag) {
            this.suffix = suffix;
            this.versionFlag = versionFlag;
        }

        public boolean in(Extension... extensions) {
            if (extensions != null && extensions.length > 0) {
                for (Extension extension : extensions) {
                    if (extension == this)
                        return true;
                }
            }
            return false;
        }

        public boolean matched(String filename) {
            return !StringUtil.isNOE(filename) && filename.toLowerCase().endsWith(suffix);
        }

        public static Extension getExtension(String filename) {
            for (Extension extension : Extension.values()) {
                if (extension.matched(filename))
                    return extension;
            }
            return UNKNOWN;
        }

        public VersionFlag getVersionFlag(String filename) {
            return versionFlag;
        }

    }

    public enum VersionFlag {
        OLD, NEW, UNKNOWN
    }
}
