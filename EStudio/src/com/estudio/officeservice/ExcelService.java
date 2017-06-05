package com.estudio.officeservice;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import sun.org.mozilla.javascript.internal.NativeArray;

import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.DateTime;
import com.aspose.cells.Font;
import com.aspose.cells.ProtectionType;
import com.aspose.cells.Range;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.estudio.utils.Convert;

public class ExcelService {

    private Workbook workbook = null;
    private Worksheet worksheet = null;
    private Pattern tagPattern = Pattern.compile("\\{[^)]*?\\}");

    /**
     * 
     * @param sheetName
     */
    public ExcelService addSheet(String sheetName) {
        worksheet = workbook.getWorksheets().add(sheetName);
        return this;
    }

    /**
     * 选择Sheet
     * 
     * @param sheetName
     */
    public ExcelService selectSheet(String sheetName) {
        worksheet = workbook.getWorksheets().get(sheetName);
        return this;
    }

    /**
     * 选择Sheet
     * 
     * @param sheetName
     */
    public ExcelService selectSheet(int index) {
        worksheet = workbook.getWorksheets().get(index);
        return this;
    }

    /**
     * 设置单元格值
     * 
     * @param cellName
     * @param value
     * @return
     */
    public ExcelService setCellValue(String cellName, Object value) {
        worksheet.getCells().get(cellName).setValue(value);
        return this;
    }

    /**
     * 设置单元格值
     * 
     * @param row
     * @param col
     * @param value
     * @return
     */
    public ExcelService setCellValue(int row, int col, Object value) {
        worksheet.getCells().get(row, col).setValue(value);
        return this;
    }

    /**
     * 设置单元格值
     * 
     * @param cellName
     * @param map
     * @param fieldName
     * @return
     */
    public ExcelService setCellValue(String cellName, Map<String, Object> map, String fieldName) {
        worksheet.getCells().get(cellName).setValue(map.get(fieldName));
        return this;
    }

    public ExcelService setCellAutoWrap(String cellName, boolean isAutoWrap) {
        Style style = worksheet.getCells().get(cellName).getStyle();
        style.setTextWrapped(isAutoWrap);
        worksheet.getCells().get(cellName).setStyle(style);
        return this;
    }

    /**
     * 导入数据集
     * 
     * @param startCell
     * @param rs
     * @param isFieldNameShown
     * @return
     * @throws Exception
     */
    public int importResultSet(String startCell, ResultSet rs, boolean isFieldNameShown) throws Exception {
        int result = worksheet.getCells().importResultSet(rs, startCell, isFieldNameShown);
        return result;
    }

    /**
     * 导入二维数据
     * 
     * @param startCell
     * @param values
     */
    public void importStringArray(String startCell, String[][] values) {
        int[] firstCR = getCellColAndRow(startCell);
        worksheet.getCells().importArray(values, firstCR[1], firstCR[0]);
    }

    /**
     * 
     * @param obj
     */
    public void importStringArray(String startCell, Object obj) {
        if (obj instanceof NativeArray) {
            NativeArray array = (NativeArray) obj;
            String[][] strs = new String[(int) (array.getLength())][];
            for (int i = 0; i < array.getLength(); i++) {

                Object subObject = array.get(i);
                NativeArray subArray = (NativeArray) subObject;
                String[] subStrs = new String[(int) subArray.getLength()];
                for (int j = 0; j < subArray.getLength(); j++)
                    subStrs[j] = subArray.get(j).toString();
                strs[i] = subStrs;
            }
            importStringArray(startCell, strs);
        }
    }

    /**
     * 获取单元格值
     * 
     * @param cellName
     * @return
     */
    public Object getCellValue(String cellName) {
        return worksheet.getCells().get(cellName).getValue();
    }

    /**
     * 获取数据类型
     * 
     * @param rowIndex
     * @param colIndex
     * @return
     */
    public int getCellDataType(int rowIndex, int colIndex) {
        return worksheet.getCells().get(rowIndex, colIndex).getType();
    }

    public Object getCellValue(int row, int col) {
        return worksheet.getCells().get(row, col).getValue();
    }

    public String getCellString(String cellName) {
        return worksheet.getCells().get(cellName).getStringValue();
    }

    public String getCellString(int row, int col) {
        return worksheet.getCells().get(row, col).getStringValue();
    }

    public int getCellInt(String cellName) {
        return worksheet.getCells().get(cellName).getIntValue();
    }

    public int getCellInt(int row, int col) {
        return worksheet.getCells().get(row, col).getIntValue();
    }

    public double getCellDouble(String cellName) {
        return worksheet.getCells().get(cellName).getDoubleValue();
    }

    public double getCellDouble(int row, int col) {
        return worksheet.getCells().get(row, col).getDoubleValue();
    }

    public Date getCellDate(String cellName) {
        DateTime d = worksheet.getCells().get(cellName).getDateTimeValue();
        return d != null ? d.toDate() : null;
    }

    public Date getCellDate(int row, int col) {
        DateTime d = worksheet.getCells().get(row, col).getDateTimeValue();
        return d != null ? d.toDate() : null;
    }

    /**
     * 合并单元格
     * 
     * @param startCell
     * @param endCell
     * @return
     */
    public ExcelService mergeCell(String startCell, String endCell) {
        int[] firstCR = getCellColAndRow(startCell);
        int[] endCR = getCellColAndRow(endCell);
        worksheet.getCells().merge(Math.min(firstCR[1], endCR[1]), Math.min(firstCR[0], endCR[0]), Math.abs(endCR[1] - firstCR[1]) + 1, Math.abs(endCR[0] - firstCR[0]) + 1);
        return this;
    }

    /**
     * 设置行高 单位像素
     * 
     * @param rows
     * @param height
     * @return
     */
    public ExcelService setRowHeight(int[] rows, int height) {
        for (int row : rows)
            worksheet.getCells().setRowHeightPixel(row, height);
        return this;
    }

    /**
     * 设置列宽
     * 
     * @param cols
     * @param width
     * @return
     */
    public ExcelService setColWidth(int[] cols, int width) {
        for (int col : cols)
            worksheet.getCells().setColumnWidthPixel(col, width);
        return this;
    }

    /**
     * 设置列宽
     * 
     * @param cols
     * @param width
     * @return
     */
    public ExcelService setColWidth(String[] cols, int width) {
        for (String str : cols)
            worksheet.getCells().setColumnWidthPixel(getColIndexByName(str), width);
        return this;
    }

    private static int getColIndexByName(String cellStr) {
        char[] cellStrArray = cellStr.toUpperCase().toCharArray();
        int len = cellStrArray.length;
        int n = 0;
        for (int i = 0; i < len; i++) {
            n += (((int) cellStrArray[i]) - 65 + 1) * Math.pow(26, len - i - 1);
        }
        return n - 1;
    }

    /**
     * 冻结单元格
     * 
     * @param startCell
     * @param endCell
     * @return
     */
    public ExcelService freezePanes(String startCell, String endCell) {
        int[] firstCR = getCellColAndRow(startCell);
        int[] endCR = getCellColAndRow(endCell);
        worksheet.freezePanes(Math.min(firstCR[1], endCR[1]), Math.min(firstCR[0], endCR[0]), Math.abs(endCR[1] - firstCR[1]) + 1, Math.abs(endCR[0] - firstCR[0]) + 1);
        return this;
    }

    /**
     * 
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol
     * @return
     */
    public ExcelService freezePanes(int startRow, int startCol, int rowCount, int colCount) {
        worksheet.freezePanes(startRow, startCol, rowCount, colCount);
        return this;
    }

    /**
     * 拆分成2个窗口
     * 
     * @param cellName
     * @return
     */
    public ExcelService splitWindow(String cellName) {
        worksheet.setActiveCell(cellName);
        worksheet.split();
        return this;
    }

    /**
     * 自动设置宽度
     * 
     * @param cols
     * @return
     * @throws Exception
     */
    public ExcelService autoColWitdh(int[] cols) throws Exception {
        if (cols == null)
            worksheet.autoFitColumns();
        else
            for (int col : cols) {
                worksheet.autoFitColumn(col);
            }
        return this;
    }

    /**
     * 自动设置行高
     * 
     * @param cols
     * @return
     * @throws Exception
     */
    public ExcelService autoRowHeight(int[] rows) throws Exception {
        if (rows == null)
            worksheet.autoFitRows();
        else
            for (int row : rows) {
                worksheet.autoFitRow(row);
            }
        return this;
    }

    /**
     * 自动计算尺寸
     * 
     * @return
     * @throws Exception
     */
    public ExcelService autoSize() throws Exception {
        worksheet.autoFitRows();
        worksheet.autoFitColumns();
        return this;
    }

    /**
     * 
     * @param startCell
     * @param endCell
     * @param align
     * @param valign
     * @return
     */
    public ExcelService setRangeAlign(String startCell, String endCell, int align, int valign) {
        int[] firstCR = getCellColAndRow(startCell);
        int[] endCR = getCellColAndRow(endCell);
        int minRow = Math.min(firstCR[1], endCR[1]);
        int maxRow = Math.max(firstCR[1], endCR[1]);
        int minCol = Math.min(firstCR[0], endCR[0]);
        int maxCol = Math.max(firstCR[0], endCR[0]);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellAlign(worksheet.getCells().get(row, col), align, valign);
        return this;
    }

    /**
     * 
     * @param startCell
     * @param endCell
     * @param align
     * @param valign
     * @return
     */
    public ExcelService setRangeAutoWrap(String startCell, String endCell, boolean isAutoWrap) {
        int[] firstCR = getCellColAndRow(startCell);
        int[] endCR = getCellColAndRow(endCell);
        int minRow = Math.min(firstCR[1], endCR[1]);
        int maxRow = Math.max(firstCR[1], endCR[1]);
        int minCol = Math.min(firstCR[0], endCR[0]);
        int maxCol = Math.max(firstCR[0], endCR[0]);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellAutoWrap(worksheet.getCells().get(row, col), isAutoWrap);
        return this;
    }

    /**
     * 设置是否自动换行
     * 
     * @param cell
     * @param isAutoWrap
     */
    private void setCellAutoWrap(Cell cell, boolean isAutoWrap) {
        Style style = cell.getStyle();
        style.setTextWrapped(isAutoWrap);
        cell.setStyle(style);
    }

    /**
     * 设置范围单元格边框
     * 
     * @param startCell
     * @param endCell
     * @param borderType
     * @return
     */
    public ExcelService setRangeBorder(String startCell, String endCell, int borderType) {
        int[] firstCR = getCellColAndRow(startCell);
        int[] endCR = getCellColAndRow(endCell);
        int minRow = Math.min(firstCR[1], endCR[1]);
        int maxRow = Math.max(firstCR[1], endCR[1]);
        int minCol = Math.min(firstCR[0], endCR[0]);
        int maxCol = Math.max(firstCR[0], endCR[0]);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置边框
     * 
     * @return
     */
    public ExcelService setBorderAll() {
        int minRow = 0;
        int maxRow = worksheet.getCells().getMaxDisplayRange().getRowCount();
        int minCol = 0;
        int maxCol = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int row = minRow; row < maxRow; row++)
            for (int col = minCol; col < maxCol; col++)
                setCellBorder(worksheet.getCells().get(row, col), 1);
        return this;
    }

    /**
     * 设置左边框
     * 
     * @param startCell
     * @param endCell
     * @param borderType
     * @return
     */
    public ExcelService setRangeLBorder(String startCell, String endCell, int borderType) {
        int[] firstCR = getCellColAndRow(startCell);
        int[] endCR = getCellColAndRow(endCell);
        int minRow = Math.min(firstCR[1], endCR[1]);
        int maxRow = Math.max(firstCR[1], endCR[1]);
        int minCol = Math.min(firstCR[0], endCR[0]);
        int maxCol = Math.max(firstCR[0], endCR[0]);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellLBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置上
     * 
     * @param startCell
     * @param endCell
     * @param borderType
     * @return
     */
    public ExcelService setRangeTBorder(String startCell, String endCell, int borderType) {
        int[] firstCR = getCellColAndRow(startCell);
        int[] endCR = getCellColAndRow(endCell);
        int minRow = Math.min(firstCR[1], endCR[1]);
        int maxRow = Math.max(firstCR[1], endCR[1]);
        int minCol = Math.min(firstCR[0], endCR[0]);
        int maxCol = Math.max(firstCR[0], endCR[0]);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellTBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置右边框
     * 
     * @param startCell
     * @param endCell
     * @param borderType
     * @return
     */
    public ExcelService setRangeRBorder(String startCell, String endCell, int borderType) {
        int[] firstCR = getCellColAndRow(startCell);
        int[] endCR = getCellColAndRow(endCell);
        int minRow = Math.min(firstCR[1], endCR[1]);
        int maxRow = Math.max(firstCR[1], endCR[1]);
        int minCol = Math.min(firstCR[0], endCR[0]);
        int maxCol = Math.max(firstCR[0], endCR[0]);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellRBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置下边框
     * 
     * @param startCell
     * @param endCell
     * @param borderType
     * @return
     */
    public ExcelService setRangeBBorder(String startCell, String endCell, int borderType) {
        int[] firstCR = getCellColAndRow(startCell);
        int[] endCR = getCellColAndRow(endCell);
        int minRow = Math.min(firstCR[1], endCR[1]);
        int maxRow = Math.max(firstCR[1], endCR[1]);
        int minCol = Math.min(firstCR[0], endCR[0]);
        int maxCol = Math.max(firstCR[0], endCR[0]);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellBBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置范围单元格
     * 
     * @param startCell
     * @param endCell
     * @param fontName
     * @param fontSize
     * @param isBold
     * @param isItalic
     * @param color
     * @return
     */
    public ExcelService setRangeFont(String startCell, String endCell, String fontName, int fontSize, int fontColor, boolean isBold, boolean isItalic) {
        int[] firstCR = getCellColAndRow(startCell);
        int[] endCR = getCellColAndRow(endCell);
        int minRow = Math.min(firstCR[1], endCR[1]);
        int maxRow = Math.max(firstCR[1], endCR[1]);
        int minCol = Math.min(firstCR[0], endCR[0]);
        int maxCol = Math.max(firstCR[0], endCR[0]);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellFont(worksheet.getCells().get(row, col), fontName, fontSize, fontColor, isBold, isItalic);
        return this;
    }

    /**
     * 设置单元格字体
     * 
     * @param cellName
     * @param fontName
     * @param fontSize
     * @param fontColor
     * @param isBold
     * @param isItalic
     * @return
     */
    public ExcelService setRangeFont(String cellName, String fontName, int fontSize, int fontColor, boolean isBold, boolean isItalic) {
        return setRangeFont(cellName, cellName, fontName, fontSize, fontColor, isBold, isItalic);
    }

    /**
     * 设置单元格字体
     * 
     * @param cellName
     * @param fontName
     * @param fontSize
     * @param isBold
     * @param isItalic
     * @return
     */
    public ExcelService setRangeFont(String cellName, String fontName, int fontSize, boolean isBold, boolean isItalic) {
        return setRangeFont(cellName, cellName, fontName, fontSize, ExcelServiceConst.COLOR_BLACK, isBold, isItalic);
    }

    /**
     * 设置行字体
     * 
     * @param rows
     * @param fontName
     * @param fontSize
     * @param isBold
     * @param isItalic
     * @return
     */
    public ExcelService setRowsFont(int[] rows, String fontName, int fontSize, int fontColor, boolean isBold, boolean isItalic) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int row : rows)
            for (int col = 0; col < colCount; col++)
                setCellFont(worksheet.getCells().get(row, col), fontName, fontSize, fontColor, isBold, isItalic);
        return this;
    }

    /**
     * 设置行字体
     * 
     * @param rows
     * @param fontName
     * @param fontSize
     * @param isBold
     * @param isItalic
     * @return
     */
    public ExcelService setColsFont(int[] cols, String fontName, int fontSize, int fontColor, boolean isBold, boolean isItalic) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int col : cols)
            for (int row = 0; row < rowCount; row++)
                setCellFont(worksheet.getCells().get(row, col), fontName, fontSize, fontColor, isBold, isItalic);

        return this;
    }

    /**
     * 设置列宽
     * 
     * @param cols
     * @param fontName
     * @param fontSize
     * @param isBold
     * @param isItalic
     * @return
     */
    public ExcelService setColsFont(String[] cols, String fontName, int fontSize, int fontColor, boolean isBold, boolean isItalic) {
        return setColsFont(colName2Indexs(cols), fontName, fontSize, fontColor, isBold, isItalic);
    }

    /**
     * 设置列Border
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsBorder(int[] cols, int borderType) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int col : cols)
            for (int row = 0; row < rowCount; row++)
                setCellBorder(worksheet.getCells().get(row, col), borderType);

        return this;
    }

    /**
     * 设置列边框
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsBorder(String[] cols, int borderType) {
        return setColsBorder(colName2Indexs(cols), borderType);
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsLBorder(int[] cols, int borderType) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int col : cols)
            for (int row = 0; row < rowCount; row++)
                setCellLBorder(worksheet.getCells().get(row, col), borderType);

        return this;
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsLBorder(String[] cols, int borderType) {
        return setColsLBorder(colName2Indexs(cols), borderType);
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsTBorder(int[] cols, int borderType) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int col : cols)
            for (int row = 0; row < rowCount; row++)
                setCellTBorder(worksheet.getCells().get(row, col), borderType);

        return this;
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsTBorder(String[] cols, int borderType) {
        return setColsTBorder(colName2Indexs(cols), borderType);
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsRBorder(int[] cols, int borderType) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int col : cols)
            for (int row = 0; row < rowCount; row++)
                setCellRBorder(worksheet.getCells().get(row, col), borderType);

        return this;
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsRBorder(String[] cols, int borderType) {
        return setColsRBorder(colName2Indexs(cols), borderType);
    }

    public int[] colName2Indexs(String[] cols) {
        int[] result = new int[cols.length];
        for (int i = 0; i < cols.length; i++)
            result[i] = columnName2Index(cols[i]);
        return result;
    }

    public ExcelService setCellBorder(String cellName, int borderType) {
        setCellBorder(worksheet.getCells().get(cellName), borderType);
        return this;
    }

    public ExcelService setLCellBorder(String cellName, int borderType) {
        setCellLBorder(worksheet.getCells().get(cellName), borderType);
        return this;
    }

    public ExcelService setCellTBorder(String cellName, int borderType) {
        setCellTBorder(worksheet.getCells().get(cellName), borderType);
        return this;
    }

    public ExcelService setCellRBorder(String cellName, int borderType) {
        setCellRBorder(worksheet.getCells().get(cellName), borderType);
        return this;
    }

    public ExcelService setCellBBorder(String cellName, int borderType) {
        setCellBBorder(worksheet.getCells().get(cellName), borderType);
        return this;
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsBBorder(int[] cols, int borderType) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int col : cols)
            for (int row = 0; row < rowCount; row++)
                setCellBBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsBBorder(String[] cols, int borderType) {
        return setColsBBorder(colName2Indexs(cols), borderType);
    }

    /**
     * 设置列对齐方式
     * 
     * @param rows
     * @param fontName
     * @param fontSize
     * @param isBold
     * @param isItalic
     * @return
     */
    public ExcelService setColsAlign(int[] cols, int align, int valign) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int col : cols)
            for (int row = 0; row < rowCount; row++)
                setCellAlign(worksheet.getCells().get(row, col), align, valign);
        return this;
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsAlign(String[] cols, int align, int valign) {
        return setColsAlign(colName2Indexs(cols), align, valign);
    }

    /**
     * 设置单元格边框
     * 
     * @param cell
     * @param borderType
     */
    private void setCellBorder(Cell cell, int borderType) {
        Style style = cell.getStyle();
        style.getBorders().getByBorderType(BorderType.LEFT_BORDER).setLineStyle(borderType);
        style.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(borderType);
        style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(borderType);
        style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(borderType);
        cell.setStyle(style);
    }

    private Color getExcelColor(int fontColor) {
        return Color.fromArgb(fontColor);
    }

    /**
     * 
     * @param cell
     * @param borderType
     */
    private void setCellLBorder(Cell cell, int borderType) {
        Style style = cell.getStyle();
        style.getBorders().getByBorderType(BorderType.LEFT_BORDER).setLineStyle(borderType);
        cell.setStyle(style);
    }

    /**
     * 
     * @param cell
     * @param borderType
     */
    private void setCellTBorder(Cell cell, int borderType) {
        Style style = cell.getStyle();
        style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(borderType);
        cell.setStyle(style);
    }

    /**
     * 
     * @param cell
     * @param borderType
     */
    private void setCellRBorder(Cell cell, int borderType) {
        Style style = cell.getStyle();
        style.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(borderType);
        cell.setStyle(style);
    }

    /**
     * 
     * @param cell
     * @param borderType
     */
    private void setCellBBorder(Cell cell, int borderType) {
        Style style = cell.getStyle();
        style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(borderType);
        cell.setStyle(style);
    }

    /**
     * 设置单元格对齐方式
     * 
     * @param cell
     * @param align
     * @param valign
     */
    private void setCellAlign(Cell cell, int align, int valign) {
        Style style = cell.getStyle();
        if (align != -1)
            style.setHorizontalAlignment(align == 0 ? TextAlignmentType.LEFT : align == 1 ? TextAlignmentType.CENTER : TextAlignmentType.RIGHT);
        if (valign != -1)
            style.setVerticalAlignment(valign == 0 ? TextAlignmentType.TOP : valign == 1 ? TextAlignmentType.CENTER : TextAlignmentType.BOTTOM);
        cell.setStyle(style);
    }

    /**
     * 设置单元格字体
     * 
     * @param cell
     * @param fontName
     * @param fontSize
     * @param fontColor
     * @param isBold
     * @param isItalic
     */
    private void setCellFont(Cell cell, String fontName, int fontSize, int fontColor, boolean isBold, boolean isItalic) {
        Style style = cell.getStyle();
        Font font = style.getFont();
        if (!StringUtils.isEmpty(fontName))
            font.setName(fontName);
        if (fontSize > 0)
            font.setSize(fontSize);
        font.setBold(isBold);
        font.setItalic(isItalic);
        font.setColor(getExcelColor(fontColor));
        cell.setStyle(style);
    }

    /**
     * 字母转化为绝对索引
     * 
     * @param str
     * @return
     */
    private int columnName2Index(String str) {
        int index = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(str.length() - i - 1);
            index += (c - 'A' + 1) * Math.pow(26, i);
        }
        index = index - 1;
        return index;
    }

    /**
     * 根据单元格名称获取索引
     * 
     * @param cellStr
     * @return
     */
    private int[] getCellColAndRow(String cellStr) {
        cellStr = StringUtils.upperCase(cellStr);
        int[] result = new int[2];
        String cs = "";
        for (int i = 0; i < cellStr.length(); i++) {
            char c = cellStr.charAt(i);
            if (c >= 'A' && c <= 'Z')
                cs = cs + c;
        }
        result[0] = columnName2Index(cs);
        result[1] = Convert.str2Int(StringUtils.replace(cellStr, cs, "")) - 1;
        return result;
    }

    /**
     * 通过标签模板填充数据
     * 
     * @param record
     * @return
     */
    public ExcelService replaceCellTag(JSONObject record, String tagCategory) {
        tagCategory = tagCategory.toUpperCase();
        Cells cells = worksheet.getCells();
        Range displayRange = cells.getMaxDisplayRange();
        int totalRow = displayRange.getRowCount();
        int totalCol = displayRange.getColumnCount();
        for (int row = displayRange.getFirstRow(); row < totalRow; row++) {
            for (int col = displayRange.getFirstColumn(); col < totalCol; col++) {
                Cell cell = cells.get(row, col);
                String cellStr = cell.getStringValueWithoutFormat();
                if (StringUtils.isEmpty(cellStr))
                    continue;
                String replaceCellStr = processTagCellValue(cellStr, record, tagCategory);
                if (!StringUtils.equals(cellStr, replaceCellStr))
                    cell.setValue(replaceCellStr);
            }
        }
        return this;
    }

    /**
     * 
     * @param cellStr
     * @param record
     * @param tagCategory
     * @return
     */
    private String processTagCellValue(String cellStr, JSONObject record, String tagCategory) {
        Matcher m = tagPattern.matcher(cellStr);
        while (m.find()) {
            String tmp = m.group();
            String template = StringUtils.substringBetween(tmp, "{", "}").trim().toUpperCase();
            if (template.startsWith(tagCategory)) {
                String fieldName = StringUtils.substringAfter(template, ".");
                cellStr = StringUtils.replace(cellStr, tmp, record.getString(fieldName));
            }
        }
        return cellStr;
    }

    /**
     * 
     * @param records
     * @param fieldList
     * @param startCellName
     * @param isAutoMergeCols
     * @return
     */
    public ExcelService jsonArray2Template(JSONArray records, Object fieldList, String startCellName) {
        int[] cr = getCellColAndRow(startCellName);
        int startCol = cr[0];
        int startRow = cr[1];
        List<String> fieldNames = new ArrayList<String>();
        if (fieldList instanceof String[]) {
            for (String str : ((String[]) fieldList))
                fieldNames.add(str);
        } else if (fieldList instanceof NativeArray) {
            NativeArray array = (NativeArray) fieldList;
            for (int i = 0; i < array.getLength(); i++)
                fieldNames.add((String) array.get(i));
        }
        Cells cells = worksheet.getCells();
        Style[] styles = new Style[fieldNames.size()];
        int rowHeight = cells.getRowHeightPixel(startRow);
        for (int i = 0; i < fieldNames.size(); i++) {
            styles[i] = cells.get(startRow, startCol + i).getStyle();
        }
        for (int i = 0; i < records.size(); i++) {
            int row = startRow + i;
            JSONObject record = records.getJSONObject(i);
            for (int j = 0; j < fieldNames.size(); j++) {
                Cell cell = cells.get(row, startCol + j);
                cell.setValue(record.get(fieldNames.get(j)));
                cell.setStyle(styles[j]);
            }
            cells.setRowHeight(row, rowHeight);
        }

        return this;
    }

    /**
     * 自动合并单元格
     * 
     * @param startCell
     * @param rowNumber
     * @return
     */
    public ExcelService autoMergeColCell(String startCellName, int rowNumber) {
        int[] cr = getCellColAndRow(startCellName);
        String prevCellValue = worksheet.getCells().get(cr[1], cr[0]).getStringValue();
        if (StringUtils.isEmpty(prevCellValue))
            prevCellValue = "";
        int startRowIndex = cr[1];
        int endRowIndex = startRowIndex + 1;
        for (int i = 1; i < rowNumber; i++) {
            endRowIndex = cr[1] + i;
            String cellValue = worksheet.getCells().get(endRowIndex, cr[0]).getStringValue();
            if (StringUtils.isEmpty(cellValue))
                cellValue = "";
            if (!StringUtils.equals(prevCellValue, cellValue)) {
                if (endRowIndex - startRowIndex > 1) {
                    worksheet.getCells().merge(startRowIndex, cr[0], endRowIndex - startRowIndex, 1);
                }
                startRowIndex = endRowIndex;
                prevCellValue = cellValue;
            }
        }
        if (endRowIndex - startRowIndex > 1)
            worksheet.getCells().merge(startRowIndex, cr[0], endRowIndex - startRowIndex, 1);
        return this;
    }

    /**
     * 
     */
    private ExcelService() {

        workbook = new Workbook();
        workbook.getWorksheets().clear();
    }

    /**
     * 
     * @param excelFileName
     * @throws Exception
     */
    private ExcelService(String excelFileName) throws Exception {
        workbook = new Workbook(excelFileName);
        if (workbook.getWorksheets().getCount() != 0)
            worksheet = workbook.getWorksheets().get(workbook.getWorksheets().getActiveSheetIndex());
    }

    public ExcelService(InputStream inputStream) throws Exception {
        workbook = new Workbook(inputStream);
        if (workbook.getWorksheets().getCount() != 0)
            worksheet = workbook.getWorksheets().get(workbook.getWorksheets().getActiveSheetIndex());
    }

    /**
     * 销毁所有资源
     */
    public void dispose() {
        workbook.dispose();
    }

    /**
     * 保存到PDF文件
     * 
     * @param fileName
     * @throws Exception
     */
    public ExcelService saveToPDF(String fileName) throws Exception {
        workbook.save(fileName, SaveFormat.PDF);
        return this;
    }

    /**
     * 保存为HTML文件
     * 
     * @param string
     * @return
     * @throws Exception
     */
    public ExcelService saveToHTML(String fileName) throws Exception {
        workbook.save(fileName, SaveFormat.HTML);
        return this;
    }

    /**
     * 保存到文件
     * 
     * @param fileName
     * @throws Exception
     */
    public ExcelService save(String fileName) throws Exception {
        workbook.save(fileName, SaveFormat.EXCEL_97_TO_2003);
        return this;
    }

    /**
     * 获取工作簿总数
     * 
     * @return
     */
    public int getSheetCount() {
        return workbook.getWorksheets().getCount();
    }

    /**
     * 获取工作簿
     * 
     * @param index
     * @return
     */
    public Worksheet getSheet(int index) {
        return workbook.getWorksheets().get(index);
    }

    /**
     * 获取一个对象
     * 
     * @return
     */
    public static ExcelService getInstance() {
        return new ExcelService();
    }

    /**
     * 获取一个对象
     * 
     * @param excelFileName
     * @return
     * @throws Exception
     */
    public static ExcelService getInstance(String excelFileName) throws Exception {
        return new ExcelService(excelFileName);
    }

    /**
     * 获取堆箱
     * 
     * @param byteArrayInputStream
     * @return
     * @throws Exception
     */
    public static ExcelService getInstance(InputStream inputStream) throws Exception {
        return new ExcelService(inputStream);
    }

    /**
     * 合并多个Excel到一个文件中
     * 
     * @param excelServiceList
     * @param excelFileName
     * @throws Exception
     */
    public static void mergeMultiWorkbook(List<ExcelService> excelServiceList, String filename) throws Exception {
        Workbook wb = null;
        try {
            wb = new Workbook();
            wb.getWorksheets().clear();
            List<String> sheetNames = new ArrayList<String>();
            for (ExcelService excelService : excelServiceList) {
                for (int i = 0; i < excelService.getSheetCount(); i++) {
                    Worksheet formSheet = excelService.getSheet(i);
                    int index = wb.getWorksheets().add();
                    Worksheet toSheet = wb.getWorksheets().get(index);
                    if (sheetNames.indexOf(formSheet.getName()) == -1) {
                        toSheet.setName(formSheet.getName());
                        sheetNames.add(formSheet.getName());
                    }
                    toSheet.copy(formSheet);
                }
            }
            wb.save(filename, SaveFormat.EXCEL_97_TO_2003);
            wb.dispose();
        } finally {
            if (wb != null)
                wb.dispose();
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 设置单元格值
     * 
     * @param cellName
     * @param value
     * @return
     */
    public ExcelService setCellValueByName(String cellName, String value) {
        worksheet.getCells().get(cellName).setValue(value);
        return this;
    }

    /**
     * 设置单元格值
     * 
     * @param row
     * @param col
     * @param value
     * @return
     */
    public ExcelService setCellValueByIndex(int row, int col, String value) {
        worksheet.getCells().get(row, col).setValue(value);
        return this;
    }

    /**
     * 设置单元格字体
     * 
     * @param cellName
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    public ExcelService setCellFontByName(String cellName, String fontName, int fontSize, boolean isBold) {
        Cell cell = worksheet.getCells().get(cellName);
        Style style = cell.getStyle();
        Font font = style.getFont();
        if (!StringUtils.isEmpty(fontName))
            font.setName(fontName);
        if (fontSize > 0)
            font.setSize(fontSize);
        font.setBold(isBold);
        cell.setStyle(style);
        return this;
    }

    /**
     * 设置单元格字体
     * 
     * @param row
     * @param col
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    public ExcelService setCellFontByIndex(int row, int col, String fontName, int fontSize, boolean isBold) {
        Cell cell = worksheet.getCells().get(row, col);
        Style style = cell.getStyle();
        Font font = style.getFont();
        if (!StringUtils.isEmpty(fontName))
            font.setName(fontName);
        if (fontSize > 0)
            font.setSize(fontSize);
        font.setBold(isBold);
        cell.setStyle(style);
        return this;
    }

    /**
     * 设置单元格字体颜色及背景颜色
     * 
     * @param cellName
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setCellColorByName(String cellName, int fontColor, int backgroundColor) {
        Cell cell = worksheet.getCells().get(cellName);
        Style style = cell.getStyle();
        if (fontColor == -1)
            fontColor = ExcelServiceConst.COLOR_BLACK;
        if (backgroundColor == -1)
            backgroundColor = ExcelServiceConst.COLOR_WHITE;
        Font font = style.getFont();
        font.setColor(Color.fromArgb(fontColor));
        style.setForegroundColor(Color.fromArgb(backgroundColor));
        cell.setStyle(style);
        return this;
    }

    /**
     * 设置单元格颜色
     * 
     * @param row
     * @param col
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setCellColorByIndex(int row, int col, int fontColor, int backgroundColor) {
        Cell cell = worksheet.getCells().get(row, col);
        Style style = cell.getStyle();
        if (fontColor == -1)
            fontColor = ExcelServiceConst.COLOR_BLACK;
        if (backgroundColor == -1)
            backgroundColor = ExcelServiceConst.COLOR_WHITE;
        Font font = style.getFont();
        font.setColor(Color.fromArgb(fontColor));
        style.setForegroundColor(Color.fromArgb(backgroundColor));
        style.setBackgroundColor(Color.fromArgb(backgroundColor));
        cell.setStyle(style);
        return this;
    }

    /**
     * 设置单元格是否自动换行
     * 
     * @param cellName
     * @param isAutoWrap
     * @return
     */
    public ExcelService setCellAutoWrapByName(String cellName, boolean isAutoWrap) {
        return setCellAutoWrap(cellName, isAutoWrap);
    }

    /**
     * 设置单元格是否自动换行
     * 
     * @param cellName
     * @param isAutoWrap
     * @return
     */
    public ExcelService setCellAutoWrapByIndex(int row, int col, boolean isAutoWrap) {
        setCellAutoWrap(worksheet.getCells().get(row, col), isAutoWrap);
        return this;
    }

    /**
     * 
     * @param cellName
     * @param borderType
     * @return
     */
    public ExcelService setCellBorderByName(String cellName, int borderType) {
        Cell cell = worksheet.getCells().get(cellName);
        setCellBorder(cell, borderType);
        return this;
    }

    /**
     * 
     * @param row
     * @param col
     * @param borderType
     * @return
     */
    public ExcelService setCellBorderByIndex(int row, int col, int borderType) {
        Cell cell = worksheet.getCells().get(row, col);
        setCellBorder(cell, borderType);
        return this;
    }

    /**
     * 
     * @param cellName
     * @param borderType
     * @return
     */
    public ExcelService setCellLBorderByName(String cellName, int borderType) {
        Cell cell = worksheet.getCells().get(cellName);
        setCellLBorder(cell, borderType);
        return this;
    }

    /**
     * 
     * @param row
     * @param col
     * @param borderType
     * @return
     */
    public ExcelService setCellLBorderByIndex(int row, int col, int borderType) {
        Cell cell = worksheet.getCells().get(row, col);
        setCellLBorder(cell, borderType);
        return this;
    }

    /**
     * 
     * @param cellName
     * @param borderType
     * @return
     */
    public ExcelService setCellTBorderByName(String cellName, int borderType) {
        Cell cell = worksheet.getCells().get(cellName);
        setCellTBorder(cell, borderType);
        return this;
    }

    /**
     * 
     * @param row
     * @param col
     * @param borderType
     * @return
     */
    public ExcelService setCellTBorderByIndex(int row, int col, int borderType) {
        Cell cell = worksheet.getCells().get(row, col);
        setCellTBorder(cell, borderType);
        return this;
    }

    /**
     * 
     * @param cellName
     * @param borderType
     * @return
     */
    public ExcelService setCellRBorderByName(String cellName, int borderType) {
        Cell cell = worksheet.getCells().get(cellName);
        setCellRBorder(cell, borderType);
        return this;
    }

    /**
     * 
     * @param row
     * @param col
     * @param borderType
     * @return
     */
    public ExcelService setCellRBorderByIndex(int row, int col, int borderType) {
        Cell cell = worksheet.getCells().get(row, col);
        setCellRBorder(cell, borderType);
        return this;
    }

    /**
     * 
     * @param cellName
     * @param borderType
     * @return
     */
    public ExcelService setCellBBorderByName(String cellName, int borderType) {
        Cell cell = worksheet.getCells().get(cellName);
        setCellBBorder(cell, borderType);
        return this;
    }

    /**
     * 
     * @param row
     * @param col
     * @param borderType
     * @return
     */
    public ExcelService setCellBBorderByIndex(int row, int col, int borderType) {
        Cell cell = worksheet.getCells().get(row, col);
        setCellBBorder(cell, borderType);
        return this;
    }

    /**
     * 
     * @param cellName
     * @param borderType
     * @return
     */
    public ExcelService setCellAlignByName(String cellName, int align, int valign) {
        Cell cell = worksheet.getCells().get(cellName);
        setCellAlign(cell, align, valign);
        return this;
    }

    /**
     * 
     * @param cellName
     * @param borderType
     * @return
     */
    public ExcelService setCellAlignByIndex(int row, int col, int align, int valign) {
        Cell cell = worksheet.getCells().get(row, col);
        setCellAlign(cell, align, valign);
        return this;
    }

    /**
     * 设置列宽
     * 
     * @param colIndex
     * @param width
     * @return
     */
    public ExcelService setColWidthByIndex(int colIndex, int width) {
        worksheet.getCells().setColumnWidthPixel(colIndex, width);
        return this;
    }

    /**
     * 
     * @param cols
     * @param width
     * @return
     */
    public ExcelService setColsWidthByIndex(int[] cols, int width) {
        for (int col : cols)
            worksheet.getCells().setColumnWidthPixel(col, width);
        return this;
    }

    /**
     * 设置列宽
     * 
     * @param colName
     * @param width
     * @return
     */
    public ExcelService setColWidthByName(String colName, int width) {
        worksheet.getCells().setColumnWidthPixel(getColIndexByName(colName), width);
        return this;
    }

    /**
     * 
     * @param cols
     * @param width
     * @return
     */
    public ExcelService setColsWidthByName(String[] cols, int width) {
        for (String col : cols)
            setColWidthByName(col, width);
        return this;
    }

    /**
     * 
     * @param col
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    public ExcelService setColFontByIndex(int col, String fontName, int fontSize, boolean isBold) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int row = 0; row < rowCount; row++)
            setCellFontByIndex(row, col, fontName, fontSize, isBold);
        return this;
    }

    /**
     * 
     * @param col
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    public ExcelService setColFontByName(String col, String fontName, int fontSize, boolean isBold) {
        return setColFontByIndex(getColIndexByName(col), fontName, fontSize, isBold);
    }

    /**
     * 
     * @param cols
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    public ExcelService setColsFontByIndex(int[] cols, String fontName, int fontSize, boolean isBold) {
        for (int col : cols)
            setColFontByIndex(col, fontName, fontSize, isBold);
        return this;
    }

    /**
     * 
     * @param cols
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    public ExcelService setColsFontByName(String[] cols, String fontName, int fontSize, boolean isBold) {
        for (String col : cols)
            setColFontByName(col, fontName, fontSize, isBold);
        return this;
    }

    /**
     * 
     * @param col
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setColColorByIndex(int col, int fontColor, int backgroundColor) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int row = 0; row < rowCount; row++)
            setCellColorByIndex(row, col, fontColor, backgroundColor);
        return this;
    }

    /**
     * 
     * @param col
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setColColorByName(String col, int fontColor, int backgroundColor) {
        return setColColorByIndex(getColIndexByName(col), fontColor, backgroundColor);
    }

    /**
     * 
     * @param cols
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setColsColorByIndex(int[] cols, int fontColor, int backgroundColor) {
        for (int col : cols)
            setColColorByIndex(col, fontColor, backgroundColor);
        return this;
    }

    /**
     * 
     * @param cols
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setColsColorByName(String[] cols, int fontColor, int backgroundColor) {
        for (String col : cols)
            setColColorByName(col, fontColor, backgroundColor);
        return this;
    }

    /**
     * 
     * @param col
     * @param isAutoWrap
     * @return
     */
    public ExcelService setColAutoWrapByIndex(int col, boolean isAutoWrap) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int row = 0; row < rowCount; row++)
            setCellAutoWrapByIndex(row, col, isAutoWrap);
        return this;
    }

    /**
     * 
     * @param col
     * @param isAutoWrap
     * @return
     */
    public ExcelService setColAutoWrapByName(String col, boolean isAutoWrap) {
        return setColAutoWrapByIndex(columnName2Index(col), isAutoWrap);
    }

    /**
     * 
     * @param cols
     * @param isAutoWrap
     * @return
     */
    public ExcelService setColsAutoWrapByIndex(int[] cols, boolean isAutoWrap) {
        for (int col : cols)
            setColAutoWrapByIndex(col, isAutoWrap);
        return this;
    }

    /**
     * 
     * @param cols
     * @param isAutoWrap
     * @return
     */
    public ExcelService setColsAutoWrapByName(String[] cols, boolean isAutoWrap) {
        for (String col : cols)
            setColAutoWrapByName(col, isAutoWrap);
        return this;
    }

    /**
     * 
     * @param col
     * @param borderType
     * @return
     */
    public ExcelService setColBorderByIndex(int col, int borderType) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int row = 0; row < rowCount; row++)
            setCellBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param borderType
     * @return
     */
    public ExcelService setColBorderByName(String col, int borderType) {
        return setColBorderByIndex(columnName2Index(col), borderType);
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsBorderByIndex(int[] cols, int borderType) {
        for (int col : cols)
            setColBorderByIndex(col, borderType);
        return this;
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsBorderByName(String[] cols, int borderType) {
        for (String col : cols)
            setColBorderByName(col, borderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param LBorderType
     * @return
     */
    public ExcelService setColLBorderByIndex(int col, int LBorderType) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int row = 0; row < rowCount; row++)
            setCellLBorder(worksheet.getCells().get(row, col), LBorderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param LBorderType
     * @return
     */
    public ExcelService setColLBorderByName(String col, int LBorderType) {
        return setColLBorderByIndex(columnName2Index(col), LBorderType);
    }

    /**
     * 
     * @param cols
     * @param LBorderType
     * @return
     */
    public ExcelService setColsLBorderByIndex(int[] cols, int LBorderType) {
        for (int col : cols)
            setColLBorderByIndex(col, LBorderType);
        return this;
    }

    /**
     * 
     * @param cols
     * @param LBorderType
     * @return
     */
    public ExcelService setColsLBorderByName(String[] cols, int LBorderType) {
        for (String col : cols)
            setColLBorderByName(col, LBorderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param TBorderType
     * @return
     */
    public ExcelService setColTBorderByIndex(int col, int TBorderType) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int row = 0; row < rowCount; row++)
            setCellTBorder(worksheet.getCells().get(row, col), TBorderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param TBorderType
     * @return
     */
    public ExcelService setColTBorderByName(String col, int TBorderType) {
        return setColTBorderByIndex(columnName2Index(col), TBorderType);
    }

    /**
     * 
     * @param cols
     * @param TBorderType
     * @return
     */
    public ExcelService setColsTBorderByIndex(int[] cols, int TBorderType) {
        for (int col : cols)
            setColTBorderByIndex(col, TBorderType);
        return this;
    }

    /**
     * 
     * @param cols
     * @param TBorderType
     * @return
     */
    public ExcelService setColsTBorderByName(String[] cols, int TBorderType) {
        for (String col : cols)
            setColTBorderByName(col, TBorderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param RBorderType
     * @return
     */
    public ExcelService setColRBorderByIndex(int col, int RBorderType) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int row = 0; row < rowCount; row++)
            setCellRBorder(worksheet.getCells().get(row, col), RBorderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param RBorderType
     * @return
     */
    public ExcelService setColRBorderByName(String col, int RBorderType) {
        return setColRBorderByIndex(columnName2Index(col), RBorderType);
    }

    /**
     * 
     * @param cols
     * @param RBorderType
     * @return
     */
    public ExcelService setColsRBorderByIndex(int[] cols, int RBorderType) {
        for (int col : cols)
            setColRBorderByIndex(col, RBorderType);
        return this;
    }

    /**
     * 
     * @param cols
     * @param RBorderType
     * @return
     */
    public ExcelService setColsRBorderByName(String[] cols, int RBorderType) {
        for (String col : cols)
            setColRBorderByName(col, RBorderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param BBorderType
     * @return
     */
    public ExcelService setColBBorderByIndex(int col, int BBorderType) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int row = 0; row < rowCount; row++)
            setCellBBorder(worksheet.getCells().get(row, col), BBorderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param BBorderType
     * @return
     */
    public ExcelService setColBBorderByName(String col, int BBorderType) {
        return setColBBorderByIndex(columnName2Index(col), BBorderType);
    }

    /**
     * 
     * @param cols
     * @param BBorderType
     * @return
     */
    public ExcelService setColsBBorderByIndex(int[] cols, int BBorderType) {
        for (int col : cols)
            setColBBorderByIndex(col, BBorderType);
        return this;
    }

    /**
     * 
     * @param cols
     * @param BBorderType
     * @return
     */
    public ExcelService setColsBBorderByName(String[] cols, int BBorderType) {
        for (String col : cols)
            setColBBorderByName(col, BBorderType);
        return this;
    }

    /**
     * 设置列对齐方式
     * 
     * @param rows
     * @param fontName
     * @param fontSize
     * @param isBold
     * @param isItalic
     * @return
     */
    public ExcelService setColAlignByIndex(int col, int align, int valign) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int row = 0; row < rowCount; row++)
            setCellAlign(worksheet.getCells().get(row, col), align, valign);
        return this;
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColAlignByName(String col, int align, int valign) {
        return setColAlignByIndex(columnName2Index(col), align, valign);
    }

    /**
     * 设置列对齐方式
     * 
     * @param rows
     * @param fontName
     * @param fontSize
     * @param isBold
     * @param isItalic
     * @return
     */
    public ExcelService setColsAlignByIndex(int[] cols, int align, int valign) {
        int rowCount = worksheet.getCells().getMaxDisplayRange().getRowCount();
        for (int col : cols)
            for (int row = 0; row < rowCount; row++)
                setCellAlign(worksheet.getCells().get(row, col), align, valign);
        return this;
    }

    /**
     * 
     * @param cols
     * @param borderType
     * @return
     */
    public ExcelService setColsAlignByName(String[] cols, int align, int valign) {
        return setColsAlign(colName2Indexs(cols), align, valign);
    }

    /**
     * 设置行高
     * 
     * @param rowIndex
     * @param height
     * @return
     */
    public ExcelService setRowHeight(int rowIndex, int height) {
        worksheet.getCells().setRowHeightPixel(rowIndex, height);
        return this;
    }

    /**
     * 
     * @param rows
     * @param height
     * @return
     */
    public ExcelService setRowsHeight(int[] rows, int height) {
        for (int row : rows)
            setRowHeight(row, height);
        return this;
    }

    /**
     * 
     * @param row
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    public ExcelService setRowFont(int row, String fontName, int fontSize, boolean isBold) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int col = 0; col < colCount; col++)
            setCellFontByIndex(row, col, fontName, fontSize, isBold);
        return this;
    }

    /**
     * 
     * @param rows
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    public ExcelService setRowsFont(int[] rows, String fontName, int fontSize, boolean isBold) {
        for (int row : rows)
            setRowFont(row, fontName, fontSize, isBold);
        return this;
    }

    /**
     * 
     * @param row
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setRowColor(int row, int fontColor, int backgroundColor) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int col = 0; col < colCount; col++)
            setCellColorByIndex(row, col, fontColor, backgroundColor);
        return this;
    }

    /**
     * 
     * @param rows
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setRowsColor(int[] rows, int fontColor, int backgroundColor) {
        for (int row : rows)
            setRowColor(row, fontColor, backgroundColor);
        return this;
    }

    /**
     * 
     * @param col
     * @param isAutoWrap
     * @return
     */
    public ExcelService setRowAutoWrap(int row, boolean isAutoWrap) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int col = 0; col < colCount; col++)
            setCellAutoWrapByIndex(row, col, isAutoWrap);
        return this;
    }

    /**
     * 
     * @param cols
     * @param isAutoWrap
     * @return
     */
    public ExcelService setRowsAutoWrap(int[] rows, boolean isAutoWrap) {
        for (int row : rows)
            setRowAutoWrap(row, isAutoWrap);
        return this;
    }

    /**
     * 
     * @param col
     * @param borderType
     * @return
     */
    public ExcelService setRowBorder(int row, int borderType) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int col = 0; col < colCount; col++)
            setCellBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置单元格边框
     * 
     * @param rows
     * @param borderType
     * @return
     */
    public ExcelService setRowsBorder(int[] rows, int borderType) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int row : rows)
            for (int col = 0; col < colCount; col++)
                setCellBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置行左边框
     * 
     * @param rows
     * @param borderType
     * @return
     */
    public ExcelService setRowsLBorder(int[] rows, int borderType) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int row : rows)
            for (int col = 0; col < colCount; col++)
                setCellLBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置行上边框
     * 
     * @param rows
     * @param borderType
     * @return
     */
    public ExcelService setRowsTBorder(int[] rows, int borderType) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int row : rows)
            for (int col = 0; col < colCount; col++)
                setCellTBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置行右边框
     * 
     * @param rows
     * @param borderType
     * @return
     */
    public ExcelService setRowsRBorder(int[] rows, int borderType) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int row : rows)
            for (int col = 0; col < colCount; col++)
                setCellRBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置行下边框
     * 
     * @param rows
     * @param borderType
     * @return
     */
    public ExcelService setRowsBBorder(int[] rows, int borderType) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int row : rows)
            for (int col = 0; col < colCount; col++)
                setCellBBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param LBorderType
     * @return
     */
    public ExcelService setRowLBorder(int row, int borderType) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int col = 0; col < colCount; col++)
            setCellTBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param TBorderType
     * @return
     */
    public ExcelService setRowTBorder(int row, int borderType) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int col = 0; col < colCount; col++)
            setCellTBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param RBorderType
     * @return
     */
    public ExcelService setRowRBorder(int row, int borderType) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int col = 0; col < colCount; col++)
            setCellRBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 
     * @param col
     * @param BBorderType
     * @return
     */
    public ExcelService setRowBBorder(int row, int borderType) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int col = 0; col < colCount; col++)
            setCellBBorder(worksheet.getCells().get(row, col), borderType);
        return this;
    }

    /**
     * 设置列对齐方式
     * 
     * @param rows
     * @param fontName
     * @param fontSize
     * @param isBold
     * @param isItalic
     * @return
     */
    public ExcelService setRowAlign(int row, int align, int valign) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int col = 0; col < colCount; col++)
            setCellAlign(worksheet.getCells().get(row, col), align, valign);
        return this;
    }

    /**
     * 
     * 
     * @param rows
     * @param fontName
     * @param fontSize
     * @param isBold
     * @param isItalic
     * @return
     */
    public ExcelService setRowsAlign(int[] rows, int align, int valign) {
        int colCount = worksheet.getCells().getMaxDisplayRange().getColumnCount();
        for (int row : rows)
            for (int col = 0; col < colCount; col++)
                setCellAlign(worksheet.getCells().get(row, col), align, valign);
        return this;
    }

    /**
     * 设置范围字体
     * 
     * @param startCellName
     * @param endCellName
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    public ExcelService setRangeFontByName(String startCellName, String endCellName, String fontName, int fontSize, boolean isBold) {
        int[] firstCR = getCellColAndRow(startCellName);
        int[] endCR = getCellColAndRow(endCellName);
        return setRangeFontByIndex(firstCR[1], firstCR[0], endCR[1], endCR[0], fontName, fontSize, isBold);
    }

    /**
     * 设置范围字体
     * 
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    public ExcelService setRangeFontByIndex(int startRow, int startCol, int endRow, int endCol, String fontName, int fontSize, boolean isBold) {
        int minRow = Math.min(startRow, endRow);
        int maxRow = Math.max(startRow, endRow);
        int minCol = Math.min(startCol, endCol);
        int maxCol = Math.max(startCol, endCol);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellFontByIndex(row, col, fontName, fontSize, isBold);
        return this;
    }

    /**
     * 设置范围字体颜色及背景颜色
     * 
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setRangeColorByIndex(int startRow, int startCol, int endRow, int endCol, int fontColor, int backgroundColor) {
        int minRow = Math.min(startRow, endRow);
        int maxRow = Math.max(startRow, endRow);
        int minCol = Math.min(startCol, endCol);
        int maxCol = Math.max(startCol, endCol);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellColorByIndex(row, col, fontColor, backgroundColor);
        return this;
    }

    /**
     * 设置范围字体颜色及背景颜色
     * 
     * @param startCellName
     * @param endCellName
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setRangeColorByName(String startCellName, String endCellName, int fontColor, int backgroundColor) {
        int[] firstCR = getCellColAndRow(startCellName);
        int[] endCR = getCellColAndRow(endCellName);
        return setRangeColorByIndex(firstCR[1], firstCR[0], endCR[1], endCR[0], fontColor, backgroundColor);
    }

    /**
     * 设置范围字体颜色及背景颜色
     * 
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setRangeAlignByIndex(int startRow, int startCol, int endRow, int endCol, int align, int valign) {
        int minRow = Math.min(startRow, endRow);
        int maxRow = Math.max(startRow, endRow);
        int minCol = Math.min(startCol, endCol);
        int maxCol = Math.max(startCol, endCol);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellAlignByIndex(row, col, align, valign);
        return this;
    }

    /**
     * 设置范围字体颜色及背景颜色
     * 
     * @param startCellName
     * @param endCellName
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setRangeAlignByName(String startCellName, String endCellName, int align, int valign) {
        int[] firstCR = getCellColAndRow(startCellName);
        int[] endCR = getCellColAndRow(endCellName);
        return setRangeAlignByIndex(firstCR[1], firstCR[0], endCR[1], endCR[0], align, valign);
    }

    /**
     * 设置范围字体颜色及背景颜色
     * 
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setRangeBorderByIndex(int startRow, int startCol, int endRow, int endCol, int borderType) {
        int minRow = Math.min(startRow, endRow);
        int maxRow = Math.max(startRow, endRow);
        int minCol = Math.min(startCol, endCol);
        int maxCol = Math.max(startCol, endCol);
        for (int row = minRow; row <= maxRow; row++)
            for (int col = minCol; col <= maxCol; col++)
                setCellBorderByIndex(row, col, borderType);
        return this;
    }

    /**
     * 设置范围字体颜色及背景颜色
     * 
     * @param startCellName
     * @param endCellName
     * @param fontColor
     * @param backgroundColor
     * @return
     */
    public ExcelService setRangeBorderByName(String startCellName, String endCellName, int borderType) {
        int[] firstCR = getCellColAndRow(startCellName);
        int[] endCR = getCellColAndRow(endCellName);
        return setRangeBorderByIndex(firstCR[1], firstCR[0], endCR[1], endCR[0], borderType);
    }

    /**
     * 自动设置宽度
     * 
     * @param cols
     * @return
     * @throws Exception
     */
    public ExcelService autoColWitdhByIndex(int col) throws Exception {
        worksheet.autoFitColumn(col);
        return this;
    }

    /**
     * 
     * @param col
     * @return
     * @throws Exception
     */
    public ExcelService autoColWitdhByName(String col) throws Exception {
        worksheet.autoFitColumn(columnName2Index(col));
        return this;
    }

    /**
     * 
     * @param cols
     * @return
     * @throws Exception
     */
    public ExcelService autoColsWitdhByName(String[] cols) throws Exception {
        for (String col : cols)
            autoColWitdhByName(col);
        return this;
    }

    /**
     * 自动设置宽度
     * 
     * @param cols
     * @return
     * @throws Exception
     */
    public ExcelService autoColsWitdhByIndex(int[] cols) throws Exception {
        if (cols == null)
            worksheet.autoFitColumns();
        else
            for (int col : cols) {
                worksheet.autoFitColumn(col);
            }
        return this;
    }

    /**
     * 自动设置行高
     * 
     * @param cols
     * @return
     * @throws Exception
     */
    public ExcelService autoRowsHeight(int[] rows) throws Exception {
        if (rows == null)
            worksheet.autoFitRows();
        else
            for (int row : rows) {
                worksheet.autoFitRow(row);
            }
        return this;
    }

    /**
     * 自动设置行高
     * 
     * @param cols
     * @return
     * @throws Exception
     */
    public ExcelService autoRowHeight(int row) throws Exception {
        worksheet.autoFitRow(row);
        return this;
    }

    public ExcelService setPassowd(String pwd) throws Exception {
        workbook.protect(ProtectionType.CONTENTS, pwd);
        return this;
    }

    // Sheet
    public class ExcelSheet {
        public void add(String sheetName) {
            addSheet(sheetName);
        }

        public void select(Object obj) {
            if (obj instanceof Double)
                selectSheet(((Double) obj).intValue());
            else if (obj instanceof String)
                selectSheet((String) obj);
        }
    }

    // 单元格
    public class ExcelCell {

    }

    // 格式
    public class ExcelStyle {

    }

    // 边框
    public class ExcelBorder {

    }

    // 尺寸
    public class ExcelSize {

    }

    public final ExcelSheet sheet = new ExcelSheet();
    public final ExcelCell cell = new ExcelCell();
    public final ExcelStyle style = new ExcelStyle();
    public final ExcelBorder border = new ExcelBorder();
    public final ExcelSize size = new ExcelSize();

}