package com.estudio.gis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.estudio.officeservice.ExcelService;
import com.estudio.officeservice.ExcelServiceConst;
import com.vividsolutions.jts.geom.Geometry;

public class WebGISExportUtils {

    /**
     * 创建Shape文件
     * 
     * @param fileName
     * @param type
     * @param schemaList
     * @param features
     * @param fileList
     * @return
     * @throws Exception
     */
    public static boolean createShapeFile(String fileName, String type, List<String> schemaList, List<Map<String, String>> features, List<String> fileList) throws Exception {
        if (features.isEmpty() || StringUtils.isEmpty(type)) {
            return false;
        }
        boolean result = false;
        com.vividsolutions.jts.io.WKTReader wkt = new com.vividsolutions.jts.io.WKTReader();
        // 创建shape文件对象
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(ShapefileDataStoreFactory.URLP.key, (new File(fileName + ".shp")).toURI().toURL());
        ShapefileDataStore ds = null;
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;
        try {
            ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
            // 定义图形信息和属性信息
            String typeStr = "the_geom:" + type;
            for (String fieldName : schemaList)
                typeStr += "," + fieldName + ":string";
            SimpleFeatureType featureType = DataUtilities.createType("the_geom", typeStr);

            ds.createSchema(featureType);
            ds.setCharset(Charset.forName("GBK"));
            // 设置Writer
            writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
            // 写下一条
            for (Map<String, String> record : features) {
                int totatBytes = 0;
                SimpleFeature feature = writer.next();
                Geometry geometry = wkt.read(record.get("shape"));
                feature.setAttribute("the_geom", geometry);
                for (String fieldName : schemaList) {
                    String value = record.get(fieldName);
                    if (StringUtils.isEmpty(value))
                        value = "";
                    byte[] bs = value.getBytes("gbk");
                    if (bs.length > 254) {
                        value = new String(bs, 0, 254, "gbk");
                        totatBytes += 254;
                    } else {
                        totatBytes += bs.length;
                    }
                    if (totatBytes > 4000)
                        continue;
                    feature.setAttribute(fieldName, value);
                }
            }
            writer.write();
            final String shapeFileBaseName = new File(fileName).getName();
            final File parentFile = new File(fileName).getParentFile();
            Collection<File> files = FileUtils.listFiles(parentFile, new IOFileFilter() {
                @Override
                public boolean accept(File arg0, String arg1) {
                    return StringUtils.contains(arg0.getName(), shapeFileBaseName);
                }

                @Override
                public boolean accept(File arg0) {
                    return StringUtils.contains(arg0.getName(), shapeFileBaseName);
                }
            }, null);
            for (File f : files)
                fileList.add(parentFile.getAbsolutePath() + File.separator + f.getName());

        } finally {
            if (writer != null)
                writer.close();
            if (ds != null)
                ds.dispose();
        }
        return result;
    }

    /**
     * 导出Excel文件
     * 
     * @param sheetList
     * @throws Exception
     */
    public static void createExcelFile(String excelFileName, List<JSONObject> sheetList) throws Exception {
        ExcelService excelService = ExcelService.getInstance();
        try {
            for (JSONObject sheetJson : sheetList) {
                final JSONArray columns = sheetJson.getJSONArray("columns");
                final JSONObject column2Comment = sheetJson.getJSONObject("columnComment");
                final JSONArray records = sheetJson.getJSONArray("records");

                excelService.addSheet(sheetJson.getString("name"));
                int firstRow = 0;
                final int firstCol = 0;

                for (int j = 0; j < columns.size(); j++)
                    excelService.setCellValue(firstRow, firstCol + j, column2Comment.getString(columns.getString(j)));
                excelService.setRowHeight(new int[] { firstRow }, 25);
                excelService.setRowsFont(new int[] { firstRow }, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, true, false);

                firstRow++;
                for (int j = 0; j < records.size(); j++) {
                    final JSONObject dataJSON = records.getJSONObject(j);
                    for (int m = 0; m < columns.size(); m++) {
                        String fieldName = columns.getString(m);
                        final String cellValue = dataJSON.get(fieldName) == null ? "" : dataJSON.getString(fieldName);
                        excelService.setCellValue(firstRow + j, firstCol + m, cellValue);
                    }
                    excelService.setRowHeight(new int[] { firstRow + j }, 25);
                    excelService.setRowsFont(new int[] { firstRow + j }, "微软雅黑", 9, ExcelServiceConst.COLOR_BLACK, false, false);
                }
                excelService.setBorderAll();
                excelService.autoColWitdh(null);
                excelService.freezePanes(1, 0, 1, 0);
            }

            excelService.save(excelFileName);
        } finally {
            excelService.dispose();
        }
    }

}
