package com.estudio.gis;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

public class WebGISDistrictItem {
    public String name;
    public String code;
    public long id;
    public Geometry geometry = null;
    public List<WebGISDistrictItem> children = null;
}
