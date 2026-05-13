package com.liamtseva.warehousemanagementsystem.persistence.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_DIR = System.getProperty("user.home") + File.separator + ".warehouse-management-system";
    private static final String DB_PATH = DB_DIR + File.separator + "warehouse.sqlite";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_PATH;
    private static DatabaseConnection instance;
    private static HikariDataSource dataSource;

    public DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public static void initializeDataSource() {
        boolean isNewDatabase = false;
        try {
            File directory = new File(DB_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists() || dbFile.length() == 0) {
                isNewDatabase = true;
            }
        } catch (Exception e) {
            System.err.println("Не вдалося створити каталог бази даних: " + e.getMessage());
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        dataSource = new HikariDataSource(config);

        if (isNewDatabase) {
            runInitScripts();
        }
    }

    private static void runInitScripts() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            executeSqlScript(stmt, "/ddl.sql");
            executeSqlScript(stmt, "/dml.sql");
        } catch (Exception e) {
            System.err.println("Помилка ініціалізації таблиць бази даних: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void executeSqlScript(Statement stmt, String resourcePath) throws Exception {
        try (InputStream is = DatabaseConnection.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Ресурс не знайдено: " + resourcePath);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("--") || trimmedLine.startsWith("/*")) {
                        continue;
                    }
                    sb.append(line).append("\n");
                    if (trimmedLine.endsWith(";")) {
                        stmt.execute(sb.toString());
                        sb.setLength(0);
                    }
                }
            }
        }
    }

    public DataSource getDataSource() {
        if (dataSource == null) {
            initializeDataSource();
        }
        return dataSource;
    }

    public void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}