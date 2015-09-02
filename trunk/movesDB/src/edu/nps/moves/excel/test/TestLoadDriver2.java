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
import java.util.Properties;

/**
 *
 * @author ahbuss
 */
public class TestLoadDriver2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String fileName = args.length > 0 ? args[0] : "data/Test2.xlsx";
        String url = ExcelDBDriver.URL_PREFIX + fileName;

        Class.forName("edu.nps.moves.excel.jdbc.ExcelDBDriver");

        System.out.println("Driver for " + url);
        Driver driver = DriverManager.getDriver(url);
        System.out.println(driver.getClass().getName());
        
//        To have HSQLDB use in-memory db, create Properties file and
//        set "useMemory" property to "true"
        Properties props = new Properties();
        props.put("useMemory", "TRUE");
//        props.remove("useMemory");
        Connection connection = DriverManager.getConnection(url, props);

        System.out.println(connection.getClass().getName());

        Statement statement = connection.createStatement();
        System.out.println(statement.getClass().getName());

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet tablesRS = databaseMetaData.getTables(null, null, null, null);
        List<String> tables = new ArrayList<>();
        List<String> tableTypes = new ArrayList<>();
        while (tablesRS.next()) {
            if ("TABLE".equalsIgnoreCase(tablesRS.getString("TABLE_TYPE"))) {
                tables.add(tablesRS.getString("TABLE_NAME"));
            }
        }

        System.out.println("Tables:");
        for (String table : tables) {
            System.out.println(table);
        }
        
        String tableName = "TimeVaryingConsumptionLogic";
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
        
        statement.close();
        connection.close();
    }

}
