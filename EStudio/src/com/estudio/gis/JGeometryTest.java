package com.estudio.gis;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.estudio.utils.ExceptionUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class JGeometryTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        write("e:\\1234.shp");
    }

    public static void write(String filepath) {
        try {
            // 创建shape文件对象
            File file = new File(filepath);
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
            // 定义图形信息和属性信息
            final SimpleFeatureType TYPE = DataUtilities.createType("the_geom", "the_geom:Point," + "NAME:String," + "INFO:String");

            ds.createSchema(TYPE);
            ds.setCharset(Charset.forName("GBK"));
            // 设置Writer
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
            // 写下一条
            SimpleFeature feature = writer.next();
            feature.setAttribute("the_geom", new GeometryFactory().createPoint(new Coordinate(116.123, 39.345)));
            feature.setAttribute("NAME", 1234567890l);
            feature.setAttribute("INFO", "某兴趣点1");
            feature = writer.next();
            feature.setAttribute("the_geom", new GeometryFactory().createPoint(new Coordinate(116.456, 39.678)));
            feature.setAttribute("NAME", 1234567891l);
            feature.setAttribute("INFO", "某兴趣点2");
            writer.write();
            writer.close();
            ds.dispose();

        } catch (final Exception e) {
            ExceptionUtils.printExceptionTrace(e);

        }
    }

}
