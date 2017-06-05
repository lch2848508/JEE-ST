package com.estudio.define.webclient.portal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.minidev.json.JSONObject;

public class PortalGridExCommon extends PortalGridExControl {
    private Map<String, String> options = new HashMap<String, String>();

    public Map<String, String> getOptions() {
        return options;
    }

    /**
     * 
     * @param controlName
     * @param controlComment
     * @param controlType
     */
    public PortalGridExCommon(String controlName, String controlComment, int controlType) {
        super(controlName, controlComment, controlType);
    }

    /**
     * Ìí¼Ó²ÎÊý
     * 
     * @param name
     * @param value
     */
    public void addParams(String name, String value) {
        this.options.put(name, value);
    }

}
