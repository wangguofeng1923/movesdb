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
public class TestColumnExists {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String fileName = args.length > 0 ? args[0] : "data/Test.xlsx";
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

        for (String table : tables) {
            ResultSet rs = statement.executeQuery("SELECT * FROM \"" + table + "\"");
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
                System.out.print("\t" + rsmd.getColumnName(column));
            }
            System.out.println();
            for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
                System.out.print("\t" + rsmd.getColumnTypeName(column));
            }
            System.out.println();
            rs.close();
        }
        
        ResultSet columnRS = databaseMetaData.getColumns(null, null, "Sheet2", "Column_2");
        ResultSetMetaData columnRSMD = columnRS.getMetaData();
        for (int column = 1; column <= columnRSMD.getColumnCount(); ++column) {
            System.out.print("\t" + columnRSMD.getColumnName(column));
        }
        System.out.println();
        while (columnRS.next()) {
            System.out.println(columnRS.getString("COLUMN_NAME"));
        }
        System.out.println("Attempting for Column_3:");
        columnRS = databaseMetaData.getColumns(null, null, "Sheet2", "Column_3");
        
        System.out.println(columnRS.next());
        
        columnRS.first();
        while (columnRS.next()) {
            System.out.println(columnRS.getString("COLUMN_NAME"));
        }
        
        columnRS.close();
        
        statement.close();
        connection.close();
    }

}
