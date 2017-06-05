package com.chinadci.jt.daemon;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.lang3.StringUtils;

import com.estudio.impl.db.DBConnProvider4Oracle;
import com.estudio.impl.db.DBHelper4Oracle;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;

public class CSVProcessService {

    private static IDBHelper DBHelper = DBHelper4Oracle.getInstance();

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        DBConnProvider4Oracle.getInstance().initParams("localhost", 1521, "oradb", "prjdbgdsjtt4od", "prjdbgdsjtt4od", 50, false);
        String csvFile = "E:\\广东省2015年12月高速公路收费明细表_8_201512_058.csv";
        processCSV2Oracle(csvFile);
    }

    private static void processCSV2Oracle(String csvFile) throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        FileInputStream fis = null;
        String SQL = "insert into tab_od_csv\n"
                + //
                "  (id, entry_code, entry_road_code, entry_site_code, entry_lane_code, entry_lane_type, entry_datetime, exit_code, exit_road_code, exit_site_code, exit_lane_code, exit_lane_type, exit_datetime, car_code, car_type, car_class, distance_km, total_axle, axle_type, total_weight, limit_weight, out_range_weight_percent, free_of_charge_type, road_sign, obu_code, is_green_road, pay_type, car_rate_of_flow)\n"
                + //
                "values (seq_4_csv.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            con = DBHelper.getNativeConnection();
            stmt = con.prepareStatement(SQL);
            fis = new FileInputStream(csvFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            line = reader.readLine();
            int totalLine = 0;
            long beginTime = System.currentTimeMillis();
            while (line != null) {
                String[] vs = line.split(",");
                if(StringUtils.isEmpty(vs[0]))stmt.setObject(1, null); else stmt.setInt(1, Convert.str2Int(vs[0])); //entry_code,
                if(StringUtils.isEmpty(vs[1]))stmt.setObject(2, null); else stmt.setInt(2, Convert.str2Int(vs[1]));//entry_road_code,
                if(StringUtils.isEmpty(vs[2]))stmt.setObject(3, null); else stmt.setInt(3, Convert.str2Int(vs[2]));//entry_site_code,
                if(StringUtils.isEmpty(vs[3]))stmt.setObject(4, null); else stmt.setInt(4, Convert.str2Int(vs[3]));//entry_lane_code,
                if(StringUtils.isEmpty(vs[4]))stmt.setObject(5, null); else stmt.setInt(5, Convert.str2Int(vs[4]));//entry_lane_type,
                vs[5] = StringUtils.replace(vs[5], "\"", "");
                if(StringUtils.isEmpty(vs[5]))stmt.setObject(6, null); else stmt.setTimestamp(6,Convert.date2SQLDateTime(Convert.str2DateTime(vs[5])));//entry_datetime,
                if(StringUtils.isEmpty(vs[6]))stmt.setObject(7, null); else stmt.setInt(7, Convert.str2Int(vs[6]));//exit_code,
                if(StringUtils.isEmpty(vs[7]))stmt.setObject(8, null); else stmt.setInt(8, Convert.str2Int(vs[7]));//exit_road_code,
                if(StringUtils.isEmpty(vs[8]))stmt.setObject(9, null); else stmt.setInt(9, Convert.str2Int(vs[8]));//exit_site_code,
                if(StringUtils.isEmpty(vs[9]))stmt.setObject(10, null); else stmt.setInt(10, Convert.str2Int(vs[9]));//exit_lane_code,
                if(StringUtils.isEmpty(vs[10]))stmt.setObject(11, null); else stmt.setInt(11, Convert.str2Int(vs[10]));//exit_lane_type,
                vs[11] = StringUtils.replace(vs[11], "\"", "");
                if(StringUtils.isEmpty(vs[11]))stmt.setObject(12, null); else stmt.setTimestamp(12,Convert.date2SQLDateTime(Convert.str2DateTime(vs[11])));//exit_datetime,
                vs[12] = StringUtils.trim(StringUtils.replace(vs[12], "\"", ""));
                if(StringUtils.isEmpty(vs[12]))stmt.setObject(13, null); else stmt.setString(13, vs[12]);//car_code,
                if(StringUtils.isEmpty(vs[13]))stmt.setObject(14, null); else stmt.setInt(14, Convert.str2Int(vs[13]));//car_type,
                if(StringUtils.isEmpty(vs[14]))stmt.setObject(15, null); else stmt.setInt(15, Convert.str2Int(vs[14]));//car_class,
                if(StringUtils.isEmpty(vs[15]))stmt.setObject(16, null); else stmt.setDouble(16, Convert.str2Double(vs[15]));//distance_km,
                if(StringUtils.isEmpty(vs[16]))stmt.setObject(17, null); else stmt.setInt(17, Convert.str2Int(vs[16]));//total_axle,
                if(StringUtils.isEmpty(vs[17]))stmt.setObject(18, null); else stmt.setInt(18, Convert.str2Int(vs[17]));//axle_type,
                if(StringUtils.isEmpty(vs[18]))stmt.setObject(19, null); else stmt.setDouble(19, Convert.str2Double(vs[18]));//total_weight,
                if(StringUtils.isEmpty(vs[19]))stmt.setObject(20, null); else stmt.setDouble(20, Convert.str2Double(vs[19]));//limit_weight,
                if(StringUtils.isEmpty(vs[20]))stmt.setObject(21, null); else stmt.setDouble(21, Convert.str2Double(vs[20]));//out_range_weight_percent,
                if(StringUtils.isEmpty(vs[21]))stmt.setObject(22, null); else stmt.setInt(22, Convert.str2Int(vs[21]));//free_of_charge_type,
                if(StringUtils.isEmpty(vs[22]))stmt.setObject(23, null); else stmt.setInt(23, Convert.str2Int(vs[22]));//road_sign,
                if(StringUtils.isEmpty(vs[23]))stmt.setObject(24, null); else stmt.setInt(24, Convert.str2Int(vs[23]));//obu_code,
                if(StringUtils.isEmpty(vs[24]))stmt.setObject(25, null); else stmt.setInt(25, Convert.str2Int(vs[24]));//is_gree_road,
                if(StringUtils.isEmpty(vs[25]))stmt.setObject(26, null); else stmt.setInt(26, Convert.str2Int(vs[25]));//pay_type,
                if(StringUtils.isEmpty(vs[26]))stmt.setObject(27, null); else stmt.setInt(27, Convert.str2Int(vs[26]));//car_rate_of_flow
                stmt.addBatch();
                totalLine++;
                if (totalLine % 5000 == 0) {
                    stmt.executeBatch();
                    System.out.println("process:" + totalLine);
                }
                line = reader.readLine();
            }
            if (totalLine % 5000 != 0)
                stmt.executeBatch();
            System.out.println("Total Line:" + totalLine);
            System.out.println("Total Time:" + (System.currentTimeMillis()-beginTime));
        } finally {
            if (fis != null)
                fis.close();
            DBHelper.closeStatement(stmt);
            DBHelper.closeConnection(con);
        }
    }

}
