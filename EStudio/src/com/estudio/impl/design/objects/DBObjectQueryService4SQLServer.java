package com.estudio.impl.design.objects;

public final class DBObjectQueryService4SQLServer extends DBObjectQueryService {
    private DBObjectQueryService4SQLServer() {
        super();
    }

    private static final DBObjectQueryService4SQLServer INSTANCE = new DBObjectQueryService4SQLServer();

    public static DBObjectQueryService4SQLServer getInstance() {
        return INSTANCE;
    }
}
