package com.estudio.gis;

import java.util.List;

import net.minidev.json.JSONObject;

public class WebGISExportTaskItem {
    public List<Long> layerIds = null;
    public JSONObject wheres = null;
    public String email = "";
    public int type = 0;
    public String resultFileName = "";
    public long userId;
    public int wkid;
}
