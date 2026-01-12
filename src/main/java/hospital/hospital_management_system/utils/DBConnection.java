package hospital.hospital_management_system.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String DATABASE_NAME = "hospital_db";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "Alphonse";

    private DBConnection() {} // Prevent instantiation

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/" + DATABASE_NAME,
                DATABASE_USER,
                DATABASE_PASSWORD
        );
    }
}
