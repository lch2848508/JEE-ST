package com.estudio.define.webclient.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortalGridExForm extends PortalGridExControl {

    private List<BindForm> bindForms = new ArrayList<BindForm>();
    private Map<String, String> options = new HashMap<String, String>();

    public Map<String, String> getOptions() {
        return options;
    }

    public PortalGridExForm(String controlName, String controlComment, int controlType) {
        super(controlName, controlComment, controlType);
    }

    public List<BindForm> getBindForms() {
        return bindForms;
    }

}
