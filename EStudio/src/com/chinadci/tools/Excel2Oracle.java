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
        generateSQL("E:\\��׼��ƶ.xls", "HELP_POOR_STATION", "ũ����˺�ͤ��Ŀ");
        //generateSQL("C:\\Users\\Administrator\\Desktop\\��׼��ƶ��Ŀ�����\\Σ�Ÿ�����Ŀ�����.xlsx", "HELP_POOR_BRIDGE", "Σ�Ÿ�����Ŀ");
        //generateSQL("C:\\Users\\Administrator\\Desktop\\��׼��ƶ��Ŀ�����\\խ·��·���ؿ������Ŀ�����.xls", "HELP_POOR_road_broaden", "խ·��·���ؿ������Ŀ");
        //generateSQL("C:\\Users\\Administrator\\Desktop\\��׼��ƶ��Ŀ�����\\��Ȼ�幫··��Ӳ����Ŀ������˿������ڵ���200.xls", "HELP_POOR_road_harden", "·��Ӳ����Ŀ");
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
        sqlBuilder.append("comment on column " + viewName + ".geometry is '��ͼͼ��';\n");
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
