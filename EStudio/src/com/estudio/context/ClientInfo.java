package com.estudio.context;

public class ClientInfo {
    private String ipAddress;
    private String macName;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacName() {
        return macName;
    }

    public void setMacName(final String macName) {
        this.macName = macName;
    }

    public ClientInfo(final String ipAddress, final String macName) {
        super();
        this.ipAddress = ipAddress;
        this.macName = macName;
    }
}
