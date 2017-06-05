package com.estudio.define.webclient.portal;

public class PortalGridExLayout {
    private String layerType = "1C";

    // AÇøÓò
    private String aControl = "";

    // BÇøÓò
    private String bControl = "";

    // CÇøÓò
    private String cControl = "";

    private boolean toolbarA;
    private boolean toolbarB;
    private boolean toolbarC;
    
    private boolean canHiddenA;
    private boolean canHiddenB;
    private boolean canHiddenC;

    
    public boolean isCanHiddenA() {
        return canHiddenA;
    }

    public void setCanHiddenA(boolean canHiddenA) {
        this.canHiddenA = canHiddenA;
    }

    public boolean isCanHiddenB() {
        return canHiddenB;
    }

    public void setCanHiddenB(boolean canHiddenB) {
        this.canHiddenB = canHiddenB;
    }

    public boolean isCanHiddenC() {
        return canHiddenC;
    }

    public void setCanHiddenC(boolean canHiddenC) {
        this.canHiddenC = canHiddenC;
    }

    private int SplitL;
    private int SplitR;
    private int SplitT;
    private int SplitB;

    public boolean isToolbarA() {
        return toolbarA;
    }

    public void setToolbarA(boolean toolbarA) {
        this.toolbarA = toolbarA;
    }

    public boolean isToolbarB() {
        return toolbarB;
    }

    public void setToolbarB(boolean toolbarB) {
        this.toolbarB = toolbarB;
    }

    public boolean isToolbarC() {
        return toolbarC;
    }

    public void setToolbarC(boolean toolbarC) {
        this.toolbarC = toolbarC;
    }

    public int getSplitL() {
        return SplitL;
    }

    public void setSplitL(int splitL) {
        SplitL = splitL;
    }

    public int getSplitR() {
        return SplitR;
    }

    public void setSplitR(int splitR) {
        SplitR = splitR;
    }

    public int getSplitT() {
        return SplitT;
    }

    public void setSplitT(int splitT) {
        SplitT = splitT;
    }

    public int getSplitB() {
        return SplitB;
    }

    public void setSplitB(int splitB) {
        SplitB = splitB;
    }

    public String getLayerType() {
        return layerType;
    }

    public void setLayerType(String layerType) {
        this.layerType = layerType;
    }

    public String getaControl() {
        return aControl;
    }

    public void setaControl(String aControl) {
        this.aControl = aControl;
    }

    public String getbControl() {
        return bControl;
    }

    public void setbControl(String bControl) {
        this.bControl = bControl;
    }

    public String getcControl() {
        return cControl;
    }

    public void setcControl(String cControl) {
        this.cControl = cControl;
    }

}
