import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import com.estudio.impl.db.DBConnProvider4MySQL;
import com.estudio.impl.db.DBConnProvider4Oracle;
import com.estudio.impl.db.DBHelper4MySQL;
import com.estudio.impl.db.DBHelper4Oracle;
import com.estudio.utils.Convert;

public class CodeAssistTools {
    public static void main(String[] args) throws Exception {
        codeAssist2File();
        // file2CodeAssist("E:\\codeassist.txt");
        // deleteJavaScriptCodeAssist();
    }

    private static void deleteJavaScriptCodeAssist() throws Exception {
        DBConnProvider4MySQL.getInstance().initParams("localhost", 3306, "tour_db", "tour_db", "tour_db", 10, false);
        Connection con = DBConnProvider4MySQL.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("select id from sys_code_assistent where pid=?");
        List<Long> ids = new ArrayList<Long>();
        getCodeAssistList(stmt, ids, -1);
        PreparedStatement delStmt = con.prepareStatement("delete from sys_code_assistent where id=?");
        for (long id : ids) {
            delStmt.setLong(1, id);
            delStmt.execute();
        }

    }

    private static void getCodeAssistList(PreparedStatement stmt, List<Long> ids, long pid) throws Exception {
        stmt.setLong(1, pid);
        List<Long> tempIds = new ArrayList<Long>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next())
            tempIds.add(rs.getLong(1));
        for (long id : tempIds)
            getCodeAssistList(stmt, ids, id);
        ids.addAll(tempIds);
    }

    private static void file2CodeAssist(String filename) throws Exception {
        DBConnProvider4MySQL.getInstance().initParams("localhost", 3306, "tour_db", "tour_db", "tour_db", 10, false);
        Connection con = DBConnProvider4MySQL.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("insert into sys_code_assistent(id,pid,sortorder,caption,content,type,exttype) values (?,?,?,?,?,?,?)");

        List<String> list = FileUtils.readLines(new File(filename), "GBK");
        Map<String, Long> idxstr2Value = new HashMap<String, Long>();

        idxstr2Value.put("-1", -1l);
        for (String str : list) {
            if (StringUtils.isEmpty(StringUtils.trim(str)))
                continue;
            String idxStr = StringUtils.substringBefore(str, "=");
            String substr[] = StringUtils.substringAfter(str, "=").split("\\|");
            String parentIdxStr = !idxStr.contains(".") ? "-1" : idxStr.substring(0, idxStr.lastIndexOf("."));
            long pid = idxstr2Value.get(parentIdxStr);
            long id = DBHelper4MySQL.getInstance().getUniqueID(con);
            stmt.setLong(1, id);
            stmt.setLong(2, pid);
            stmt.setLong(3, pid);
            stmt.setString(4, substr[0]);
            stmt.setBytes(5, Convert.str2Bytes(StringEscapeUtils.unescapeJava(substr.length >= 4 ? substr[3] : "")));
            stmt.setString(6, substr[1]);
            stmt.setString(7, "null".equals(substr[2]) ? "" : substr[2]);
            stmt.execute();
            idxstr2Value.put(idxStr, id);
        }
    }

    private static void codeAssist2File() throws SQLException, IOException {
        DBConnProvider4Oracle.getInstance().initParams("localhost", 1521, "oradb", "prjdbgdsjtt", "prjdbgdsjtt", 10, false);
        Connection con = DBConnProvider4Oracle.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("select id,caption,content,help,type,exttype from sys_code_assistent where pid=? order by sortorder");
        JSONArray list = new JSONArray();
        generateCodeAssistent(stmt, list, -1l);
        StringBuilder sb = new StringBuilder();
        generateCodeAssistent2File("", list, sb);
        FileUtils.writeStringToFile(new File("E:\\codeassist.txt"), sb.toString());
    }

    private static void generateCodeAssistent2FileEx(String prefix, JSONArray list, StringBuilder sb) {
        for (int i = 0; i < list.size(); i++) {
            JSONObject json = list.getJSONObject(i);
            String str = (StringUtils.isEmpty(prefix) ? "" : (prefix + ".")) + (i + 1) + "=";
            String tempPrefix = (StringUtils.isEmpty(prefix) ? "" : (prefix + ".")) + (i + 1);
            String extType = json.getString("exttype");
            if (extType == "null")
                extType = "È«¾Ö";
            str += json.getString("caption") + "|" + json.getString("type") + "|" + extType + "|" + StringEscapeUtils.escapeJava(json.getString("content"));
            sb.append(str).append("\n");
            JSONArray tempList = json.getJSONArray("items");
            if (tempList != null)
                generateCodeAssistent2FileEx(tempPrefix, tempList, sb);
        }
    }

    private static void generateCodeAssistent2File(String prefix, JSONArray list, StringBuilder sb) {
        for (int i = 0; i < list.size(); i++) {
            JSONObject json = list.getJSONObject(i);
            String str = "";
            if (StringUtils.equals(json.getString("type"), "1"))
                str = StringEscapeUtils.escapeJava(json.getString("content")) + "£º" + json.getString("caption") + "¡£";
            sb.append(str).append("\n");
            JSONArray tempList = json.getJSONArray("items");
            if (tempList != null)
                generateCodeAssistent2File("", tempList, sb);
        }
    }

    private static void generateCodeAssistent(PreparedStatement stmt, JSONArray list, long pid) throws SQLException {
        stmt.setLong(1, pid);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            JSONObject json = new JSONObject();
            json.put("caption", rs.getString(2));
            json.put("content", Convert.bytes2Str(rs.getBytes(3)));
            json.put("help", Convert.bytes2Str(rs.getBytes(4)));
            json.put("type", rs.getInt(5));
            json.put("exttype", rs.getString(6));
            json.put("id", rs.getLong(1));
            list.add(json);
        }
        for (int i = 0; i < list.size(); i++) {
            JSONObject json = list.getJSONObject(i);
            JSONArray tempList = new JSONArray();
            generateCodeAssistent(stmt, tempList, json.getLong("id"));
            if (!tempList.isEmpty())
                json.put("items", tempList);
        }
    }
}
