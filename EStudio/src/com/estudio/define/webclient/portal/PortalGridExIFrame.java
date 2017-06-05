package com.estudio.define.webclient.portal;

public class PortalGridExIFrame extends PortalGridExControl {

    private String url;
    public PortalGridExIFrame(String controlName, String controlComment, int controlType, String url) {
        super(controlName, controlComment, controlType);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
