package com.mindx.blog_service.service;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.mindx.blog_service.Util;
import com.mindx.blog_service.dao.BlogDAO;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class BlogHandler {
    private static final Logger _LOGGER = Logger.getLogger(BlogHandler.class.getName());

    public static void createBlog(RoutingContext rc) {
        rc.vertx().executeBlocking(blockingCodeHandler -> {
            try {
                JsonObject json = rc.getBodyAsJson();
                String title = json.getString("title");
                String content = json.getString("content");
                List<String> tags = json.getJsonArray("tags").getList();
                int accountId = Util.getAccountId(rc);
                int blogId = BlogDAO.createBlog(title, content, tags, accountId);
                Util.sendResponse(rc, 200, Map.of("blogId", blogId));
            } catch (Exception ex) {
                _LOGGER.severe(ex.getMessage());
                rc.fail(ex);
            }
        }, false, null);
    }

}
