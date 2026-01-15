package hospital.hospital_management_system.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String DB_HOST = EnvLoader.get("DB_HOST", "localhost");
    private static final String DB_PORT = EnvLoader.get("DB_PORT", "5432");
    private static final String DB_NAME = EnvLoader.get("DB_NAME", "hospital_db");
    private static final String DB_USER = EnvLoader.get("DB_USER", "postgres");
    private static final String DB_PASSWORD = EnvLoader.get("DB_PASSWORD", "Alphonse");

    private DBConnection() {} // Prevent instantiation

    public static Connection getConnection() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME);
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }
}
