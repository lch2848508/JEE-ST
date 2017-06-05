import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import com.estudio.impl.db.DBConnProvider4Oracle;
import com.estudio.utils.Convert;
import com.estudio.utils.ImageUtils;

public class GenerateFSLayerMarker {

    /**
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        DBConnProvider4Oracle.getInstance().initParams("localhost", 1521, "oradb", "prjdbfsjtjghk", "prjdbfsjtjghk", 10, false);
        processLayer(819824, 819619, "warehouse");// 仓库
        processLayer(819825, 819574, "storage");// 储罐
        processLayer(819827, 819602, "site");// 堆场
        processLayer(819826, 819506, "point-machine");// 点状机械
        processLayer(819828, 819521, "line-machine");// 线状机械
    }

    private static void processLayer(int templateId, int layerId, String picturePath) throws SQLException {
        Map<String, JSONArray> id2Pictures = new HashMap<String, JSONArray>();
        String[] files = new File("F:\\attachment\\gis-picture\\" + picturePath).list();
        if (files == null)
            files = new String[0];
        for (String fileName : files) {
            if (fileName.endsWith(".small.jpg"))
                continue;
            String objectId = StringUtils.substringBefore(fileName, "_");
            if (!id2Pictures.containsKey(objectId))
                id2Pictures.put(objectId, new JSONArray());
            JSONObject json = new JSONObject();
            json.put("category", "全部");
            json.put("url", "../../attachment/gis-picture/" + picturePath + "/" + fileName);
            json.put("small", "../../attachment/gis-picture/" + picturePath + "/" + StringUtils.replace(fileName, ".JPG", ".small.jpg"));
            json.put("descript", "");
            json.put("fileName", "");
            json.put("href", "");
            id2Pictures.get(objectId).add(json);
        }
        Connection con = DBConnProvider4Oracle.getInstance().getConnection();
        PreparedStatement stmt = con.prepareStatement("insert into webgis_layer_marker_records(id,layer_id,template_id,key_value,pictures,attributes,attachments) values (seq_for_j2ee_uniqueid.nextval,?,?,?,?,?,?)");
        for (Entry<String, JSONArray> entry : id2Pictures.entrySet()) {
            stmt.setLong(1, layerId);
            stmt.setLong(2, templateId);
            stmt.setString(3, entry.getKey());
            stmt.setBytes(4, Convert.str2Bytes(entry.getValue().toString()));
            stmt.setBytes(5, Convert.str2Bytes("[]"));
            stmt.setBytes(6, Convert.str2Bytes("[]"));
            stmt.execute();
            System.out.println("处理: " + entry.getKey() + " 完毕!");
        }

        stmt.close();
        con.close();
    }

    /*
     * ID 218175 唯一标识号 LAYER_ID 197921 图层ID TEMPLATE_ID 217232 模版ID KEY_VALUE
     * 11295 键值 ATTRIBUTES <BLOB> 属性内容 GEOMETRY <BLOB> GIS信息 PICTURES <BLOB>
     * 图片信息 ATTACHMENTS <BLOB> 附件信息
     */
    private static void processFile(File f) {
        File[] files = f.listFiles();
        for (File file : files) {
            String filename = file.getAbsolutePath();
            System.out.println("正在处理:" + file.getAbsolutePath());
            if (file.getName().contains(" ")) {
                filename = StringUtils.replace(filename, " ", "");
                file.renameTo(new File(filename));
            }
            String smallFilename = StringUtils.replace(filename, ".JPG", ".small.jpg");
            ImageUtils.getInstance().resizeImage(filename, smallFilename, 256, 256);
        }
    }

}
