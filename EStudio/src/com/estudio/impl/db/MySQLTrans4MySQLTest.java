package com.estudio.impl.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySQLTrans4MySQLTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
         String sql = "select * from table1,( select * from table2 ) t";
         System.out.println(DBSQLTrans4MySQL.getInstance().transSQL4Page(sql));
        // List<String> params = new ArrayList<String>();
        // params.add("F1");
        // sql = DBSQLTrans4MySQL.getInstance().transSQL4Page(sql, "id");
        // System.out.println(sql);
        // sql =
        // DBSQLTrans4MySQL.getInstance().generatePageOptimizeIDSQL(sql,params,"ID");
        // System.out.println(sql);

        //DBConnProvider4MySQL.getInstance().initParams("127.0.0.1", 3306, "tour_db", "tour_db", "tour_db", 50, false);
        // String sql =
        // "INSERT INTO tab_oa_address VALUES (3893, 3870, 'ÁºÉúºì', 1, NULL, NULL, NULL, NULL, '13690416171', 'shenghongl@163.com', 3893, '24269934', NULL, '24269934', 'ÁºÉúºì13690416171shenghongl@163.com2426993424269934', NULL, NULL)";
        //long id = 300000;
        //Connection con = DBConnProvider4MySQL.getInstance().getConnection();
        //Statement stmt = con.createStatement();
        //for (int i = 0; i < 2000000; i++) {
        //   String sql = "INSERT INTO tab_oa_address VALUES (" + (id++) + ", 3870, 'ÁºÉúºì', 1, NULL, NULL, NULL, NULL, '13690416171', 'shenghongl@163.com', 3893, '24269934', NULL, '24269934', 'ÁºÉúºì13690416171shenghongl@163.com2426993424269934', NULL, NULL)";
        //    stmt.execute(sql);
        //    if (i % 1000 == 0)
        //        System.out.println(i);
        //}
    }
}
