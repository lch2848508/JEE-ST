package com.estudio.gis;

import java.io.Serializable;

public class WebGISProxyItem implements Serializable {
    private static final long serialVersionUID = -5359360933739558529L;
    public boolean isByte = false;
    public boolean isImage = false;
    public boolean isError = false;
    public String contentType;
    public byte[] content = null;
}
