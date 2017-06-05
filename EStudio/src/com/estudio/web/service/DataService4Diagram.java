package com.estudio.web.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.estudio.utils.JSONUtils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class DataService4Diagram {
    private static DataService4Diagram instance = new DataService4Diagram();

    public static DataService4Diagram getInstance() {
        return instance;
    }

    private DataService4Diagram() {

    }

    private IDBHelper DBHelper = RuntimeContext.getDbHelper();

    /**
     * 获取流程图内容
     * 
     * @param diagramName
     * @return
     * @throws Exception
     */
    public JSONObject getDiagram(String diagramName) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        JSONObject json = new JSONObject();
        json.put("r", false);
        try {
            con = DBHelper.getConnection();
            stmt = con.prepareStatement("select property from sys_workflow_d_process where name = ?");
            stmt.setString(1, diagramName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                json.put("diagram", getDiagramJson(Convert.bytes2Str(rs.getBytes(1))));
                json.put("r", true);
            }
        } finally {
            DBHelper.closeStatement(stmt);
            DBHelper.closeConnection(con);
        }
        return json;
    }

    /**
     * 
     * @param designProperty
     * @return
     */
    private JSONObject getDiagramJson(final String designProperty) {
        JSONObject diagramJson = new JSONObject();
        final JSONObject json = JSONUtils.parserJSONObject(designProperty);

        if (json.containsKey("Forms")) {
            JSONArray formArray = json.getJSONArray("Forms");
            for (int j = 0; j < formArray.size(); j++) {
                JSONObject formJson = formArray.getJSONObject(j);
                formJson.remove("Controls");
                JSONArray params = formJson.getJSONArray("params");
                for (int m = 0; m < params.size(); m++) {
                    JSONObject paramJson = params.getJSONObject(m);
                    String v = StringUtils.substringBetween(paramJson.getString("value"), "[", "]");
                    paramJson.put("value", v);
                }
            }
            diagramJson.put("Forms", formArray);
        }

        // 生成UI Diagram JSON;
        int offsetLeft = 65535; // 偏移量 X
        int offsetTop = 65535; // 偏移量 Y
        int diagramWidth = 0;
        int diagramHeight = 0;
        JSONArray array = json.getJSONArray("Actions");
        for (int i = 0; i < array.size(); i++) {
            final JSONObject activityJson = array.getJSONObject(i);
            final JSONObject aJson = new JSONObject();
            aJson.put("Caption", activityJson.getString("Caption"));
            aJson.put("Type", activityJson.getString("Type"));
            aJson.put("FontColor", activityJson.getInt("FontColor"));
            aJson.put("Background", activityJson.getInt("Background"));
            aJson.put("Bg", activityJson.getInt("Bg"));
            aJson.put("Fc", activityJson.getInt("Fc"));
            aJson.put("X", activityJson.getInt("X"));
            aJson.put("Y", activityJson.getInt("Y"));
            aJson.put("W", activityJson.getInt("Width"));
            aJson.put("H", activityJson.getInt("Height"));
            aJson.put("Name", activityJson.getString("Name"));
            if (activityJson.containsKey("Forms")) {
                JSONArray formArray = activityJson.getJSONArray("Forms");
                for (int j = 0; j < formArray.size(); j++)
                    JSONUtils.append(aJson, "Forms", formArray.getJSONObject(j).get("ID"));
            }
            offsetLeft = Math.min(offsetLeft, activityJson.getInt("X"));
            offsetTop = Math.min(offsetTop, activityJson.getInt("Y"));
            diagramWidth = Math.max(activityJson.getInt("X") + activityJson.getInt("Width"), diagramWidth);
            diagramHeight = Math.max(activityJson.getInt("Y") + activityJson.getInt("Height"), diagramHeight);
            JSONUtils.append(diagramJson, "actions", aJson);
        }
        array = json.getJSONArray("Links");
        for (int i = 0; i < array.size(); i++) {
            final JSONObject lJson = array.getJSONObject(i);
            final JSONObject aJson = JSONUtils.parserJSONObject(lJson.toString());
            final JSONArray ps = pointsStr2Point(lJson.getString("Points"));
            for (int j = 0; j < ps.size(); j++) {
                offsetLeft = Math.min(offsetLeft, ps.getJSONArray(j).getInt(0));
                offsetTop = Math.min(offsetTop, ps.getJSONArray(j).getInt(1));
                diagramWidth = Math.max(diagramWidth, ps.getJSONArray(j).getInt(0));
                diagramHeight = Math.max(diagramHeight, ps.getJSONArray(j).getInt(1));
            }
            aJson.put("Points", ps);
            JSONUtils.append(diagramJson, "links", aJson);
        }

        diagramJson.put("offsetLeft", offsetLeft);
        diagramJson.put("offsetTop", offsetTop);
        diagramJson.put("diagramWidth", diagramWidth - offsetLeft);
        diagramJson.put("diagramHeight", diagramHeight - offsetTop);

        //System.out.println(diagramJson.toString());
        // Link属性
        return diagramJson;
    }

    private JSONArray pointsStr2Point(final String points) {
        final JSONArray array = JSONUtils.parserJSONArray("[" + StringUtils.replace(StringUtils.replace(points, "(", "["), ")", "]") + "]");
        final JSONArray result = new JSONArray();
        for (int i = 0; i < array.size(); i++)
            if ((i == 0) || (array.getJSONArray(i).getInt(0) != result.getJSONArray(result.size() - 1).getInt(0)) || (array.getJSONArray(i).getInt(1) != result.getJSONArray(result.size() - 1).getInt(1)))
                result.add(array.getJSONArray(i));
        return result;
    }

}
