package com.mindx.blog_service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mindx.blog_service.Util;

public class AccountDAO extends Db {
    private static final Logger _LOGGER = Logger.getLogger(AccountDAO.class.getName());
    // create Random
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        String username = "thang1160";
        String password = "123";
        // create passwordSalt string with 6 random characters a-z A-Z 0-9 using StringBuilder
        StringBuilder passwordSalt = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int random = RANDOM.nextInt(62);
            if (random < 10) {
                passwordSalt.append(random);
            } else if (random < 36) {
                passwordSalt.append((char) (random + 55));
            } else {
                passwordSalt.append((char) (random + 61));
            }
        }
        // Create hashed password
        String hashedPassword = Util.hashPassword(password, passwordSalt.toString());
        createAccount(username, hashedPassword, passwordSalt.toString());
    }

    public static void createAccount(String username, String password, String passwordSalt) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO account (username, password, password_salt) VALUES (?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, passwordSalt);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            _LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(conn, stmt);
        }
    }

    // get Account by username
    public static int getAccountId(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int accountId = 0;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM account WHERE username = ?");
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                String salt = rs.getString("password_salt");
                String realPassword = rs.getString("password");
                String hashedPassword = Util.hashPassword(password, salt);
                if (realPassword.equals(hashedPassword)) {
                    accountId = rs.getInt("account_id");
                }
            }
        } catch (SQLException ex) {
            _LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt, conn);
        }
        return accountId;
    }
}
