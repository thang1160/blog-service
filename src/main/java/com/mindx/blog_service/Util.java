package com.mindx.blog_service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.codec.digest.DigestUtils;

public class Util {
    private Util() {}

    public static final Gson GSON = new Gson();

    public static List<Map<String, Object>> convertResultSetToList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> list = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>(columns);
            for (int i = 1; i <= columns; ++i) {
                row.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(row);
        }
        return list;
    }

    public static String toJson(Object input) {
        return GSON.toJson(input);
    }

    public static void sendResponse(RoutingContext rc, int statusCode, Object object) {
        try {
            String result = null;
            JsonObject jContent = null;
            if (object instanceof JsonObject) {
                jContent = (JsonObject) object;
                result = jContent.encode();
            } else if (object instanceof List) {
                result = GSON.toJson(object);
            } else {
                jContent = new JsonObject(GSON.toJson(object));
                result = jContent.encode();
            }

            rc.response().setStatusCode(statusCode)
                    .putHeader("Content-Type", "application/json")
                    .end(result);
        } catch (Exception ex) {
            rc.fail(ex);
        }
    }

    public static void failureResponse(RoutingContext rc) {
        Throwable throwable = rc.failure();

        int statusCode = rc.statusCode();
        String message = throwable.getMessage();

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", statusCode);
        errorResponse.put("message", message);
        String json = GSON.toJson(errorResponse);

        rc.response().setStatusCode(statusCode).end(new JsonObject(json).encode());
    }

    public static String hashPassword(String password, String salt) {
        return DigestUtils.sha256Hex(password + salt);
    }
}
