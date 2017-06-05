package com.estudio.impl.design.objects;

public final class DBObjectQueryService4Oracle extends DBObjectQueryService {
    private DBObjectQueryService4Oracle() {
        super();
    }

    private static final DBObjectQueryService4Oracle INSTANCE = new DBObjectQueryService4Oracle();

    public static DBObjectQueryService4Oracle getInstance() {
        return INSTANCE;
    }
}
