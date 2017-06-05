package com.chinadci.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.estudio.officeservice.ExcelService;

public class Excel2Oracle {
    public static void main(final String[] args) throws Exception {
        generateSQL("E:\\精准扶贫.xls", "HELP_POOR_STATION", "农村客运候车亭项目");
        //generateSQL("C:\\Users\\Administrator\\Desktop\\精准扶贫项目整理后\\危桥改造项目情况表.xlsx", "HELP_POOR_BRIDGE", "危桥改造项目");
        //generateSQL("C:\\Users\\Administrator\\Desktop\\精准扶贫项目整理后\\窄路基路面拓宽改造项目情况表.xls", "HELP_POOR_road_broaden", "窄路基路面拓宽改造项目");
        //generateSQL("C:\\Users\\Administrator\\Desktop\\精准扶贫项目整理后\\自然村公路路面硬化项目情况表人口数大于等于200.xls", "HELP_POOR_road_harden", "路面硬化项目");
    }

    private static void generateSQL(String excelFile, String tableName, String sheetName) throws Exception, IOException {
        ExcelService excelService = ExcelService.getInstance(excelFile);
        excelService.selectSheet(sheetName);
        StringBuilder sqlBuilder = new StringBuilder();
        int colCount = 0;

        String cellStr = excelService.getCellString(0, colCount);
        List<String> fieldComment = new ArrayList<String>();
        while (!StringUtils.isEmpty(cellStr)) {
            fieldComment.add(cellStr);
            cellStr = excelService.getCellString(0, ++colCount);
        }
        String createSQL = "create table " + tableName + " (";
        createSQL += "id integer,";
        for (int i = 0; i < fieldComment.size(); i++) {
            createSQL += "F" + (i + 1) + " varchar2(1000),";
        }
        createSQL += "search_caption varchar2(4000) );\n";
        sqlBuilder.append(createSQL);
        sqlBuilder.append("alter table " + tableName + " add constraint idx_" + tableName + "_id primary key (ID);\n");
        sqlBuilder.append("alter table " + tableName + " add record_id as ('" + tableName.toLowerCase() + "_'||id);\n");
        
        sqlBuilder.append("comment on table " + tableName + " is '" + sheetName + "';\n");
        for (int i = 0; i < fieldComment.size(); i++) {
            sqlBuilder.append("comment on column " + tableName + ".F" + (i + 1) + " is '" + fieldComment.get(i) + "';\n");
        }
        String viewName = "VIEW_" + tableName;
        sqlBuilder.append("create view " + viewName + " as select a.*,b.geometry from " + tableName + " a,sys_ext_geometry b where a.record_id=b.record_id(+);\n");
        sqlBuilder.append("comment on table " + viewName + " is '" + sheetName + "';\n");
        for (int i = 0; i < fieldComment.size(); i++) {
            sqlBuilder.append("comment on column " + viewName + ".F" + (i + 1) + " is '" + fieldComment.get(i) + "';\n");
        }
        sqlBuilder.append("comment on column " + viewName + ".geometry is '地图图斑';\n");
        String firstInsertSQL = "insert into " + tableName + "(id";
        for (int i = 0; i < fieldComment.size(); i++) {
            firstInsertSQL += ",F" + (i + 1);
        }
        firstInsertSQL += ",search_caption) values (";
        int rowIndex = 1;
        cellStr = excelService.getCellString(rowIndex, 0);
        while (!StringUtils.isEmpty(cellStr)) {
            String searchCaption = "";
            String insertSQL = firstInsertSQL + rowIndex;
            for (int i = 0; i < fieldComment.size(); i++) {
                cellStr = excelService.getCellString(rowIndex, i);
                insertSQL += ",'" + cellStr + "'";
                searchCaption += cellStr;
            }
            insertSQL += ",'"+searchCaption+"');\n";
            sqlBuilder.append(insertSQL);
            cellStr = excelService.getCellString(++rowIndex, 0);
        }
        FileUtils.writeStringToFile(new File("E:\\" + tableName + ".sql"), sqlBuilder.toString());
        excelService.dispose();
    }
}
