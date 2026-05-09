package com.project.artconnect;

import com.project.artconnect.util.ConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Quick test to verify JDBC connection to the database.
 */
public class JdbcConnectionTest {
    public static void main(String[] args) {
        try {
            Connection conn = ConnectionManager.getConnection();
            if (conn != null) {
                System.out.println("✓ JDBC Connection successful!");
                System.out.println("Database connection metadata:");
                System.out.println("  - Database: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("  - Version: " + conn.getMetaData().getDatabaseProductVersion());
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("✗ JDBC Connection failed!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("\nCheck your DatabaseConfig.java:");
            System.out.println("  - URL: verify 'artconnect_db' database name and host:port");
            System.out.println("  - USER: verify MySQL username");
            System.out.println("  - PASSWORD: verify MySQL password");
        }
    }
}
