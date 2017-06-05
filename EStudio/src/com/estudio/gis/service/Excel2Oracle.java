package com.estudio.gis.service;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.estudio.officeservice.ExcelService;
import com.estudio.utils.StringUtilsLocal;

public class Excel2Oracle {
    public static boolean execute(Connection con, Statement exeStmt, long taskId, String caption, String tableName, String excelFile) throws Exception {
        boolean result = false;
        ExcelService excelService = null;
        PreparedStatement iStmt = null;
        try {
            excelService = ExcelService.getInstance(excelFile);
            excelService.selectSheet(0);
            int colCount = 0;

            String cellStr = excelService.getCellString(0, colCount);
            cellStr=StringUtilsLocal.replaceBlank(cellStr);
            List<String> fieldComment = new ArrayList<String>();
            while (!StringUtils.isEmpty(cellStr)) {
            	cellStr=StringUtilsLocal.replaceBlank(cellStr);
                fieldComment.add(cellStr);
                cellStr = excelService.getCellString(0, ++colCount);
            }
            exeStmt.execute("delete from webgis_dynamic_field where p_id=" + taskId);
            String createSQL = "create table " + tableName + " (";
            createSQL += "id integer,";
            String sql = "insert into webgis_dynamic_field(id,p_id,field_name,field_comment,schema_field_name,is_visible,is_query,is_enum,is_relate_parent,data_type,sortorder,is_primary) values (seq_for_j2ee_webgis.nextval," + taskId + ",'id','唯一标识号','id',1,0,0,0,'Number',seq_for_j2ee_webgis.nextval,1)";
            exeStmt.execute(sql);
            for (int i = 0; i < fieldComment.size(); i++) {
                createSQL += "F" + (i + 1) + " varchar2(400),";
                sql = "insert into webgis_dynamic_field(id,p_id,field_name,field_comment,schema_field_name,is_visible,is_query,is_enum,is_relate_parent,data_type,sortorder,is_primary) values (seq_for_j2ee_webgis.nextval," + taskId + ",'F" + (i + 1) + "','" + fieldComment.get(i) + "','F" + (i + 1) + "',1,0,0,0,'String',seq_for_j2ee_webgis.nextval,0)";
                exeStmt.execute(sql);
            }
            createSQL += "search_caption varchar2(4000) )";
            exeStmt.execute(createSQL);

            exeStmt.execute("alter table " + tableName + " add constraint idx_" + StringUtils.substring(tableName, 8)  + "_id primary key (ID)");
            exeStmt.execute("alter table " + tableName + " add record_id as ('" + tableName + "_'||id)");

            exeStmt.execute("comment on table " + tableName + " is '" + caption + "'");
            for (int i = 0; i < fieldComment.size(); i++) {
                exeStmt.execute("comment on column " + tableName + ".F" + (i + 1) + " is '" + fieldComment.get(i) + "'");
            }
            String viewName = "VIEW_" + tableName;
            exeStmt.execute("create view " + viewName + " as select a.*,b.geometry from " + tableName + " a,sys_ext_geometry b where a.record_id=b.record_id(+)");
            exeStmt.execute("comment on table " + viewName + " is '" + caption + "'");
            for (int i = 0; i < fieldComment.size(); i++) {
                exeStmt.execute("comment on column " + viewName + ".F" + (i + 1) + " is '" + fieldComment.get(i) + "'");
            }
            exeStmt.execute("comment on column " + viewName + ".geometry is '地图图斑'");
            String insertSQL = "insert into " + tableName + "(id";
            for (int i = 0; i < fieldComment.size(); i++) {
                insertSQL += ",F" + (i + 1);
            }
            insertSQL += ",search_caption) values (?";
            for (int i = 0; i < fieldComment.size(); i++) {
                insertSQL += ",?";
            }
            insertSQL += ",?)";
            iStmt = con.prepareStatement(insertSQL);

            int rowIndex = 1;
            cellStr = excelService.getCellString(rowIndex, 0);
            cellStr=StringUtilsLocal.replaceBlank(cellStr);
            while (!StringUtils.isEmpty(cellStr)) {
                String searchCaption = "";
                iStmt.setLong(1, rowIndex);
                for (int i = 0; i < fieldComment.size(); i++) {
                    cellStr = excelService.getCellString(rowIndex, i);
                	cellStr=StringUtilsLocal.replaceBlank(cellStr);
                    iStmt.setString(2 + i, fixStrSize(cellStr, 400));
                    searchCaption += cellStr;
                }
                iStmt.setString(fieldComment.size() + 2, fixStrSize(searchCaption, 4000));
                iStmt.execute();
                cellStr = excelService.getCellString(++rowIndex, 0);
            }
            result = true;
        } catch (Exception e) {
        	e.printStackTrace();
            exeStmt.execute("update webgis_dynamic_service set last_error_msg='" + e.getMessage() + "' where id=" + taskId);
        } finally {
            if (excelService != null)
                excelService.dispose();
            if (iStmt != null)
                iStmt.close();
        }
        return result;
    }

    private static String fixStrSize(String cellStr, int size) {
        if (StringUtils.isEmpty(cellStr))
            return "";
        int length = cellStr.getBytes(Charset.forName("GBK")).length;
        while (length > size) {
            cellStr = cellStr.substring(0, cellStr.length() - 2);
            length = cellStr.getBytes(Charset.forName("GBK")).length;
        }
        return cellStr;
    }
}
