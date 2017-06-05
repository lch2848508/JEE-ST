import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.estudio.utils.Convert;

public class JDBCTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String driver = "oracle.jdbc.driver.OracleDriver";
        // String url = "jdbc:oracle:thin:@127.0.0.1:1521:pdbmyoracle";//
        // 设置连接字符串
        String url = "jdbc:oracle:thin:@localhost:1521:oradb";
        String username = "prjdbgt";// 用户名
        String password = "prjdbgt";// 密码
        Connection conn = null; // 创建数据库连接对象
        PreparedStatement stmt = null;
        PreparedStatement ustmt = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.prepareStatement("select id,property from sys_portal_item where p_id=46");
            ustmt = conn.prepareStatement("update sys_portal_item set property=? where id=?");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                long id = rs.getLong(1);
                String property = Convert.bytes2Str(rs.getBytes(2));
                property= StringUtils.replace(property, "44", "46");
                ustmt.setLong(2, id);
                ustmt.setBytes(1, Convert.str2Bytes(property));
                ustmt.execute();
            }
            System.out.println(conn);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
