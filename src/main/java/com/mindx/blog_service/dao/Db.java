package com.mindx.blog_service.dao;

import com.mindx.blog_service.ConfigUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Db {
    private static Logger logger = Logger.getLogger(Db.class.getName());
    private static HikariDataSource pool;

    static {
        try {

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setDataSourceClassName("com.microsoft.sqlserver.jdbc.SQLServerDataSource");
            hikariConfig.setJdbcUrl(ConfigUtil.getDBUrl());
            hikariConfig.addDataSourceProperty("user", ConfigUtil.getDBUser());
            hikariConfig.addDataSourceProperty("password", ConfigUtil.getDBPassword());
            hikariConfig.addDataSourceProperty("databaseName", ConfigUtil.getDBName());
            hikariConfig.setPoolName("blog");
            hikariConfig.setMinimumIdle(ConfigUtil.getDBMinPoolSize());
            hikariConfig.setMaximumPoolSize(ConfigUtil.getDBMaxPoolSize());
            hikariConfig.setConnectionTestQuery("SELECT GETDATE();");
            pool = new HikariDataSource(hikariConfig);
            logger.info("Pool created");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "", e);
        }
    }

    public static void close(AutoCloseable... objs) {
        for (AutoCloseable obj : objs)
            try {
                if (obj != null)
                    obj.close();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "", e);
            }
    }

    protected static Connection getConnection() throws SQLException {
        return pool.getConnection();
    }
}
