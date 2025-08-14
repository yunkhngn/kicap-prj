package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static final String URL  = "jdbc:sqlserver://localhost:1433;databaseName=kicap";
    private static final String USER = "yunkhngn";
    private static final String PASS = "123";

    static {
        try { Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); }
        catch (ClassNotFoundException e) { throw new RuntimeException("Không tìm thấy driver MSSQL", e); }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}