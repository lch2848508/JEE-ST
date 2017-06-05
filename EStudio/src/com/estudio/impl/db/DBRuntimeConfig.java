package com.estudio.impl.db;

public class DBRuntimeConfig {
    public boolean isRelease = false;

    private DBRuntimeConfig() {
    }
    
    public static final DBRuntimeConfig instance = new DBRuntimeConfig();
}
