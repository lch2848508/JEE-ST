package com.estudio.gis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import oracle.spatial.geometry.JGeometry;

import org.apache.commons.lang3.StringUtils;

import com.estudio.gis.oracle.OracleSpatialUtils;
import com.estudio.utils.Convert;

public class GeometryUtils {

    private static final int SIZE_32K = 32 * 1024;

    /**
     * 
     * @param geometryType
     * @param geometryJson
     * @param wkid
     * @return
     * @throws Exception
     */
    public static JGeometry arcGISGeometry2OracleSpatial(String geometryType, JSONObject geometryJson, int wkid) throws Exception {
        JGeometry result = null;
        if (StringUtils.equals("esriGeometryPolyline", geometryType)) {
            JSONArray pathArray = geometryJson.getJSONArray("paths");
            if (pathArray.size() != 0) {
                List<double[]> pointsList = generalGeometryPoints(pathArray);
                if (pointsList.size() == 0)
                    result = JGeometry.createLinearLineString(pointsList.get(0), 2, 0);
                else result = JGeometry.createLinearMultiLineString(pointsList.toArray(), 2, 0);
            }
        } else if (StringUtils.equals("esriGeometryPolygon", geometryType)) {
            JSONArray ringArray = geometryJson.getJSONArray("rings");
            if (ringArray.size() != 0) {
                List<List<double[]>> pointsList = generalPointsList(ringArray);
                List<double[]> points4Jeometry = generalGeometryPoints(ringArray);
                if (pointsList.size() == 1 || OnlyOneExteriorRing(pointsList))
                    result = JGeometry.createLinearPolygon(points4Jeometry.toArray(), 2, 0);
                else {
                    StringBuilder sb = new StringBuilder(SIZE_32K);
                    AppendMultiPolygonTaggedText(pointsList, sb);
                    result = OracleSpatialUtils.wkt2geom(sb.toString());
                }
            }

        } else if (StringUtils.equals("esriGeometryPoint", geometryType)) {
            result = JGeometry.createPoint(new double[] { Convert.obj2Double(geometryJson.get("x"), 0), Convert.obj2Double(geometryJson.get("y"), 0) }, 2, 0);
        } else if (StringUtils.equalsIgnoreCase("esriGeometryEnvelope", geometryType)) {
            double xmin = geometryJson.getDouble("xmin");
            double ymin = geometryJson.getDouble("ymin");
            double xmax = geometryJson.getDouble("xmax");
            double ymax = geometryJson.getDouble("ymax");
            result = JGeometry.createLinearPolygon(new double[] { xmin, ymin, xmin, ymax, xmax, ymax, xmax, ymin, xmin, ymin }, 2, 0);
        }
        if (result != null)
            result.setSRID(wkid);
        return result;
    }

    /**
     * 
     * @param geometryType
     * @param geometryJson
     * @return
     */
    public static String arcGISGeometry2WKT(String geometryType, JSONObject geometryJson) {
        StringBuilder sb = new StringBuilder(SIZE_32K);
        if (StringUtils.equals("esriGeometryPolyline", geometryType)) {
            JSONArray pathArray = geometryJson.getJSONArray("paths");
            List<List<double[]>> pointsList = generalPointsList(pathArray);
            if (getCCWPointListCount(pointsList) == 1)
                AppendLineStringTaggedText(pointsList, sb);
            else AppendMultiLineStringTaggedText(pointsList, sb);
        } else if (StringUtils.equals("esriGeometryPolygon", geometryType)) {
            JSONArray ringArray = geometryJson.getJSONArray("rings");
            List<List<double[]>> pointsList = generalPointsList(ringArray);
            if (OnlyOneExteriorRing(pointsList))
                AppendPolygonTaggedText(pointsList, sb);
            else AppendMultiPolygonTaggedText(pointsList, sb);

        } else if (StringUtils.equals("esriGeometryPoint", geometryType)) {
            sb.append("POINT (").append(geometryJson.getString("x")).append(" ").append(geometryJson.getString("y")).append(")");
        }
        return sb.toString();
    }

    /**
     * wktJSON转化为ArcGIS JSON对象
     * 
     * @param wktStr
     * @return
     */
    public static JSONObject wkt2ArcGISGeometryJSON(final String wktStr) {
        JSONObject json = new JSONObject();

        String tempStr = wktStr;
        if (StringUtils.startsWith(wktStr, "GEOMETRYCOLLECTION"))
            tempStr = StringUtils.substring(wktStr, "GEOMETRYCOLLECTION (".length(), wktStr.length() - "GEOMETRYCOLLECTION (".length() - 1);
        while (StringUtils.contains(tempStr, "(("))
            tempStr = StringUtils.replaceEach(tempStr, new String[] { "((", "))" }, new String[] { "(", ")" });
        String[] pointStr = StringUtils.substringsBetween(tempStr, "(", ")");

        List<List<JSONArray>> points = new ArrayList<List<JSONArray>>();
        for (int i = 0; i < pointStr.length; i++) {
            List<JSONArray> path = new ArrayList<JSONArray>();
            String[] ps = pointStr[i].split(",");
            for (String p : ps) {
                String[] xy = StringUtils.trim(p).split(" ");
                JSONArray point = new JSONArray();
                point.add(Convert.str2Double(xy[0]));
                point.add(Convert.str2Double(xy[1]));
                path.add(point);
            }
            points.add(path);
        }

        if (StringUtils.startsWith(wktStr, "POINT")) {
            json.put("x", Convert.str2Double(points.get(0).get(0).getString(0)));
            json.put("y", Convert.str2Double(points.get(0).get(0).getString(1)));
        } else if (StringUtils.contains(wktStr, "POLYGON"))
            json.put("rings", points);
        else json.put("paths", points);
        return json;
    }

    /**
     * 生成多面体
     * 
     * @param pointsList
     * @param sb
     */
    private static void AppendMultiPolygonTaggedText(List<List<double[]>> pointsList, StringBuilder sb) {
        sb.append("MULTIPOLYGON ");
        sb.append("(");

        boolean outerRing = true;
        if (pointsList.size() > 0) {
            outerRing = IsCCW(pointsList.get(0));
        }
        for (int i = 0; i < pointsList.size(); i++) {
            if (i > 0)
                sb.append(", ");

            List<List<double[]>> singlePolygon = new ArrayList<List<double[]>>();
            singlePolygon.add(pointsList.get(i));
            // ring

            // Add any interior rings
            for (int j = i + 1; j < pointsList.size(); j++) {
                // It is an interior ring if the clockwise direction is opposite
                // of the first ring
                if (IsCCW(pointsList.get(j)) == outerRing)
                    break;
                singlePolygon.add(pointsList.get(j));
                i++;
            }
            AppendPolygonText(singlePolygon, sb);
        }
        sb.append(")");
    }

    /**
     * 生成单面体
     * 
     * @param pointsList
     * @param sb
     */
    private static void AppendPolygonTaggedText(List<List<double[]>> pointsList, StringBuilder sb) {
        sb.append("POLYGON ");
        AppendPolygonText(pointsList, sb);
    }

    /**
     * 
     * 
     * @param pointsList
     * @param sb
     */
    private static void AppendPolygonText(List<List<double[]>> pointsList, StringBuilder sb) {
        sb.append("(");
        AppendLineStringText(pointsList.get(0), sb); // ExteriorRing
        for (int i = 1; i < pointsList.size(); i++) {
            sb.append(", ");
            AppendLineStringText(pointsList.get(i), sb); // InteriorRings
        }
        sb.append(")");
    }

    /**
     * 
     * @param pointsList
     * @return
     */
    private static boolean OnlyOneExteriorRing(List<List<double[]>> pointsList) {
        boolean exteriorCCW = false;
        if (pointsList.size() > 0) {
            exteriorCCW = IsCCW(pointsList.get(0));
        }
        int count = 0;
        for (List<double[]> ring : pointsList) {
            if (IsCCW(ring) == exteriorCCW)
                count++;
        }
        return count == 1;
    }

    /**
     * 
     * @param pointsList
     * @param sb
     */
    private static void AppendMultiLineStringTaggedText(List<List<double[]>> pointsList, StringBuilder sb) {
        sb.append("MULTILINESTRING ");
        sb.append("(");
        for (int i = 0; i < pointsList.size(); i++) {
            if (i > 0)
                sb.append(", ");
            AppendLineStringText(pointsList.get(i), sb);
        }
        sb.append(")");
    }

    /**
     * 
     * @param paths
     * @param sb
     */
    private static void AppendLineStringTaggedText(List<List<double[]>> paths, StringBuilder sb) {
        sb.append("LINESTRING ");
        AppendLineStringText(paths.get(0), sb);
    }

    /**
     * 
     * @param list
     * @param sb
     */
    private static void AppendLineStringText(List<double[]> list, StringBuilder sb) {
        sb.append("(");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0)
                sb.append(", ");
            AppendCoordinate(list.get(i), sb);
        }
        sb.append(")");
    }

    /**
     * 
     * @param mapPoint
     * @param sb
     */
    private static void AppendCoordinate(double[] mapPoint, StringBuilder sb) {
        sb.append(new BigDecimal(mapPoint[0]).toPlainString()).append(" ").append(new BigDecimal(mapPoint[1]).toPlainString());
    }

    /**
     * 获取几个逆时针内环
     * 
     * @param pointsList
     * @return
     */
    private static int getCCWPointListCount(List<List<double[]>> pointsList) {
        int count = 0;
        for (List<double[]> list : pointsList) {
            if (IsCCW(list))
                count++;
        }
        return count;
    }

    /**
     * 生成点列表
     * 
     * @param pathArray
     * @return
     */
    private static List<List<double[]>> generalPointsList(JSONArray pathArray) {
        List<List<double[]>> result = new ArrayList<List<double[]>>();
        for (int i = 0; i < pathArray.size(); i++) {
            List<double[]> points = new ArrayList<double[]>();
            JSONArray paths = pathArray.getJSONArray(i);
            for (int j = 0; j < paths.size(); j++) {
                JSONArray ps = paths.getJSONArray(j);
                Object x = ps.get(0);
                Object y = ps.get(1);
                double[] point = new double[] { Convert.obj2Double(x, 0), Convert.obj2Double(y, 0) };
                points.add(point);
            }
            result.add(points);
        }
        return result;
    }

    /**
     * 
     * @param pathArray
     * @return
     */
    private static List<double[]> generalGeometryPoints(JSONArray pathArray) {
        List<double[]> result = new ArrayList<double[]>();
        for (int i = 0; i < pathArray.size(); i++) {
            JSONArray paths = pathArray.getJSONArray(i);
            double[] points = new double[paths.size() * 2];
            for (int j = 0; j < paths.size(); j++) {
                JSONArray ps = paths.getJSONArray(j);
                points[2 * j] = Convert.obj2Double(ps.get(0), 0);
                points[2 * j + 1] = Convert.obj2Double(ps.get(1), 0);
            }
            result.add(points);
        }
        return result;
    }

    /**
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private static double Distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0));
    }

    /**
     * 
     * @param ring
     * @return
     */
    private static boolean IsCCW(List<double[]> ring) {
        double[] PrevPoint, NextPoint, p;
        double[] hip = ring.get(0);
        int hii = 0;
        for (int i = 1; i < ring.size(); i++) {
            p = ring.get(i);
            if (p[1] > hip[1]) {
                hip = p;
                hii = i;
            }
        }
        int iPrev = hii - 1;
        if (iPrev < 0)
            iPrev = ring.size() - 2;

        int iNext = hii + 1;
        if (iNext >= ring.size())
            iNext = 1;
        PrevPoint = ring.get(iPrev);
        NextPoint = ring.get(iNext);

        double prev2X = PrevPoint[0] - hip[0];
        double prev2Y = PrevPoint[1] - hip[1];
        double next2X = NextPoint[0] - hip[0];
        double next2Y = NextPoint[1] - hip[1];

        double disc = next2X * prev2Y - next2Y * prev2X;
        if (disc == 0.0)
            return (PrevPoint[0] > NextPoint[0]);
        return (disc > 0.0);
    }


}
