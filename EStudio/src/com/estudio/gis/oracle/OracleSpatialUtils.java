package com.estudio.gis.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.minidev.json.JSONObject;
import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.WKB;
import oracle.spatial.util.WKT;

import org.apache.commons.lang3.StringUtils;

import com.estudio.context.RuntimeContext;
import com.estudio.gis.GeometryUtils;
import com.estudio.intf.db.IDBHelper;
import com.estudio.utils.Convert;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

public class OracleSpatialUtils {

    private static IDBHelper DBHelper = RuntimeContext.getDbHelper();

    /**
     * JGeometry转KWT
     * 
     * @param geometry
     * @return
     * @throws Exception
     */
    public static String geom2wkt(JGeometry geometry) throws Exception {
        return new String(new WKT().fromJGeometry(geometry));
    }

    /**
     * WKT转JGeometry
     * 
     * @param wkt
     * @return
     * @throws Exception
     */
    public static JGeometry wkt2geom(String wkt) throws Exception {
        JGeometry geometry = null;
        try {
            geometry = new WKT().toJGeometry(wkt.getBytes());
        } catch (Exception e) {
        }
        if (geometry == null) {
            wkt = new WKTWriter().write(new WKTReader().read(wkt));
            geometry = new WKT().toJGeometry(wkt.getBytes());
        }
        return geometry;
    }

    /**
     * WKT转为JGeometry
     * 
     * @param bytes
     * @return
     * @throws Exception
     */
    public static JGeometry wkt2geom(byte[] bytes) throws Exception {
        return wkt2geom(new String(bytes));
    }

    /**
     * 缓冲区
     * 
     * @param con
     * @param geometry
     * @param buf
     * @return
     * @throws Exception
     */
    public static JGeometry buffer(Connection con, JGeometry geometry, double buf) throws Exception {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select sdo_geom.sdo_buffer(?, ?, 0.5, 'unit= M') from dual");
            stmt.setObject(1, JGeometry.store(geometry, DBHelper.getNaviteConnection(con)));
            stmt.setDouble(2, buf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                geometry = JGeometry.load(rs.getBytes(1));
        } finally {
            DBHelper.closeStatement(stmt);
        }
        return geometry;
    }

    /**
     * 坐标转换
     * 
     * @param con
     * @param geometry
     * @param toWKID
     * @return
     * @throws Exception
     */
    public static JGeometry transCroods(Connection con, JGeometry geometry, int toWKID) throws Exception {
        if (geometry.getSRID() != toWKID) {
            PreparedStatement stmt = null;
            try {
                stmt = con.prepareStatement("select sdo_cs.transform(?,2," + toWKID + ") from dual");
                stmt.setObject(1, JGeometry.store(geometry, DBHelper.getNaviteConnection(con)));
                ResultSet rs = stmt.executeQuery();
                if (rs.next())
                    geometry = JGeometry.load(rs.getBytes(1));
            } finally {
                DBHelper.closeStatement(stmt);
            }

        }
        return geometry;
    }

    /**
     * 求周长
     * 
     * @param con
     * @param geometry
     * @return
     * @throws Exception
     */
    public static double length(Connection con, JGeometry geometry) throws Exception {
        double result = 0;
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select sdo_geom.sdo_length(?,2) from dual");
            stmt.setObject(1, JGeometry.store(geometry, DBHelper.getNaviteConnection(con)));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            result = rs.getDouble(1);
        } finally {
            DBHelper.closeStatement(stmt);
        }
        return result;

    }

    /**
     * 求面积
     * 
     * @param con
     * @param geometry
     * @return
     * @throws Exception
     */
    public static double[] lengthAndArea(Connection con, JGeometry geometry) throws Exception {
        double[] result = new double[] { 0, 0 };
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select sdo_geom.sdo_length(?,2),sdo_geom.sdo_area(?,2) from dual");
            stmt.setObject(1, JGeometry.store(geometry, DBHelper.getNaviteConnection(con)));
            stmt.setObject(2, JGeometry.store(geometry, DBHelper.getNaviteConnection(con)));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            result[0] = rs.getDouble(1);
            result[1] = rs.getDouble(2);
        } finally {
            DBHelper.closeStatement(stmt);
        }
        return result;
    }

    /**
     * 抽吸算法
     * 
     * @param con
     * @param geometry
     * @param simpValue
     * @return
     * @throws Exception
     */
    public static JGeometry simply(Connection con, JGeometry geometry, double simpValue) throws Exception {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select sdo_util.simplify(?," + simpValue + ") from dual");
            stmt.setObject(1, JGeometry.store(geometry, DBHelper.getNaviteConnection(con)));
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                geometry = JGeometry.load(rs.getBytes(1));
        } catch (Exception e) {

        } finally {
            DBHelper.closeStatement(stmt);
        }
        return geometry;
    }

    /**
     * 获取中心点坐标
     * 
     * @param geometry
     * @return
     * @throws Exception
     */
    public static double[] center(JGeometry geometry) throws Exception {
        Geometry g = new WKTReader().read(new String(new WKT().fromJGeometry(geometry)));
        Point p = g.getCentroid();
        return new double[] { p.getCoordinate().x, p.getCoordinate().y };
    }

    /**
     * 判断几何体是否相交
     * 
     * @param geometry1
     * @param geometry2
     * @return
     * @throws Exception
     */
    public static boolean isInteract(JGeometry geometry1, JGeometry geometry2) throws Exception {
        return toGeometry(geometry1).intersects(toGeometry(geometry2));
    }

    /**
     * 判断几何体是否相交
     * 
     * @param geometry1
     * @param geometry2
     * @return
     * @throws Exception
     */
    public static boolean isInteract(Geometry geometry1, Geometry geometry2) throws Exception {
        return geometry1.intersects(geometry2) || geometry1.contains(geometry2) || geometry2.contains(geometry1);
    }

    /**
     * 判断几何体是否相交
     * 
     * @param geometry1
     * @param geometry2
     * @return
     * @throws Exception
     */
    public static boolean isInteract(Geometry geometry1, JGeometry geometry2) throws Exception {
        Geometry g = toGeometry(geometry2);
        return geometry1.intersects(g) || geometry1.contains(g) || g.contains(geometry1);
    }

    /**
     * 
     * @param geomery
     * @return
     * @throws
     * @throws ParseException
     */
    public static Geometry toGeometry(JGeometry geomery) throws Exception {
        return new WKBReader().read(new WKB().fromJGeometry(geomery));
    }

    public static JSONObject JGeometry2ArcGISJSON(JSONObject json, String geometryType, double resolution, int wkid) throws Exception {
        JGeometry result = null;
        if (StringUtils.equals(geometryType, "esriGeometryPoint")) {
            double x = Convert.str2Double(json.getString("x"));
            double y = Convert.str2Double(json.getString("y"));
            if (resolution != 0) {
                double r = resolution * 3;
                result = JGeometry.createLinearPolygon(new double[] { x - r, y - r, x - r, y + r, x + r, y + r, x + r, y - r, x - r, y - r }, 2, 0);
            } else {
                result = JGeometry.createPoint(new double[] { x, y }, 2, 0);
            }
        } else if (StringUtils.equals("esriGeometryEnvelope", geometryType)) {
            double xmin = json.getDouble("xmin");
            double xmax = json.getDouble("xmax");
            double ymin = json.getDouble("ymin");
            double ymax = json.getDouble("ymax");
            result = JGeometry.createLinearPolygon(new double[] { xmin, ymin, xmin, ymax, xmax, ymax, xmax, ymin, xmin, ymin }, 2, 0);
        } else
            result = GeometryUtils.arcGISGeometry2OracleSpatial(geometryType, json, wkid);
        if (result != null)
            result.setSRID(wkid);
        return GeometryUtils.wkt2ArcGISGeometryJSON(new String(new WKB().fromJGeometry(result)));
    }

    /**
     * 
     * @param json
     * @param geometryType
     * @param resolution
     * @return
     * @throws Exception
     */
    public static JGeometry arcGISGeometryJson2JGeometry(JSONObject json, String geometryType, double resolution, int wkid) throws Exception {
        JGeometry result = null;
        if (StringUtils.equals(geometryType, "esriGeometryPoint")) {
            double x = Convert.str2Double(json.getString("x"));
            double y = Convert.str2Double(json.getString("y"));
            if (resolution != 0) {
                double r = resolution * 3;
                result = JGeometry.createLinearPolygon(new double[] { x - r, y - r, x - r, y + r, x + r, y + r, x + r, y - r, x - r, y - r }, 2, 0);
            } else {
                result = JGeometry.createPoint(new double[] { x, y }, 2, 0);
            }
        } else if (StringUtils.equals("esriGeometryEnvelope", geometryType)) {
            double xmin = json.getDouble("xmin");
            double xmax = json.getDouble("xmax");
            double ymin = json.getDouble("ymin");
            double ymax = json.getDouble("ymax");
            result = JGeometry.createLinearPolygon(new double[] { xmin, ymin, xmin, ymax, xmax, ymax, xmax, ymin, xmin, ymin }, 2, 0);
        } else
            result = GeometryUtils.arcGISGeometry2OracleSpatial(geometryType, json, wkid);
        if (result != null)
            result.setSRID(wkid);
        return result;
    }

    public static Geometry interact(Geometry g1, Geometry g2) {
        return g1.intersection(g2);
    }

    public static JGeometry toJGeometry(Geometry geometry) throws Exception {
        return wkt2geom(new WKTWriter().write(geometry));
    }

    public static String toArcGISJson(JGeometry geometry) throws Exception {
        return GeometryUtils.wkt2ArcGISGeometryJSON(new String(new WKT().fromJGeometry(geometry))).toJSONString();
    }
}
