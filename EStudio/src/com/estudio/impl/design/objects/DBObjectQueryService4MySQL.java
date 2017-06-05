package com.estudio.impl.design.objects;

public final class DBObjectQueryService4MySQL extends DBObjectQueryService {
    private DBObjectQueryService4MySQL() {
        super();
    }

    private static final DBObjectQueryService4MySQL INSTANCE = new DBObjectQueryService4MySQL();

    public static DBObjectQueryService4MySQL getInstance() {
        return INSTANCE;
    }
}
