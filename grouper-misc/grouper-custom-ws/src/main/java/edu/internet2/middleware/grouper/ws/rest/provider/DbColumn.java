package edu.internet2.middleware.grouper.ws.rest.provider;

public class DbColumn {
    private final String parameter;
    private final String columnName;
    private Class type;

    public DbColumn(final String parameter, final String columnName, final Class type) {
        this.parameter = parameter;
        this.columnName = columnName;
        this.type = type;
    }

    public String getParameter() {
        return parameter;
    }

    public String getColumnName() {
        return columnName;
    }

    public Class getType() {
        return type;
    }
}
