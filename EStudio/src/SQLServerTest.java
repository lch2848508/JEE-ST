import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.estudio.impl.db.DBConnProvider4SQLServer;
import com.estudio.impl.design.utils.DBCodeAssistService4SQLServer;
import com.estudio.utils.Convert;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

public class SQLServerTest {
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // "",,") || (Tablename == "")) || ((Tablename == "") || (Tablename == ""))) || (Tablename == ""
        DBConnProvider4SQLServer.getInstance().initParams(".", 1433, "ROADDATABASEGL5_440700_2016", "sa", "123456", 50, false);
        Connection con = DBConnProvider4SQLServer.getInstance().getConnection();
//        try{
//        	con = DBConnProvider4SQLServer.getInstance().getConnection();
//        }catch(Exception e){
//        	e.printStackTrace();
//        }
        String[] shapeFiles = new String[] { "GPSGD", "GPSSD", "GPSXD", "GPSYD", "GPSZD", "GPSCD", "GPSVD" };
        for (int i = 0; i < shapeFiles.length; i++)
            try {
                GenerateShapeFile(con, shapeFiles[i]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        con.close();
    }

    private static void GenerateShapeFile(Connection con, String dbfFileName) throws SQLException, Exception {
        PreparedStatement stmt = con.prepareStatement("Select * From " + dbfFileName);
        ResultSet rs = stmt.executeQuery();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.URLP.key, (new File("E:\\破解数据\\" + dbfFileName + ".shp")).toURI().toURL());
        ShapefileDataStore ds = null;
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;

        ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
        // 定义图形信息和属性信息
        String typeStr = "the_geom:LineString";

        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String fieldName = metaData.getColumnName(i + 1);
            if (StringUtils.equalsIgnoreCase("shape", fieldName))
                continue;
            typeStr += ",F" + i + ":string";
        }
        SimpleFeatureType featureType = DataUtilities.createType("the_geom", typeStr);

        ds.createSchema(featureType);
        ds.setCharset(Charset.forName("GBK"));
        // 设置Writer
        writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
        while (rs.next()) {
            rs.next();
            byte[] bs = rs.getBytes("shape");
            int num = byteToInt(bs, 4);
            double[] xx = new double[num];
            double[] yy = new double[num];
            int index = 8;
            for (int i = 0; i < num; i++) {
                xx[i] = byteToDouble(bs, index);
                index += 8;
            }
            for (int i = 0; i < num; i++) {
                yy[i] = byteToDouble(bs, index);
                index += 8;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("LINESTRING (");
            for (int i = 0; i < num; i++) {
                sb.append(xx[i]).append(" ").append(yy[i]);
                if (i != num - 1)
                    sb.append(",");
            }
            sb.append(")");
            System.out.println(sb);
            SimpleFeature feature = writer.next();
            feature.setAttribute("the_geom", new WKTReader().read(sb.toString()));

            for (int i = 0; i < metaData.getColumnCount(); i++) {
                String fieldName = metaData.getColumnName(i + 1);
                if (StringUtils.equalsIgnoreCase("shape", fieldName))
                    continue;
                Object obj = rs.getObject(fieldName);
                feature.setAttribute("F" + i, obj == null ? "" : rs.getString(fieldName));
            }
        }

        writer.write();
        writer.close();
        ds.dispose();

    }

    public static int byteToInt(byte[] data, int index) {
        ByteBuffer _intShifter = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).order(ByteOrder.LITTLE_ENDIAN);
        _intShifter.clear();
        _intShifter.put(data, index, Integer.SIZE / Byte.SIZE);
        _intShifter.flip();
        return _intShifter.getInt();
    }

    public static double byteToDouble(byte[] data, int index) {
        ByteBuffer _intShifter = ByteBuffer.allocate(Double.SIZE / Byte.SIZE).order(ByteOrder.LITTLE_ENDIAN);
        _intShifter.clear();
        _intShifter.put(data, index, Double.SIZE / Byte.SIZE);
        _intShifter.flip();
        return _intShifter.getDouble();
    }

}
