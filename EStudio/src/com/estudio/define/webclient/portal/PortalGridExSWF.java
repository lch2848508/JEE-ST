package com.estudio.define.webclient.portal;

public class PortalGridExSWF extends PortalGridExControl {

    private String url;
    private boolean module = false;

    public PortalGridExSWF(String controlName, String controlComment, int controlType, String url, boolean isModule) {
        super(controlName, controlComment, controlType);
        this.url = url;
        this.module = isModule;
    }

    public String getUrl() {
        return url;
    }

    public boolean isModule() {
        return module;
    }

}
