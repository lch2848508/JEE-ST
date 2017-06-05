package com.estudio.define.webclient.portal;

public class PortalGridExTree extends PortalGridExSQLBase {

    private boolean singleLevel = false;
    private boolean asyncLoad = false;

    /**
     * ¹¹Ôìº¯Êý
     * 
     * @param controlName
     * @param controlComment
     * @param controlType
     */
    public PortalGridExTree(String controlName, String controlComment, int controlType) {
        super(controlName, controlComment, controlType);
    }

    public boolean isSingleLevel() {
        return singleLevel;
    }

    public void setSingleLevel(boolean singleLevel) {
        this.singleLevel = singleLevel;
    }

    public boolean isAsyncLoad() {
        return asyncLoad;
    }

    public void setAsyncLoad(boolean asyncLoad) {
        this.asyncLoad = asyncLoad;
    }

}
