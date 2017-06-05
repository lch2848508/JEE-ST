package com.estudio.tools;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.estudio.impl.db.DBConnProvider4Oracle;
import com.estudio.utils.Convert;

public class FixForms {

    private static List<String> filterContent = new ArrayList<String>();

    /**
     * @param args
     * @throws SQLException
     * @throws IOException
     */
    public static void main(final String[] args) throws SQLException, IOException {
        filterContent.add("OnlyDropDownList");
        filterContent.add("InputFormat");
        filterContent.add("PopupStyle");
        filterContent.add("DynamicLoad");
        filterContent.add("InitDataBind");
        filterContent.add("AppendNullValue");
        filterContent.add("Require");
        filterContent.add("IsQueryButton");
        filterContent.add("ButtonType");
        filterContent.add("URL");
        filterContent.add("Require");
        filterContent.add("FormatRegEx");
        filterContent.add("Alignment");
        filterContent.add("MaskString");
        filterContent.add("DynamicLoad");
        filterContent.add("LinksField");
        filterContent.add("ParamDataSource");
        // filterContent.add("ParentRelations");
        filterContent.add("ParamRelations");
        filterContent.add("ParamInitRelations");
        filterContent.add("DefaultValue");
        filterContent.add("CompareOperation");
        filterContent.add("RTablePosIsDefine");
        filterContent.add("RTableLeft");
        filterContent.add("RTableTop");
        filterContent.add("RTableWidth");
        filterContent.add("RTableHeight");
        filterContent.add("SortOrder");
        filterContent.add("InsertEmptyRow");
        filterContent.add("RightConfig");
        filterContent.add("RefreshAfterSave");
        fixObjectFormPropertys();
    }

    private static void fixObjectFormPropertys() throws SQLException, IOException {
        DBConnProvider4Oracle.getInstance().initParams("localhost", 1521, "oradb", "nh_landmonitor_ii", "nh_landmonitor_ii", 50, false);
        Connection con = DBConnProvider4Oracle.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("select id,dfmstream from sys_object_forms");
        PreparedStatement updateStmt = con.prepareStatement("update sys_object_forms set dfmstream=?,version=version+1 where id=?");
        ResultSet rs = stmt.executeQuery();
        StringBuilder sb = new StringBuilder();
        String[] skipFilters = new String[filterContent.size()];
        for (int i = 0; i < filterContent.size(); i++)
            skipFilters[i] = filterContent.get(i);
        while (rs.next()) {
            String dfmStr = Convert.bytes2Str(rs.getBytes(2));
            long id = rs.getLong(1);
            dfmStr = fixCompactDFM(dfmStr, skipFilters);
            sb.append(dfmStr).append("\n");
            updateStmt.setLong(2, id);
            updateStmt.setBytes(1, Convert.str2Bytes(dfmStr));
            updateStmt.execute();
        }
        // FileUtils.writeStringToFile(new File("E:\\1234.dfm"), sb.toString());
        con.close();
    }

    /**
     * ÐÞ¸´¼æÈÝÐÔÐÔ
     * 
     * @param dfmStr
     * @param skipFilters
     * @return
     */
    private static String fixCompactDFM(String dfmStr, String[] skipFilters) {
        StringBuilder sb = new StringBuilder();
        String[] dfmList = dfmStr.split("\n");
        String objectType = "";
        boolean skip = false;
        for (int i = 0; i < dfmList.length; i++) {
            skip = false;
            String str = dfmList[i].trim();
            if (StringUtils.startsWith(str, "object "))
                objectType = StringUtils.substringAfter(str, ":").trim();
            if (!StringUtils.equals(objectType, "TValidatorLabel") && StringUtils.startsWithAny(str, skipFilters))
                skip = true;
            if (StringUtils.startsWith(str, "ParentRelations")) {
                String tempStr = dfmList[i].trim();
                while (!StringUtils.endsWith(tempStr, ">")) {
                    tempStr = dfmList[i++].trim();
                }
                skip = true;
            }
            if (!skip)
                sb.append(dfmList[i]).append("\n");
        }
        return sb.toString();
    }

}
