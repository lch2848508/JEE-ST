package com.estudio.impl.design.utils;

import com.estudio.intf.design.utils.IVersionService;

public final class DBVersionService4Oracle extends DBVersionService {
    /**
     * @return
     */
    @Override
    protected String getVersionSQL() {
        return "select fun_4_design_get_version(?) from dual";
    }

    /**
     * @return
     */
    @Override
    protected String getIncVersionSQL() {
        return "begin fun_4_design_inc_version(?);end;";
    }

    private DBVersionService4Oracle() {
        super();

    }

    private static final IVersionService INSTANCE = new DBVersionService4Oracle();

    public static IVersionService getInstance() {
        return INSTANCE;
    }
}
