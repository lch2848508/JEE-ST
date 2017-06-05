package com.estudio.intf.webclient.utils;

public class PictureProperty {
    private long width;
    private long height;
    private String fileName;
    private String saveFileName;
    private String thumbnailFileName;
    private String contentType;

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(final String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public String getThumbnailFileName() {
        return thumbnailFileName;
    }

    public void setThumbnailFileName(final String thumbnailFileName) {
        this.thumbnailFileName = thumbnailFileName;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(final long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(final long height) {
        this.height = height;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

}
