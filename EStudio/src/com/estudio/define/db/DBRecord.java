package com.estudio.define.db;

import net.minidev.json.JSONObject;

public abstract class DBRecord {
    boolean recordIsNew = true;

    public boolean isNew() {
        return recordIsNew;
    }

    public void setOld() {
        recordIsNew = false;
    }

    public abstract JSONObject getJSON();
}
