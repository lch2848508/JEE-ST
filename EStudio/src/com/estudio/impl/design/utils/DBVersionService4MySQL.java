package com.estudio.impl.design.utils;

import com.estudio.intf.design.utils.IVersionService;

public final class DBVersionService4MySQL extends DBVersionService {
    /**
     * @return
     */
    @Override
    protected String getVersionSQL() {
        return "select fun_design_get_version(?)";
    }

    /**
     * @return
     */
    @Override
    protected String getIncVersionSQL() {
        return "{call proc_design_inc_version(?)}";
    }

    private DBVersionService4MySQL() {
        super();

    }

    private static final IVersionService INSTANCE = new DBVersionService4MySQL();

    public static IVersionService getInstance() {
        return INSTANCE;
    }
}
