package com.soyokra.sprival.support.ck;

public class ClickHouseTemplate {
    private final ClickHouseConnectionFactory connectionFactory;

    public ClickHouseTemplate(ClickHouseConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }


    public void query(String sql) {
        this.connectionFactory.createConnection();
    }

}
