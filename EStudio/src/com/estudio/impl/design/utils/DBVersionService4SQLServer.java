package com.estudio.impl.design.utils;

import com.estudio.intf.design.utils.IVersionService;

public final class DBVersionService4SQLServer extends DBVersionService {
    /**
     * @return
     */
    @Override
    protected String getVersionSQL() {
        return "select dbo.fun_4_design_get_version(?)";
    }

    /**
     * @return
     */
    @Override
    protected String getIncVersionSQL() {
        return "{call fun_4_design_inc_version(?)}";
    }

    private DBVersionService4SQLServer() {
        super();

    }

    private static final IVersionService INSTANCE = new DBVersionService4SQLServer();

    public static IVersionService getInstance() {
        return INSTANCE;
    }
}
