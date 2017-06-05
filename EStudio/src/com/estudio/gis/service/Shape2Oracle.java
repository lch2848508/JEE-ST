package com.estudio.gis.service;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import oracle.spatial.geometry.JGeometry;
import oracle.spatial.util.WKT;

import org.apache.commons.lang3.StringUtils;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import com.estudio.gis.GeometryUtils;
import com.estudio.utils.Convert;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

public class Shape2Oracle {
    public static boolean execute(Connection con, Statement exeStmt, long taskId, String caption, String tableName, String shapeFileName) throws Exception {
        boolean result = false;
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        ShapefileDataStore sourceDataSource = null;
        PreparedStatement iStmt = null;
        PreparedStatement iStmt2 = null;
        try {
            sourceDataSource = (ShapefileDataStore) dataStoreFactory.createDataStore(new File(shapeFileName).toURI().toURL());
            sourceDataSource.setCharset(Charset.forName("GBK"));
            SimpleFeatureType featureType = sourceDataSource.getSchema();
            List<AttributeDescriptor> attributeList = featureType.getAttributeDescriptors();
            List<String> fieldNameList = new ArrayList<String>();
            Map<String, String> fileName2DataType = new HashMap<String, String>();
            Map<String, String> fileName2SchemaName = new HashMap<String, String>();
            int index = 1;
            for (AttributeDescriptor attribute : attributeList) {
                String fieldName = attribute.getLocalName();
                if (StringUtils.equalsIgnoreCase(fieldName, "the_geom"))
                    continue;
                String dataType = attribute.getType().getBinding().getName();
                dataType = dataType.substring(dataType.lastIndexOf(".") + 1);
                fieldNameList.add(fieldName);
                fileName2DataType.put(fieldName, dataType);
                fileName2SchemaName.put(fieldName, "F" + index++);
            }

            exeStmt.execute("delete from webgis_dynamic_field where p_id=" + taskId);
            String createSQL = "create table " + tableName + " (";
            createSQL += "PRIMARY_ID integer,";
            String sql = "insert into webgis_dynamic_field(id,p_id,field_name,field_comment,schema_field_name,is_visible,is_query,is_enum,is_relate_parent,data_type,sortorder,is_primary) values (seq_for_j2ee_webgis.nextval," + taskId + ",'PRIMARY_ID','唯一标识号','PRIMARY_ID',1,0,0,0,'Number',seq_for_j2ee_webgis.nextval,1)";
            exeStmt.execute(sql);
            for (int i = 0; i < fieldNameList.size(); i++) {
                String fieldName = fieldNameList.get(i);
                String dataType = fileName2DataType.get(fieldName);
                if (StringUtils.equalsIgnoreCase(dataType, "String"))
                    createSQL += fileName2SchemaName.get(fieldName) + " varchar2(255),";
                else if (StringUtils.equalsIgnoreCase(dataType, "Integer") || StringUtils.equalsIgnoreCase(dataType, "Long") || StringUtils.equalsIgnoreCase(dataType, "Double") || StringUtils.equalsIgnoreCase(dataType, "float"))
                    createSQL += fileName2SchemaName.get(fieldName) + " number,";
                else if (StringUtils.equalsIgnoreCase(dataType, "Date"))
                    createSQL += fileName2SchemaName.get(fieldName) + " date,";
                else
                    createSQL += fileName2SchemaName.get(fieldName) + " varchar2(255),";
                exeStmt.execute("insert into webgis_dynamic_field(id,p_id,field_name,field_comment,schema_field_name,is_visible,is_query,is_enum,is_relate_parent,data_type,sortorder,is_primary) values (seq_for_j2ee_webgis.nextval," + taskId + ",'" + fieldName.toUpperCase() + "','" + fieldName.toUpperCase() + "','" + fileName2SchemaName.get(fieldName) + "',1,0,0,0,'" + dataType
                        + "',seq_for_j2ee_webgis.nextval,0)");
            }
            createSQL += "search_caption varchar2(4000),the_geom MDSYS.SDO_GEOMETRY)";
            exeStmt.execute(createSQL);

            exeStmt.execute("alter table " + tableName + " add constraint idx_" + tableName + "_id primary key (PRIMARY_ID)");
            exeStmt.execute("alter table " + tableName + " add record_id as ('" + tableName + "_'||PRIMARY_ID)");

            exeStmt.execute("comment on table " + tableName + " is '" + caption + "'");
            String viewName = "VIEW_" + tableName;
            exeStmt.execute("create view " + viewName + " as select a.*,b.geometry from " + tableName + " a,sys_ext_geometry b where a.record_id=b.record_id(+)");
            exeStmt.execute("comment on table " + viewName + " is '" + caption + "'");

            String insertSQL = "insert into " + tableName + "(PRIMARY_ID";
            for (int i = 0; i < fieldNameList.size(); i++)
                insertSQL += "," + fileName2SchemaName.get(fieldNameList.get(i));
            insertSQL += ",search_caption,the_geom) values (?";
            for (int i = 0; i < fieldNameList.size(); i++)
                insertSQL += ",?";
            insertSQL += ",?,?)";

            exeStmt.execute("delete from sys_ext_geometry where record_id like '" + tableName + "_%'");
            iStmt2 = con.prepareStatement("insert into sys_ext_geometry(record_id,geometry) values (?,?)");

            iStmt = con.prepareStatement(insertSQL);
            SimpleFeatureSource sourceFeatureSource = sourceDataSource.getFeatureSource();
            SimpleFeatureIterator itertor = sourceFeatureSource.getFeatures().features();
            index = 1;
            while (itertor.hasNext()) {
                try {
                    SimpleFeature feature = itertor.next();
                    long key = index++;
                    String recordKey = tableName + "_" + key;
                    iStmt.setLong(1, key);
                    String searchCaption = "";
                    for (int i = 0; i < fieldNameList.size(); i++) {
                        String fieldName = fieldNameList.get(i);
                        String dataType = fileName2DataType.get(fieldName);
                        Object attribValue = feature.getAttribute(fieldName);
                        if (attribValue == null) {
                            iStmt.setObject(i + 2, null);
                            continue;
                        }

                        if (StringUtils.equalsIgnoreCase(dataType, "String"))
                            iStmt.setString(i + 2, (String) attribValue);
                        else if (StringUtils.equalsIgnoreCase(dataType, "Long"))
                            iStmt.setLong(i + 2, (Long) attribValue);
                        else if (StringUtils.equalsIgnoreCase(dataType, "Double"))
                            iStmt.setDouble(i + 2, (Double) attribValue);
                        else if (StringUtils.equalsIgnoreCase(dataType, "Float"))
                            iStmt.setFloat(i + 2, (Float) attribValue);
                        else if (StringUtils.equalsIgnoreCase(dataType, "Integer"))
                            iStmt.setInt(i + 2, (Integer) attribValue);
                        else if (StringUtils.equalsIgnoreCase(dataType, "Date"))
                            iStmt.setDate(i + 2, Convert.date2SQLDate((Date) attribValue));
                        else if (StringUtils.equalsIgnoreCase(dataType, "Boolean"))
                            iStmt.setInt(i + 2, (Boolean) attribValue ? 1 : 0);
                        else
                            iStmt.setString(i + 2, String.valueOf(attribValue));

                        if (StringUtils.equalsIgnoreCase(dataType, "String"))
                            searchCaption += feature.getAttribute(fieldName);
                    }
                    searchCaption = fixStrSize(searchCaption, 4000);
                    iStmt.setString(2 + fieldNameList.size(), searchCaption);

                    Geometry geometry = (Geometry) feature.getAttribute("the_geom"); //MULTILINESTRING ((110.35270839079827 21.00685645815163, 110.34500155730832 21.005558436727473,
                    //获取geometry 单线
                    if (geometry.getNumGeometries() == 1)
                        geometry = geometry.getGeometryN(0);
                    
                    //将Geometry转化为字符串
                    String wkt = new WKTWriter().write(geometry);
                    //将字符串转化为byte 然后将byte转化为JGeometry
                    JGeometry geom = new WKT().toJGeometry(wkt.getBytes());
                    //存储到数据库中 需将JGeometry转化为Object
                    iStmt.setObject(3 + fieldNameList.size(), JGeometry.store(geom, con));
                    iStmt.execute();

                    iStmt2.setString(1, recordKey);
                    JSONArray array = new JSONArray();
                    //将字符串转化为GeoJSon
                    JSONObject json = GeometryUtils.wkt2ArcGISGeometryJSON(wkt);
                    array.add(json);
                    iStmt2.setBytes(2, Convert.str2Bytes(array.toJSONString()));
                    iStmt2.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            itertor.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sourceDataSource != null)
                sourceDataSource.dispose();
            if (iStmt != null)
                iStmt.close();
            if (iStmt2 != null)
                iStmt2.close();
        }
        return result;
    }

    private static String fixStrSize(String cellStr, int size) {
        if (StringUtils.isEmpty(cellStr))
            return "";
        int length = cellStr.getBytes(Charset.forName("GBK")).length;
        while (length > size) {
            cellStr = cellStr.substring(0, cellStr.length() - 2);
            length = cellStr.getBytes(Charset.forName("GBK")).length;
        }
        return cellStr;
    }
}
