package com.estudio.gis.oracle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.json.JSONArray;

public class WebGISLayerFields implements Serializable {
    private static final long serialVersionUID = 2030106160767969701L;
    public long version;
    public String captionTemplate = "";
    public String objectFieldName = "";
    public List<String> fieldList = new ArrayList<String>();
    public List<String> fieldComment = new ArrayList<String>();
    public List<String> schemaFieldList = new ArrayList<String>();
    public Map<String, String> schemaFieldName2LayerFieldName = new HashMap<String, String>();
    public JSONArray fieldJsonArray = new JSONArray();
    public Map<String, Integer> schemaFieldName2DataType = new HashMap<String, Integer>();
    public List<String> captionFieldList = new ArrayList<String>();
    public Map<String, Boolean> schemaFieldName2IsQuery = new HashMap<String, Boolean>();
    public Map<String, String> captionFieldMap = new HashMap<String, String>();
    public Map<String,String>schemaField2Comment = new HashMap<String, String>();
}
