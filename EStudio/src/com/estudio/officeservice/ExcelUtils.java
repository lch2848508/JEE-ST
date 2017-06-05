package com.estudio.officeservice;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.estudio.define.webclient.portal.SQLDefine4Portal;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;
import com.estudio.utils.ThreadUtils;

public class ExcelUtils {

    private String tempPath;

    /**
     * 生成Excel
     * 
     * @param json
     * @return
     * @throws Exception
     */
    public String createExcelByJsonDefine(JSONObject json) throws Exception {
        final String fileName = "导出Excel[" + System.currentTimeMillis() + "].xls";
        ExcelService excelService = ExcelService.getInstance();
        try {

            final String sheetName = "数据";
            excelService.addSheet(sheetName);

            // 列 顺序为A B C
            JSONArray columns = json.getJSONArray("columnWidth");
            if (columns != null) {
                for (int i = 0; i < columns.size(); i++) {
                    excelService.setColWidth(new int[] { i }, columns.getInt(i));
                }
            }

            // 行
            JSONArray rows = json.getJSONArray("rowHeight");
            if (rows != null) {
                for (int i = 0; i < rows.size(); i++) {
                    excelService.setRowHeight(new int[] { i }, rows.getInt(i));
                }
            }

            // 数据
            JSONArray records = json.getJSONArray("records");
            if (records != null) {
                for (int i = 0; i < records.size(); i++) {
                    JSONObject record = records.getJSONObject(i);
                    String value = record.getString("value");
                    String cellStr = record.getString("cell");
                    String fontName = record.getString("font");
                    if (StringUtils.isEmpty(fontName))
                        fontName = "微软雅黑";
                    int fontSize = record.getInt("size");
                    if (fontSize == 0)
                        fontSize = 9;
                    boolean isBold = record.getBoolean("bold");
                    boolean isBorder = record.containsKey("border") ? record.getBoolean("border") : true;
                    int align = record.containsKey("align") ? record.getInt("align") : 0;
                    int valign = record.containsKey("valign") ? record.getInt("valign") : 1;
                    excelService.setCellValue(cellStr, value);
                    excelService.setRangeFont(cellStr, cellStr, fontName, fontSize, ExcelServiceConst.COLOR_BLACK, isBold, false);
                    excelService.setCellBBorder(cellStr, isBorder ? ExcelServiceConst.BORDER_THICK : ExcelServiceConst.BORDER_NONE);
                    excelService.setRangeAlign(cellStr, cellStr, align, valign);
                }
            }
            excelService.save(tempPath + fileName);
        } finally {
            excelService.dispose();
        }
        registerDeleteFiles(fileName);
        JSONObject resultJson = new JSONObject();
        resultJson.put("url", "../excel_temp/" + fileName);
        resultJson.put("r", true);
        return resultJson.toString();
    }

    /**
     * 生成Excel文件
     * 
     * @param sqlDefine
     * @param jsonArray
     * @return
     * @throws Exception
     */
    public String createExcelBySQLDefine(final SQLDefine4Portal sqlDefine, final JSONArray datasJSON, final String[] filterFields) throws Exception {
        final String fileName = "导出Excel[" + System.currentTimeMillis() + "].xls";
        ExcelService excelService = ExcelService.getInstance();
        try {
            excelService.addSheet("数据");
            int firstRow = 0;
            final int firstCol = 0;
            int columnIndex = 0;

            // Excel列头
            for (int j = 0; j < sqlDefine.getColumnCount(); j++)
                if (ArrayUtils.indexOf(filterFields, sqlDefine.getColumn(j).getFieldName()) != -1) {
                    excelService.setCellValue(firstRow, firstCol + columnIndex, sqlDefine.getColumn(j).getFieldLabel());
                    columnIndex++;
                }
            excelService.setRowsFont(new int[] { firstRow }, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, true, false);
            excelService.setRowHeight(new int[] { firstRow }, 25);
            excelService.freezePanes(firstRow + 1, 0, 1, 0);
            firstRow++;

            int rows[] = new int[datasJSON.size()];
            for (int j = 0; j < datasJSON.size(); j++) {
                final JSONObject dataJSON = datasJSON.getJSONObject(j);
                columnIndex = 0;
                for (int m = 0; m < sqlDefine.getColumnCount(); m++)
                    if (ArrayUtils.indexOf(filterFields, sqlDefine.getColumn(m).getFieldName()) != -1) {
                        final String cellValue = dataJSON.containsKey(sqlDefine.getColumn(m).getFieldName()) ? dataJSON.getString(sqlDefine.getColumn(m).getFieldName()) : "";
                        excelService.setCellValue(firstRow + j, firstCol + columnIndex, cellValue);
                        columnIndex++;
                    }
                rows[j] = firstRow + j;
            }
            excelService.setRowsFont(rows, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, false, false);
            excelService.setRowHeight(rows, 25);
            excelService.setBorderAll();
            excelService.autoColWitdh(null);
            excelService.save(tempPath + fileName);
            registerDeleteFiles(fileName);
        } finally {
            excelService.dispose();
        }

        return "../excel_temp/" + fileName;
    }

    /**
     * 根据数据创建Excel
     * 
     * @param define
     * @return
     * @throws Exception
     * @throws JSONException
     */
    public JSONObject createExcelByData(final JSONObject define) throws Exception {
        final JSONObject json = new JSONObject();
        final String fileName = "导出Excel[" + System.currentTimeMillis() + "].xls";

        ExcelService excelService = ExcelService.getInstance();
        try {

            final JSONArray columns = define.getJSONArray("columns");
            final JSONArray datasJSON = define.getJSONArray("datas");
            excelService.addSheet("数据");

            int firstRow = 0;
            final int firstCol = 0;

            for (int j = 0; j < columns.size(); j++) {
                excelService.setCellValue(firstRow, firstCol + j, columns.getString(j));
            }
            excelService.setRowHeight(new int[] { firstRow }, 25);
            excelService.setRowsFont(new int[] { firstRow }, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, true, false);
            excelService.freezePanes(firstRow + 1, 0, 1, 0);

            firstRow++;

            int rows[] = new int[datasJSON.size()];
            for (int j = 0; j < datasJSON.size(); j++) {
                final JSONArray dataJSON = datasJSON.getJSONArray(j);
                for (int m = 0; m < columns.size(); m++) {
                    final String cellValue = dataJSON.get(m) == null ? "" : dataJSON.getString(m);
                    excelService.setCellValue(firstRow + j, firstCol + m, cellValue);
                }
                rows[j] = firstRow + j;
            }
            excelService.setRowHeight(rows, 25);
            excelService.setRowsFont(rows, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, false, false);
            excelService.setBorderAll();
            excelService.autoColWitdh(null);
            excelService.save(tempPath + fileName);
        } finally {
            excelService.dispose();
        }
        registerDeleteFiles(fileName);
        json.put("r", true);
        json.put("path", "../excel_temp/" + fileName);
        return json;
    }

    /**
     * 生成Excel数据文件
     * 
     * @param exportDefineJSON
     * @param dataJSON
     * @return
     * @throws Exception
     * @throws JSONException
     */
    public String createExcelByData(final JSONObject exportDefineJSON, final JSONArray datasJSON) throws Exception {
        final String fileName = exportDefineJSON.getString("filename") + "[" + System.currentTimeMillis() + "].xls";
        ExcelService excelService = ExcelService.getInstance();
        try {
            final JSONArray executes = exportDefineJSON.getJSONArray("execute");

            for (int i = 0; i < executes.size(); i++) {
                final JSONObject execute = executes.getJSONObject(i);
                final String sheetName = execute.getString("sheetName");
                excelService.addSheet(sheetName);

                int firstRow = execute.getInt("firstRow");
                final int firstCol = execute.getInt("firstCol");

                final JSONArray columns = execute.getJSONArray("headers");
                for (int j = 0; j < columns.size(); j++)
                    excelService.setCellValue(firstRow, firstCol + j, columns.getString(j));
                excelService.setRowHeight(new int[] { firstRow }, 25);
                excelService.setRowsFont(new int[] { firstRow }, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, true, false);
                excelService.setRowColor(firstRow, ExcelServiceConst.COLOR_BLACK, ExcelServiceConst.COLOR_GRAY);
                excelService.freezePanes(firstRow + 1, 0, 1, 0);

                firstRow++;
                int rows[] = new int[datasJSON.size()];
                for (int j = 0; j < datasJSON.size(); j++) {
                    final JSONArray dataJSON = datasJSON.getJSONArray(j);
                    for (int m = 0; m < columns.size(); m++) {
                        final String cellValue = dataJSON.get(m) == null ? "" : dataJSON.getString(m);
                        excelService.setCellValue(firstRow + j, firstCol + m, cellValue);
                    }
                    rows[j] = j + firstRow;
                }
                excelService.setRowHeight(rows, 25);
                excelService.setRowsFont(rows, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, false, false);
                excelService.setBorderAll();
                excelService.autoColWitdh(null);
            }
            excelService.save(tempPath + fileName);
            registerDeleteFiles(fileName);
        } finally {
            excelService.dispose();
        }
        return fileName;
    }

    /**
     * 创建临时模版文件
     * 
     * @param templateJSON
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws WriteException
     */
    public String createExcelTemplate(final String templateJSON) throws Exception {
        boolean result = false;
        final JSONObject json = JSONUtils.parserJSONObject(templateJSON);
        final String fileName = json.getString("sheetName") + "[" + System.currentTimeMillis() + "]" + ".xls";
        result = createExcelTemplate(json, tempPath + fileName);
        registerDeleteFiles(fileName);
        result = true;
        return result ? fileName : "";
    }

    public String createExcelTemplate4DataGrid(String[] headers) throws Exception {
        JSONObject json = new JSONObject();
        final String fileName = "导入数据模板" + "[" + System.currentTimeMillis() + "]" + ".xls";
        ExcelService excelService = ExcelService.getInstance();
        try {
            int row = 0;
            int col = 0;
            excelService.addSheet("导入数据模板");
            for (int i = 0; i < headers.length; i++) {
                excelService.setCellValue(row, col++, headers[i]);
            }
            excelService.setRowHeight(new int[] { row }, 25);
            excelService.setRowsFont(new int[] { row }, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, true, false);
            excelService.freezePanes(1, 0, 1, 0);
            excelService.setBorderAll();
            excelService.save(tempPath + fileName);
        } finally {
            excelService.dispose();
        }
        registerDeleteFiles(tempPath + fileName);
        json.put("r", true);
        json.put("path", "../excel_temp/" + fileName);
        return json.toJSONString();
    }

    /**
     * 设置临时路径
     * 
     * @param tempPath
     */
    public void setTempPath(final String tempPath) {
        this.tempPath = tempPath;
    }

    /**
     * 
     * @param fileName
     * @param cvsArray
     * @throws Exception
     */
    public void createExcelByCVSJSONArray(String fileName, JSONArray cvsArray) throws Exception {
        if (cvsArray.isEmpty())
            return;
        ExcelService excelService = ExcelService.getInstance();
        try {
            excelService.addSheet("结果");
            JSONArray recordArray = cvsArray.getJSONArray(0);
            for (int i = 0; i < recordArray.size(); i++)
                excelService.setCellValue(0, i, recordArray.getString(i));
            excelService.setRowsFont(new int[] { 0 }, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, true, false);
            for (int i = 1; i < cvsArray.size(); i++) {
                recordArray = cvsArray.getJSONArray(i);
                for (int j = 0; j < recordArray.size(); j++)
                    excelService.setCellValue(i, j, recordArray.getString(j));
                excelService.setRowsFont(new int[] { i }, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, false, false);
            }
            excelService.setBorderAll();
            excelService.autoSize();
            excelService.save(fileName);
        } finally {
            excelService.dispose();
        }
    }

    /**
     * 生成Excel模版
     * 
     * @param templateJSON
     * @param outputStream
     * @return
     * @throws Exception
     * @throws JSONException
     */
    private boolean createExcelTemplate(final JSONObject json, String fileName) throws Exception {
        boolean result = false;
        ExcelService excelService = ExcelService.getInstance();
        try {
            int endRow = 0;
            int endCol = 0;
            excelService.addSheet(json.getString("sheetName"));
            final JSONArray headers = json.getJSONArray("header");
            for (int i = 0; i < headers.size(); i++) {
                final JSONObject headerJSON = headers.getJSONObject(i);
                int startCol = headerJSON.getInt("startCol");
                final int row = headerJSON.getInt("row");
                endRow = Math.max(row, endRow);
                final JSONArray columns = headerJSON.getJSONArray("items");
                for (int j = 0; j < columns.size(); j++)
                    excelService.setCellValue(row, startCol++, columns.getString(j));
                endCol = Math.max(endCol, columns.size());
                excelService.setRowHeight(new int[] { row }, 25);
                excelService.setRowsFont(new int[] { row }, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, true, false);
            }
            excelService.freezePanes(1, 0, 1, 0);
            excelService.setBorderAll();
            excelService.save(fileName);
            result = true;
        } finally {
            excelService.dispose();
        }

        return result;
    }

    private static final ExcelUtils INSTANCE = new ExcelUtils();

    public static ExcelUtils getInstance() {
        return INSTANCE;
    }

    /**
     * 根据Excel生成数据
     * 
     * @param templateDefineJSON
     * @param templateJSON
     * @param excelFileName
     * @param isIncludeExtAttributes
     * @return
     * @throws JSONException
     * @throws IOException
     * @throws BiffException
     * @throws ParseException
     */
    public JSONObject getExcelData(final JSONObject templateDefineJSON, JSONObject templateJSON, final String excelFileName, boolean isIncludeExtAttributes) throws Exception {
        final JSONObject json = new JSONObject();
        json.put("portal_id", templateDefineJSON.getInt("portalID"));
        ExcelService excelService = null;
        try {
            excelService = ExcelService.getInstance(excelFileName);
            final JSONArray executes = templateDefineJSON.getJSONArray("execute");
            for (int i = 0; i < executes.size(); i++) {
                final JSONObject executeJSON = executes.getJSONObject(i);
                excelService.selectSheet(executeJSON.getString("sheetName"));
                final String datasetName = executeJSON.getString("datasetName");
                final int firstRow = executeJSON.getInt("firstRow");
                final int firstCol = executeJSON.getInt("firstCol");
                final JSONArray columns = templateJSON.getJSONArray("header").getJSONObject(0).getJSONArray("items");
                JSONUtils.append(json, "items", getSheetDatas(datasetName, excelService, firstRow, firstCol, columns, isIncludeExtAttributes));
            }
        } finally {
            if (excelService != null)
                excelService.dispose();
        }
        json.put("r", true);
        return json;
    }

    public static void main(final String[] args) throws Exception {
        JSONArray result = getInstance().getExcelData("E:\\J2EE-Workspaces\\EStudio-GIS\\EStudio\\temp\\1477146290721.xls");
        System.out.println(result);

    }

    public JSONArray getExcelData(String excelFileName) throws Exception {
        JSONArray result = new JSONArray();
        ExcelService excelService = null;
        try {
            excelService = ExcelService.getInstance(excelFileName);
            excelService.selectSheet(0);
            JSONArray list = new JSONArray();
            int totalCol = 0;
            String cellStr = excelService.getCellString(0, totalCol++);
            while (!StringUtils.isEmpty(cellStr)) {
                list.add(cellStr);
                cellStr = excelService.getCellString(0, totalCol++);
            }
            result.add(list);

            int r = 1;
            cellStr = excelService.getCellString(r, 0);
            while (!StringUtils.isEmpty(cellStr)) {
                list = new JSONArray();
                list.add(cellStr);
                for (int c = 1; c < totalCol; c++) {
                    String str = excelService.getCellString(r, c);
                    list.add(str);
                }
                result.add(list);
                cellStr = excelService.getCellString(++r, 0);
            }
        } finally {
            if (excelService != null)
                excelService.dispose();
        }
        return result;
    }

    /**
     * 将一个Sheet中的数据打包成一个JSON对象
     * 
     * @param datasetName
     * @param excelService
     * @param firstRow
     * @param col
     * @param columns
     * @param isIncludeExtAttributes
     * @return
     * @throws JSONException
     * @throws ParseException
     */
    private JSONObject getSheetDatas(final String datasetName, final ExcelService excelService, final int row, final int col, final JSONArray columns, boolean isIncludeExtAttributes) throws ParseException {
        final JSONObject json = new JSONObject();
        json.put("dataset", datasetName);
        int firstRow = row;
        List<String> extColumns = null;
        if (isIncludeExtAttributes) {
            extColumns = new ArrayList<String>();
            int extIndex = col + columns.size();
            String extString = getCellContent(excelService, firstRow - 1, extIndex).toString();
            while (!StringUtils.isEmpty(extString)) {
                extColumns.add(extString);
                extString = getCellContent(excelService, firstRow - 1, ++extIndex).toString();
            }
        }

        while (!StringUtils.isEmpty(getCellContent(excelService, firstRow, col).toString())) {
            final JSONArray rowDatas = new JSONArray();
            for (int i = 0; i < columns.size(); i++)
                rowDatas.add(getCellContent(excelService, firstRow, col + i));
            if (isIncludeExtAttributes) {
                JSONArray extArray = new JSONArray();
                for (int j = 0; j < extColumns.size(); j++) {
                    JSONObject extJson = new JSONObject();
                    extJson.put("name", extColumns.get(j));
                    extJson.put("value", getCellContent(excelService, firstRow, col + columns.size() + j).toString());
                    extArray.add(extJson);
                }
                rowDatas.add(extArray.toJSONString());
            }
            json.put("columns", columns);
            JSONUtils.append(json, "datas", rowDatas);
            // json.append("datas", rowDatas);
            firstRow++;
        }
        return json;
    }

    /**
     * 得到单元格的值
     * 
     * @param excelService
     * @param rowIndex
     * @param colIndex
     * @return
     * @throws ParseException
     */
    private String getCellContent(final ExcelService excelService, final int rowIndex, final int colIndex) throws ParseException {
        int dataType = excelService.getCellDataType(rowIndex, colIndex);
        if (dataType == ExcelServiceConst.TYPE_NULL)
            return "";
        else if (dataType == ExcelServiceConst.TYPE_STRING)
            return excelService.getCellString(rowIndex, colIndex);
        else if (dataType == ExcelServiceConst.TYPE_NUMBER) {
            String str = Double.toString(excelService.getCellDouble(rowIndex, colIndex));
            if (StringUtils.endsWith(str, ".0"))
                str = StringUtils.substring(str, 0, str.length() - 2);
            return str;
        } else if (dataType == ExcelServiceConst.TYPE_DATE) {
            String str = Convert.datetime2Str(excelService.getCellDate(rowIndex, colIndex));
            if (StringUtils.endsWith(str, "00:00:00"))
                str = StringUtils.substring(str, 0, str.length() - 9);
            return str;
        } else
            return excelService.getCellString(rowIndex, colIndex);
    }

    private void registerDeleteFiles(final String fileName) {
        files.add(new File2Date(fileName));
    }

    private final ArrayList<File2Date> files = new ArrayList<ExcelUtils.File2Date>();

    /**
     * 后台监护线程
     */
    public void startDaemonThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDirectory(new File(tempPath));
                } catch (final IOException e1) {
                }
                try {
                    FileUtils.forceMkdir(new File(tempPath));
                } catch (final IOException e1) {
                }
                while (true) {
                    final Date cd = new Date();
                    int count = 0;
                    for (int i = 0; i < files.size(); i++) {
                        final File2Date file2Date = files.get(i);
                        if (DateUtils.addMinutes(file2Date.date, 10).after(cd))
                            try {
                                FileUtils.forceDelete(new File(tempPath + "/" + file2Date.filename));
                            } catch (final IOException e) {

                            }
                        else {
                            count = i;
                            break;
                        }
                        while (count != 0) {
                            files.remove(count);
                            count--;
                        }
                    }
                    ThreadUtils.sleepMinute(5);
                }
            }
        }).start();
    }

    private class File2Date {
        String filename;
        Date date;

        public File2Date(final String filename) {
            super();
            this.filename = filename;
            date = new Date();
        }
    }

}
