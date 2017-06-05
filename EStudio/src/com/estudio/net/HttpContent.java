package com.estudio.net;

public class HttpContent {
    private String contentType;
    private byte[] content;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public HttpContent(String contentType, byte[] content) {
        super();
        this.contentType = contentType;
        this.content = content;
    }

}
