package com.mindx.blog_service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlogDAO extends Db {
    private static final Logger _LOGGER = Logger.getLogger(BlogDAO.class.getName());
    
    public static int createBlog(String title, String content, List<String> tags, int accountId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        int blogId = 0;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO blog (account_id, title, [content], tags, created_at) OUTPUT Inserted.blog_id VALUES (?, ?, ?, ?, GETDATE())");
            stmt.setInt(1, accountId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            stmt.setString(4, String.join(",", tags));
            ResultSet rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                blogId = rs.getInt("blog_id");
            }
        } catch (SQLException ex) {
            _LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(conn, stmt);
        }
        return blogId;
    }
}
