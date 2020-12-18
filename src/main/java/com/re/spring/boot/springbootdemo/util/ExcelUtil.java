package com.re.spring.boot.springbootdemo.util;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ExcelUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtil.class);

    /*
     * ---------
     * excel 导入
     * ---------
     */

    /**
     * 读取Excel，兼容 Excel 2003/2007/2010
     *
     * @param fieldList 字段
     */
    public static List<Map<String, Object>> importExcel(String fileName, InputStream is, List<String> fieldList) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {

            /*
             * 判断excel
             */
            if (fileName != null && is != null && fieldList != null && fieldList.size() > 0) {
                Workbook workbook = WorkbookFactory.create(is); // 这种方式 Excel2003/2007/2010都是可以处理的

                // int sheetCount = workbook.getNumberOfSheets(); // Sheet的数量

                /*
                 * 设置当前excel中sheet的下标：0开始
                 */
                if (workbook == null) {
                    return result;
                }
                Sheet sheet = workbook.getSheetAt(0);   // 遍历第一个Sheet

                //获取总行数
                int lastRowNum = sheet.getLastRowNum();
                LOGGER.info("get data = {}", lastRowNum);

                // 为跳过第一行目录设置count
                int count = 0;
                for (Row row : sheet) {
                    try {
                        // 跳过第一行的目录
                        if (count < 1) {
                            count++;
                            continue;
                        }

                        //如果当前行没有数据，跳出循环，去掉空格
                        if (row.getCell(0).toString().replaceAll(" ", "").equals("")) {
                            continue;
                        }

                        Map<String, Object> map = new HashMap<>();
                        //获取总列数(空格的不计算)
                        int columnTotalNum = row.getPhysicalNumberOfCells();
                        //for循环的，不扫描空格的列
                        int end = row.getLastCellNum();
                        for (int i = 0; i < end; i++) {
                            Cell cell = row.getCell(i);
                            if (cell == null) {
                                continue;
                            }
                            map.put(fieldList.get(i), getValue(cell));
                        }
                        result.add(map);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }

    private static Object getValue(Cell cell) {
        Object obj = null;
        switch (cell.getCellType()) {
            case BOOLEAN:
                obj = cell.getBooleanCellValue();
                break;
            case ERROR:
                obj = cell.getErrorCellValue();
                break;
            case NUMERIC:
                obj = cell.getNumericCellValue();
                break;
            case STRING:
                obj = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return obj;
    }


    /*
     * ---------
     * excel 导出
     * ---------
     */

    /**
     * 下载Excel (样式为通用样式,sheet页是一个)
     *
     * @param resp     response返回
     * @param fileName 文件名 不需要后缀 默认是xlsx
     * @param datas    数据
     * @param titles   表头
     */
    public static void exportExcel(HttpServletResponse resp, String fileName,
                                   Collection<?> datas, List<String> titles) throws Exception {
        resp.setCharacterEncoding(UTF_8.name());
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, UTF_8.name()) + ".xlsx");
        exportExcel(resp.getOutputStream(), datas, titles);
    }

    /**
     * Excel 写入OutputStream
     *
     * @param out    输出流
     * @param datas  数据
     * @param titles 表头
     */
    public static void exportExcel(OutputStream out, Collection<?> datas, List<String> titles) {
        Workbook wb = new XSSFWorkbook();
        try {
            Sheet sheet = wb.createSheet("new sheet");
            writeDataToExcel(wb, sheet, datas, titles);
            wb.write(out);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 写Excel
     *
     * @param workbook 工作区
     * @param sheet    sheet
     * @param datas    数据
     * @param titles   表头
     */
    private static void writeDataToExcel(Workbook workbook, Sheet sheet, Collection<?> datas, List<String> titles) {
        int cellIndex = writeTitleToExcel(workbook, sheet, titles);
        writeDataToExcel(workbook, sheet, datas);
        // autoSizeColumns(sheet, cellIndex); 自动设置列宽在openJDK1.8 linux环境会出现NPE
    }

    /**
     * 写标题到Excel
     *
     * @param workbook 工作区
     * @param sheet    sheet
     * @param titles   表头
     */
    private static int writeTitleToExcel(Workbook workbook, Sheet sheet, List<String> titles) {
        int colIndex = 0;
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Row titleRow = sheet.createRow(0);
        for (String title : titles) {
            Cell cell = titleRow.createCell(colIndex);
            cell.setCellValue(title);
            cell.setCellStyle(titleStyle);
            colIndex++;
        }
        return colIndex;
    }

    /**
     * 写内容到Excel
     *
     * @param workbook 工作区
     * @param sheet    sheet
     * @param datas    数据
     */
    private static void writeDataToExcel(Workbook workbook, Sheet sheet, Collection<?> datas) {
        int rowIndex = 1;
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        for (Object o : datas) {
            Row dataRow = sheet.createRow(rowIndex);
            Class clazz = o.getClass();
            Field[] fields = clazz.getDeclaredFields();
            int cellIndex = 0;
            for (Field field : fields) {
                Cell cell = dataRow.createCell(cellIndex);
                cell.setCellStyle(dataStyle);
                String fieldName = field.getName();
                String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method method;
                Object value = null;
                try {
                    method = clazz.getMethod(methodName);
                    value = method.invoke(o);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
                if (value == null) {
                    value = "";
                } else if (value instanceof LocalDateTime) {
                    value = ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                cell.setCellValue(String.valueOf(value));
                cellIndex++;
            }
            rowIndex++;
        }
    }

    /**
     * 设置行宽度自适应 列不会设置
     *
     * @param sheet        sheet
     * @param columnNumber 结束列
     */
    private static void autoSizeColumns(Sheet sheet, int columnNumber) {
        for (int i = 0; i < columnNumber; i++) {
            int oldWidth = sheet.getColumnWidth(i);
            sheet.autoSizeColumn(i, true);
            int newWidth = sheet.getColumnWidth(i) + 100;
            sheet.setColumnWidth(i, Math.max(newWidth, oldWidth));
        }
    }
}
