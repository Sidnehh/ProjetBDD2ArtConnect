package com.project.artconnect.config;

/**
 * Database configuration constants.
 * 
 * INSTRUCTIONS:
 * 1. Modify URL if your MySQL database is on a different host/port/name
 * 2. Set USER and PASSWORD according to your MySQL credentials
 * 3. Ensure the database "artconnect_db" exists (create it with schema scripts if needed)
 */
public class DatabaseConfig {
    // Example: jdbc:mysql://localhost:3306/artconnect_db
    public static final String URL = "jdbc:mysql://localhost:3306/artconnect";
    
    // MySQL username (default: root)
    public static final String USER = "root";
    
    // MySQL password (modify according to your setup)
    public static final String PASSWORD = "password";
}
