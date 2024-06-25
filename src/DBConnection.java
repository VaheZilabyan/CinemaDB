import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;

public class DBConnection {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/Cinema";
    private static final String username = "root";
    private static final String password = "Vahe.2001";
    private static Connection connection;

    public Connection connect() {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Open a connection
            Connection connection = DriverManager.getConnection(url, username, password);
            if (connection != null) {
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    public static void close(Connection connection) throws SQLException {
        if (DBConnection.connection != null && !DBConnection.connection.isClosed()) {
            DBConnection.connection.close();
        }
        System.out.println("Databasa is closed!");
    }
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void printTable(DefaultTableModel model, String tableName) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            model.setColumnCount(0);
            model.setRowCount(0);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add column names to model
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }
            // Add data rows to model
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getTables() {
        ArrayList<String> tableNames = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Get database metadata
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseName = connection.getCatalog();
            // Get all tables' names
            ResultSet tables = metaData.getTables(databaseName, null, "%", new String[]{"TABLE"});

            // Print each table name
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableNames;
    }

    public static void deleteRow(String tableName, int id) {
        String columnName = getPrimaryKeyColumnName(tableName);
        if (columnName != null) {
            String sql = "DELETE FROM " + tableName + " WHERE " + columnName + " = ?";

            try (Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                int rowsAffected = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No primary key column found.");
        }
    }

    public static void deleteAllData(String tableName) {
        String sql = "DELETE FROM " + tableName;
        try (Connection connection = DBConnection.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getPrimaryKeyColumnName(String tableName) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getPrimaryKeys(null, null, tableName);

            String columnName = null;
            while (resultSet.next()) {
                columnName = resultSet.getString("COLUMN_NAME");
            }
            return columnName;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getTableMetaData(String tableName) {
        String url = "jdbc:mysql://127.0.0.1:3306/Cinema";
        String username = "root";
        String password = "Vahe.2001";
        ArrayList<String> tableMetaData = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Create statement
            Statement statement = connection.createStatement();

            // Execute query to retrieve data
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
            // Get ResultSetMetaData to obtain column names
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add column names to model
            for (int i = 1; i <= columnCount; i++) {
                tableMetaData.add(metaData.getColumnName(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableMetaData;
    }
}