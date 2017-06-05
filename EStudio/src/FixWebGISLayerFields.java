import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.estudio.impl.db.DBConnProvider4Oracle;
import com.estudio.utils.SecurityUtils;

public class FixWebGISLayerFields {

    /**
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        DBConnProvider4Oracle.getInstance().initParams("19.130.250.15", 1521, "orcl", "prjdbfs", "prjdbfs", 10, false);
        Connection con = DBConnProvider4Oracle.getInstance().getConnection();
        Statement tempStmt = con.createStatement();
        PreparedStatement stmt = con.prepareStatement("update webgis_layer_fields set SCHEMA_FIELD_NAME=? where id=?");
        PreparedStatement stmt1 = con.prepareStatement("select id,name from webgis_layer_fields");
        ResultSet rs = stmt1.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString(2));
            stmt.setString(1, fixShapeFieldName2OracleFieldName(tempStmt, rs.getString(2)));
            stmt.setLong(2, rs.getLong(1));
            stmt.execute();
        }
        System.out.println("Hello");

    }

    public static String fixShapeFieldName2OracleFieldName(Statement stmt, String fieldName) {
        fieldName = fieldName.toUpperCase();
        if (fieldName.getBytes().length != fieldName.length() || fieldName.getBytes().length >= 31) {
            fieldName = "F" + SecurityUtils.md5(fieldName).substring(8, 24);
        } else {
            String sql = "select 1 as" + fieldName + " from dual";
            try {
                stmt.executeQuery(sql);
            } catch (Exception e) {
                System.out.println(fieldName);
                fieldName = "F" + SecurityUtils.md5(fieldName).substring(8, 24);
            }
        }
        return fieldName;
    }

}
