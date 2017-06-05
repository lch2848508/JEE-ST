package com.estudio.gis;

public class WebGISSpatialReferenceConfig {
    public int wkid;
    public double xMin;
    public double xMax;
    public double yMin;
    public double yMax;
    public double idxValue;
    public double simpValue;
    public int version;
    public String oraSpatialTableName;
    public int maxRecordPerTime;
    public String spatialIndexName;
    public String spatialSimpIndexName;
    public int geometrySrid = 4326;

    public WebGISSpatialReferenceConfig(int wkid, int srid, double xMin, double xMax, double yMin, double yMax, double idxValue, double simpValue, int version, String oraSpatialTableName, int maxRecordPerTime) {
        this.wkid = wkid;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.idxValue = idxValue;
        this.simpValue = simpValue;
        this.version = version;
        this.oraSpatialTableName = oraSpatialTableName;
        this.maxRecordPerTime = maxRecordPerTime;
        this.geometrySrid = srid;
    }
}