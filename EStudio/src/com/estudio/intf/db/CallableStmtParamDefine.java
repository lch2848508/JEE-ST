package com.estudio.intf.db;

public class CallableStmtParamDefine {

    public DBParamDataType type;
    public boolean isOutput = false;

    public CallableStmtParamDefine() {
        super();
    }

    public CallableStmtParamDefine(final DBParamDataType type, final boolean isOutput) {
        super();
        this.type = type;
        this.isOutput = isOutput;
    }

}
