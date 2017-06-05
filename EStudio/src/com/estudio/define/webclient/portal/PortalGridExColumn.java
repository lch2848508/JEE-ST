package com.estudio.define.webclient.portal;

public class PortalGridExColumn extends PortalGridColumn {
    private String editorClass;
    private String editorProperty;
    private String urlFieldName;
    private boolean editable;

    public String getUrlFieldName() {
        return urlFieldName;
    }

    public void setUrlFieldName(String urlFieldName) {
        this.urlFieldName = urlFieldName;
    }

    public boolean isEditable() {
        return editable;
    }

    /**
     * ¹¹Ôìº¯Êý
     * 
     * @param fieldName
     * @param fieldLabel
     * @param width
     * @param isFixedWidth
     * @param alignment
     * @param style
     * @param editColumn
     * @param editorClass
     * @param editorProperty
     * @param editable
     */
    public PortalGridExColumn(String fieldName, String fieldLabel, String width, boolean isFixedWidth, String alignment, String style, boolean editColumn, String editorClass, String editorProperty, boolean editable) {
        super(fieldName, fieldLabel, width, isFixedWidth, alignment, style, editColumn);
        this.editorClass = editorClass;
        this.editorProperty = editorProperty;
        this.editable = editable;
    }

    public String getEditorClass() {
        return editorClass;
    }

    public String getEditorProperty() {
        return editorProperty;
    }

}
