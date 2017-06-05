package com.estudio.context;

public final class RuntimeConfig {

    private int version = 0;
    private boolean release;
    private boolean enabledUserRegister;
    private boolean enableForgetPassword;
    private boolean JMSEnabled = false;
    private boolean ICQEnabled = false;
    private boolean WEBGISEnabled = false;
    private String cityName="";

    public boolean isWEBGISEnabled() {
        return WEBGISEnabled;
    }

    public void setWEBGISEnabled(boolean wEBGISEnabled) {
        WEBGISEnabled = wEBGISEnabled;
    }

    public boolean isJMSEnabled() {
        return JMSEnabled;
    }

    public void setJMSEnabled(boolean jMSEnabled) {
        JMSEnabled = jMSEnabled;
    }

    public boolean isICQEnabled() {
        return ICQEnabled;
    }

    public void setICQEnabled(boolean iCQEnabled) {
        ICQEnabled = iCQEnabled;
    }

    public boolean isRelease() {
        return release;
    }

    public void setRelease(final boolean release) {
        this.release = release;
    }

    public boolean isEnabledUserRegister() {
        return enabledUserRegister;
    }

    public void setEnabledUserRegister(final boolean enabledUserRegister) {
        this.enabledUserRegister = enabledUserRegister;
    }

    public boolean isEnableForgetPassword() {
        return enableForgetPassword;
    }

    public void setEnableForgetPassword(final boolean enableForgetPassword) {
        this.enableForgetPassword = enableForgetPassword;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }
    
    public String getCityName() {
        return cityName;
    }

    public void setCityName(final String cityName) {
        this.cityName = cityName;
    }

    private RuntimeConfig() {

    }

    private final static RuntimeConfig INSTANCE = new RuntimeConfig();

    public static RuntimeConfig getInstance() {
        return INSTANCE;
    }
}
