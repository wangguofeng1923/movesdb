package edu.nps.moves.excel.test;

import edu.nps.moves.excel.jdbc.ExcelDBDriver;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ahbuss
 */
public class TestLoadDriver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String fileName = args.length > 0 ? args[0] : "data/DS_LBC_new.xlsx";
        String url = ExcelDBDriver.URL_PREFIX + fileName;

        Class.forName("edu.nps.moves.excel.jdbc.ExcelDBDriver");

        System.out.println("Driver for " + url);
        Driver driver = DriverManager.getDriver(url);
        System.out.println(driver.getClass().getName());

        Connection connection = DriverManager.getConnection(url);

        System.out.println(connection.getClass().getName());

        Statement statement = connection.createStatement();
        System.out.println(statement.getClass().getName());

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet tablesRS = databaseMetaData.getTables(null, null, null, null);
        List<String> tables = new ArrayList<>();
        while (tablesRS.next()) {
            if ("TABLE".equalsIgnoreCase(tablesRS.getString("TABLE_TYPE"))) {
                tables.add(tablesRS.getString("TABLE_NAME"));
            }
        }

        System.out.println("Tables:");
        for (String table : tables) {
            System.out.println(table);
        }

        String tableName = "ScenarioData";
        System.out.println(tableName);
        String query = "SELECT * FROM \"" + tableName + "\"";
        ResultSet resultSet = statement.executeQuery(query);

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        for (int column = 1; column <= resultSetMetaData.getColumnCount(); ++column) {
            System.out.print("\t" + resultSetMetaData.getColumnName(column));
        }
        System.out.println();
        for (int column = 1; column <= resultSetMetaData.getColumnCount(); ++column) {
            System.out.print("\t" + resultSetMetaData.getColumnTypeName(column));
        }
        System.out.println();
        while (resultSet.next()) {
            for (int column = 1; column <= resultSetMetaData.getColumnCount(); ++column) {
                System.out.print("\t" + resultSet.getString(column));
            }
            System.out.println();
        }

        tableName = "Output";
        System.out.println(tableName);
        query = "SELECT * FROM \"" + tableName + "\"";        resultSet = statement.executeQuery(query);
        resultSetMetaData = resultSet.getMetaData();
        for (int column = 1; column <= resultSetMetaData.getColumnCount(); ++column) {
            System.out.print("\t" + resultSetMetaData.getColumnName(column));
        }
        System.out.println();
        for (int column = 1; column <= resultSetMetaData.getColumnCount(); ++column) {
            System.out.print("\t" + resultSetMetaData.getColumnTypeName(column));
        }
        System.out.println();
        while (resultSet.next()) {
            for (int column = 1; column <= resultSetMetaData.getColumnCount(); ++column) {
                System.out.print("\t" + resultSet.getString(column));
            }
            System.out.println();
        }
        connection.close();
    }

}
