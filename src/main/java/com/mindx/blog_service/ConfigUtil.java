package com.mindx.blog_service;

import io.vertx.core.json.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigUtil {
    private static Logger logger = Logger.getLogger(ConfigUtil.class.getName());

    private static JsonObject config;

    static {
        try {
            InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream("config.json");
            config = new JsonObject(new String(getBytesFromInputStream(inputStream), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "", e);
        }
    }

    public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    public static Object getValue(String path, Object def) {
        Object o = config;
        for (String key : path.split("[.]")) {
            if (o instanceof JsonObject)
                o = ((JsonObject) o).getValue(key);
            if (o == null)
                break;
        }
        return o != null ? o : def;
    }

    public static Boolean getBoolean(String path, String def) {
        return (Boolean) getValue(path, def);
    }


    public static String getString(String path, String def) {
        return (String) getValue(path, def);
    }

    public static Integer getInteger(String path, Integer def) {
        return (Integer) getValue(path, def);
    }

    public static Long getLong(String path, Long def) {
        return (Long) getValue(path, def);
    }

    public static Double getDouble(String path, Double def) {
        return Double.parseDouble(String.valueOf(getValue(path, def)));
    }

    public static String getUriPrefix() {
        return getString("server.uri_prefix", "/blog/api/v1");
    }

    public static String getPublicKey() {
        return getString("server.public_key", null);
    }

    public static String getPrivateKey() {
        return getString("server.private_key", null);
    }

    public static int getServerPort() {
        return Integer.parseInt(getInteger("server.port", 8080) + "");
    }

    public static String getDBUrl() {
        return getString("database.url", null);
    }

    public static String getDBUser() {
        return getString("database.user", null);
    }

    public static String getDBPassword() {
        return getString("database.password", null);
    }

    public static String getDBName() {
        return getString("database.databaseName", null);
    }

    public static int getDBInitialPoolSize() {
        return getInteger("database.init_pool_size", 1);
    }

    public static int getDBMinPoolSize() {
        return getInteger("database.min_pool_size", 1);
    }

    public static int getDBMaxPoolSize() {
        return getInteger("database.max_pool_size", 10);
    }

}
