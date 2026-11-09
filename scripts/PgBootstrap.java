import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Creates the service databases in the local PostgreSQL instance.
 * Run: java -cp postgresql.jar scripts/PgBootstrap.java
 */
public final class PgBootstrap {

    private static final String URL = "jdbc:postgresql://127.0.0.1:5440/postgres";
    private static final String USER = "postgres";
    private static final List<String> DATABASES = List.of("auth", "vacancy", "notification", "billing");

    private PgBootstrap() {
    }

    public static void main(final String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, "postgres")) {
            for (final String name : DATABASES) {
                create(connection, name);
            }
        }
    }

    private static void create(final Connection connection, final String name) throws SQLException {
        if (exists(connection, name)) {
            System.out.println("exists  " + name);
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE " + name);
            System.out.println("created " + name);
        }
    }

    private static boolean exists(final Connection connection, final String name) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("select 1 from pg_database where datname = '" + name + "'")) {
            return result.next();
        }
    }
}
