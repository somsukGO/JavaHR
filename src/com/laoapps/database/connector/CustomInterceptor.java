package com.laoapps.database.connector;

import com.laoapps.utils.Naming;
import org.hibernate.EmptyInterceptor;

public class CustomInterceptor extends EmptyInterceptor {
    private final String uuid;

    public CustomInterceptor(String tableName) {
        this.uuid = tableName;
    }

    @Override
    public String onPrepareStatement(String sql) {

        sql = sql.replace(Naming.DEPARTMENTS_TABLE_NAME + " ", Naming.DEPARTMENTS_TABLE_NAME + "_" + uuid + " ");

        return sql;
    }
}
