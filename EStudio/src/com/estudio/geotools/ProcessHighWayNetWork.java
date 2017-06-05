package com.estudio.geotools;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import oracle.spatial.geometry.JGeometry;
import oracle.spatial.network.Link;
import oracle.spatial.network.Network;
import oracle.spatial.network.NetworkManager;
import oracle.spatial.network.Path;

import org.apache.commons.lang3.StringUtils;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import com.estudio.gis.oracle.OracleSpatialUtils;
import com.estudio.impl.db.DBConnProvider4Oracle;
import com.estudio.impl.db.DBHelper4Oracle;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

public class ProcessHighWayNetWork {

    private static IDBHelper DBHELPER = null;

    /**
     * 处理高速公路路网
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        DBConnProvider4Oracle.getInstance().initParams("localhost", 1521, "oradb", "prjdbgt", "prjdbgt", 50, false);
        DBHELPER = DBHelper4Oracle.getInstance();
        /**
         * 第一步首先生成高速公路节点表及连接表
         */
        // Shape2Oracle("C:\\Users\\shenghongl\\Desktop\\shp\\2016高速.shp",
        // "network_highway_info");
        // Shape2Oracle("C:\\Users\\shenghongl\\Desktop\\shp\\收费站点.shp",
        // "network_highway_station");
        // readShapeFile();
        // createNodeAndLink();
        // readStationShapeFile();
        // getShortestPath();
        // testShapeFile();
    }

    // 将Shape文件添加到Oracle中
    private static void Shape2Oracle(String shapeFileName, String tableName) throws Exception {
        // TODO Auto-generated method stub
        Connection con = DBHELPER.getNativeConnection();
        DBHELPER.execute("drop table " + tableName, con);

        // 创建表结构
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory.createDataStore(new File(shapeFileName).toURI().toURL());
        sds.setCharset(Charset.forName("GBK"));
        SimpleFeatureType featureType = sds.getSchema();
        List<String> nameList = new ArrayList<String>();
        Map<String, String> name2type = new HashMap<String, String>();
        for (AttributeDescriptor attribute : featureType.getAttributeDescriptors()) {
            String name = attribute.getLocalName();
            String type = attribute.getType().getBinding().getSimpleName();
            if (StringUtils.equalsIgnoreCase(name, "the_geom"))
                continue;
            nameList.add(name);
            name2type.put(name, type);
            System.out.println(name + ":" + type);
        }

        StringBuffer sb = new StringBuffer();
        sb.append("create table ").append(tableName).append("(");
        for (String fieldName : nameList) {
            sb.append(fieldName).append(" ");
            String type = name2type.get(fieldName);
            if ("Integer".equalsIgnoreCase(type) || "Double".equalsIgnoreCase(type) || "Long".equalsIgnoreCase(type))
                sb.append("number");
            else if ("String".equalsIgnoreCase(type))
                sb.append("varchar2(254)");
            sb.append(",");
        }
        sb.append("geometry  MDSYS.SDO_GEOMETRY)");
        DBHELPER.execute(sb.toString(), con);

        sb = new StringBuffer();
        sb.append("insert into ").append(tableName).append("(");
        for (String fieldName : nameList)
            sb.append(fieldName).append(",");
        sb.append("geometry) values (");
        for (String fieldName : nameList)
            sb.append("?").append(",");
        sb.append("?)");
        PreparedStatement stmt = con.prepareStatement(sb.toString());

        // 插入记录
        SimpleFeatureSource featureSource = sds.getFeatureSource();
        SimpleFeatureIterator itertor = featureSource.getFeatures().features();
        while (itertor.hasNext()) {
            SimpleFeature feature = itertor.next();
            Geometry lineString = (Geometry) feature.getAttribute("the_geom");
            JGeometry geometry = OracleSpatialUtils.toJGeometry(lineString);
            int index = 1;
            for (String fieldName : nameList) {
                String type = name2type.get(fieldName);
                if ("Integer".equalsIgnoreCase(type))
                    stmt.setInt(index, (Integer) (feature.getAttribute(fieldName)));
                if ("Long".equalsIgnoreCase(type))
                    stmt.setLong(index, (Long) (feature.getAttribute(fieldName)));
                else if ("Double".equalsIgnoreCase(type))
                    stmt.setDouble(index, (Double) (feature.getAttribute(fieldName)));
                else if ("String".equalsIgnoreCase(type))
                    stmt.setString(index, (String) (feature.getAttribute(fieldName)));
                index++;
            }
            stmt.setObject(index++, JGeometry.store(geometry, con));
            stmt.execute();
            System.out.println(lineString);
        }

    }

    public static String getShortestPath() {
        String wkt = "";
        Connection con = null;
        PreparedStatement stmt = null;
        PreparedStatement selStmt = null;
        PreparedStatement insStmt = null;
        int OKTimes = 0;
        int ErrorTimes = 0;
        try {
            int N1 = 0;
            int N2 = 0;
            con = DBHELPER.getNativeConnection();
            Network network = NetworkManager.readNetwork(con, "networt_highway");
            selStmt = con.prepareStatement("select * from NETWORK_HIGHWAY_N2N t");
            insStmt = con.prepareStatement("insert into network_highway_n2n_link (id, n2n_id, link_id,sortorder) values (seq_for_j2ee_webgis.nextval, ?, ?,?)");
            ResultSet rs = selStmt.executeQuery();
            int index = 0;
            while (rs.next()) {
                index++;
                int id = rs.getInt(1);
                N1 = rs.getInt(4);
                N2 = rs.getInt(5);
                System.out.println("正在处理第：" + index + "条记录 N1:" + N1 + "N2:" + N2 + "...");
                Path path = NetworkManager.shortestPath(network, N1, N2);

                if (path == null) {
                    System.out.println("Error:" + N1 + " " + N2);
                    ErrorTimes++;
                    continue;
                }
                Link[] links = path.getLinkArray();
                int sortorderIndex = 1;
                for (Link k : links) {
                    insStmt.setLong(1, id);
                    insStmt.setLong(2, k.getID());
                    insStmt.setInt(3, sortorderIndex++);
                    insStmt.execute();
                }
                // Node[] ns = path.getNodeArray();
                // StringBuffer sb = new StringBuffer();
                // System.out.println(links[0].getID());
                OKTimes++;
            }
            System.out.println("OK:" + OKTimes + "Error:" + ErrorTimes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBHELPER.closeStatement(stmt);
            DBHELPER.closeConnection(con);
        }
        return wkt;
    }

    /**
     * 根据路网生成节点数据 节点数据为每条路的最后一个字段
     */

    private static void createNodeAndLink() throws Exception {
        Connection con = DBHELPER.getNativeConnection();
        PreparedStatement stmt = con.prepareStatement("insert into network_highway_node (id, x, y, geometry) values (?, ?, ?, ?)");
        PreparedStatement selStmt = con.prepareStatement("select count(*) from network_highway_node where id=?");
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        PreparedStatement insertLinkStmt = con.prepareStatement("insert into network_highway_link (id, start_node, end_node, length,geometry) values (?, ?, ?, ?, ?)");
        try {
            ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory.createDataStore(new File("C:\\Users\\shenghongl\\Desktop\\shp\\2016高速.shp").toURI().toURL());
            sds.setCharset(Charset.forName("GBK"));
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            SimpleFeatureIterator itertor = featureSource.getFeatures().features();
            while (itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                Geometry lineString = (MultiLineString) feature.getAttribute("the_geom");
                lineString = lineString.getGeometryN(0);
                Coordinate[] coords = lineString.getCoordinates();
                Coordinate startCoord = coords[0];
                Coordinate endCoord = coords[coords.length - 1];
                long startNodeId = saveNodePoint(con, stmt, selStmt, startCoord);
                long endNodeId = saveNodePoint(con, stmt, selStmt, endCoord);

                insertLinkStmt.setLong(1, Convert.str2Long(feature.getAttribute("ID").toString()));
                insertLinkStmt.setLong(2, startNodeId);
                insertLinkStmt.setLong(3, endNodeId);
                insertLinkStmt.setDouble(4, lineString.getLength());
                JGeometry geometry = OracleSpatialUtils.toJGeometry(lineString);
                geometry.setSRID(4326);
                insertLinkStmt.setObject(5, JGeometry.store(geometry, con));
                insertLinkStmt.execute();
                System.out.println(lineString);

            }
            itertor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBHELPER.closeStatement(stmt);
        DBHELPER.closeConnection(con);
    }

    private static long saveNodePoint(Connection con, PreparedStatement stmt, PreparedStatement selStmt, Coordinate coord) throws SQLException {
        int x = (int) (coord.x * 1000);
        int y = (int) (coord.y * 100);
        int id = x * 10000 + y;
        selStmt.setLong(1, id);
        ResultSet rs = selStmt.executeQuery();
        rs.next();
        if (rs.getInt(1) == 0) {
            stmt.setLong(1, id);
            stmt.setLong(2, (long) (coord.x * 10000));
            stmt.setLong(3, (long) (coord.y * 10000));
            JGeometry geometry = JGeometry.createPoint(new double[] { coord.x, coord.y }, 2, 4326);
            stmt.setObject(4, JGeometry.store(geometry, con));
            stmt.execute();
        }
        return id;
    }

    /**
     * 首先路网数据入库
     * 
     * @throws Exception
     */
    private static void readShapeFile() throws Exception {
        Connection con = DBHELPER.getNativeConnection();
        PreparedStatement stmt = con.prepareStatement("insert into network_highway_info (id, ldbh, ldmc, roadcode, start_zh, end_zh, geometry) values (?, ?, ?, ?, ?, ?, ?)");
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory.createDataStore(new File("E:\\广东省高速公路路网\\高速公路网.shp").toURI().toURL());
            sds.setCharset(Charset.forName("GBK"));
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            SimpleFeatureIterator itertor = featureSource.getFeatures().features();
            while (itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                Geometry lineString = (MultiLineString) feature.getAttribute("the_geom");
                lineString = lineString.getGeometryN(0);
                stmt.setLong(1, Convert.str2Long(feature.getAttribute("ID").toString()));
                stmt.setLong(2, Convert.str2Long(feature.getAttribute("LDBH").toString()));
                stmt.setString(3, feature.getAttribute("LDMC").toString());
                stmt.setString(4, feature.getAttribute("ROADCODE").toString());
                stmt.setDouble(5, Convert.str2Double(feature.getAttribute("START_ZH").toString()));
                stmt.setDouble(6, Convert.str2Double(feature.getAttribute("END_ZH").toString()));
                JGeometry geometry = OracleSpatialUtils.toJGeometry(lineString);
                geometry.setSRID(4326);
                stmt.setObject(7, JGeometry.store(geometry, con));
                stmt.execute();
                System.out.println(lineString);
            }
            itertor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBHELPER.closeStatement(stmt);
        DBHELPER.closeConnection(con);
    }

    /**
     * 首先路网数据入库
     * 
     * @throws Exception
     */
    private static void readStationShapeFile() throws Exception {
        Connection con = DBHELPER.getNativeConnection();
        PreparedStatement stmt = con.prepareStatement("insert into netword_highway_station (id,ldbh,sfzbh,ldmc,sfzmc,x,y) values (?,?,?,?,?,?,?)");
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory.createDataStore(new File("E:\\广东省高速公路路网\\收费站点.shp").toURI().toURL());
            sds.setCharset(Charset.forName("GBK"));
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            SimpleFeatureIterator itertor = featureSource.getFeatures().features();
            while (itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                Point point = (Point) feature.getAttribute("the_geom");
                Long sfzbh = Convert.str2Long(feature.getAttribute("SFZBH").toString());
                Long ldbh = Convert.str2Long(feature.getAttribute("LDBH").toString());
                stmt.setLong(1, ldbh * 1000 + sfzbh);
                stmt.setLong(2, ldbh);
                stmt.setLong(3, sfzbh);
                stmt.setString(4, feature.getAttribute("LDMC").toString());
                stmt.setString(5, feature.getAttribute("SFZMC").toString());
                stmt.setDouble(6, point.getX());
                stmt.setDouble(7, point.getY());
                try {
                    stmt.execute();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println(point);
            }
            itertor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DBHELPER.closeStatement(stmt);
        DBHELPER.closeConnection(con);
    }

    private static void testShapeFile() throws Exception {
        Map<Long, Coordinate> id2Feature = new HashMap<Long, Coordinate>();
        Map<Long, Integer> id2Times = new HashMap<Long, Integer>();

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory.createDataStore(new File("E:\\广东省高速公路路网\\高速公路网.shp").toURI().toURL());
        sds.setCharset(Charset.forName("GBK"));
        SimpleFeatureSource featureSource = sds.getFeatureSource();
        SimpleFeatureIterator itertor = featureSource.getFeatures().features();
        while (itertor.hasNext()) {
            SimpleFeature feature = itertor.next();
            Geometry lineString = (MultiLineString) feature.getAttribute("the_geom");
            lineString = lineString.getGeometryN(0);
            Coordinate[] coords = lineString.getCoordinates();
            Coordinate startCoord = coords[0];
            Coordinate endCoord = coords[coords.length - 1];
            testCoord(startCoord, id2Feature, id2Times);
            testCoord(endCoord, id2Feature, id2Times);
        }
        writeTestResult2Shape(id2Feature, id2Times);
        itertor.close();
    }

    private static void writeTestResult2Shape(Map<Long, Coordinate> id2Feature, Map<Long, Integer> id2Times) throws Exception {
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.URLP.key, (new File("E:\\广东省高速公路路网\\路网交叉点测试.shp")).toURI().toURL());
        ShapefileDataStore ds = null;
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;

        ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
        // 定义图形信息和属性信息
        String typeStr = "the_geom:Point,times:int";
        SimpleFeatureType featureType = DataUtilities.createType("the_geom", typeStr);

        ds.createSchema(featureType);
        ds.setCharset(Charset.forName("GBK"));
        // 设置Writer
        writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
        // 写下一条
        for (Entry<Long, Integer> entry : id2Times.entrySet()) {
            Coordinate c = id2Feature.get(entry.getKey());
            Geometry geometry = new GeometryFactory().createPoint(c);
            SimpleFeature feature = writer.next();
            feature.setAttribute("the_geom", geometry);

            feature.setAttribute("times", entry.getValue());
        }
        writer.write();
        writer.close();
    }

    private static void testCoord(Coordinate coord, Map<Long, Coordinate> id2Feature, Map<Long, Integer> id2Times) {
        long x = (long) (coord.x * 1000);
        long y = (long) (coord.y * 1000);
        long key = x * 10000000 + y;
        if (id2Feature.containsKey(key))
            id2Times.put(key, id2Times.get(key) + 1);
        else {
            id2Times.put(key, 1);
            id2Feature.put(key, coord);
        }
    }
}
