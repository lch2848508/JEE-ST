package com.estudio.define.webclient.portal;

public class PortalGridExControl {

    public static final int CONTROL_GRID = 0;
    public static final int CONTROL_TREE = 1;
    public static final int CONTROL_FORM = 2;
    public static final int CONTROL_SWF = 3;
    public static final int CONTROL_IFRAME = 4;
    public static final int CONTROL_RICHVIEW = 5;
    public static final int CONTROL_DIAGRAM = 6;
    public static final int CONTROL_CALENDAR = 7;
    public static final int CONTROL_GISMAP = 8;
    public static final int CONTROL_PROPERTY = 9;
    public static final int CONTROL_PICTURELIST = 10;
    public static final int CONTROL_FILEMANAGER = 11;
    public static final int CONTROL_PAGECONTROL = 12;

    private String controlName;
    private String controlComment;
    private int controlType;

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }

    public String getControlComment() {
        return controlComment;
    }

    public void setControlComment(String controlComment) {
        this.controlComment = controlComment;
    }

    public int getControlType() {
        return controlType;
    }

    public void setControlType(int controlType) {
        this.controlType = controlType;
    }

    /**
     * ¹¹Ôìº¯Êý
     * 
     * @param controlName
     * @param controlComment
     * @param controlType
     */
    public PortalGridExControl(String controlName, String controlComment, int controlType) {
        super();
        this.controlName = controlName;
        this.controlComment = controlComment;
        this.controlType = controlType;
    }

}
