package com.estudio.define.db;

import com.estudio.intf.db.DBParamDataType;

public class SQLParam {

    protected String name;
    protected String label;
    protected DBParamDataType dataType;
    protected boolean partMatch = true;

    public boolean isPartMatch() {
        return partMatch;
    }

    public SQLParam() {
        super();
    }

    public DBParamDataType getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public SQLParam(final String name, final String label, final DBParamDataType dataType) {
        super();
        this.name = name;
        this.label = label;
        this.dataType = dataType;
    }

}
