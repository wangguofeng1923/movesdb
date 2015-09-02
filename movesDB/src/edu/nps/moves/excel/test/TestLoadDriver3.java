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
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ahbuss
 */
public class TestLoadDriver3 {

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

//        System.out.println("Tables:");
//        for (String table : tables) {
//            System.out.println(table);
//        }
        for (String table : tables) {
            System.out.println(table);
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
            while (rs.next()) {
                for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
                    System.out.print("\t");
                    switch(rsmd.getColumnType(column)) {
                        case VARCHAR:
                            System.out.print(rs.getString(column));
                            break;
                        case DOUBLE:
                            System.out.print(rs.getDouble(column));
                            break;
                        case BOOLEAN:
                            System.out.print(rs.getBoolean(column));
                            break;
                        case INTEGER:
                            System.out.print(rs.getInt(column));
                            break;
                        default:
                            System.out.print(rs.getObject(column));
                            break;
                    }
                }
                System.out.println();
            }
            rs.close();
        }

        System.out.println("INFORMATION_SCHEMA.COLUMNS:");
        ResultSet rs = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='Sheet2'");
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
            System.out.print("\t" + rsmd.getColumnName(column));
        }
        while (rs.next()) {
            for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
                System.out.print("\t" + rs.getString(column));
            }
            System.out.println();
        }
        System.out.println();
        
        rs = databaseMetaData.getColumns(null, null, "Sheet1", "One");
        if (rs.next()) {
            System.out.println(rs.getString("COLUMN_NAME"));
        }
        rs = databaseMetaData.getColumns(null, null, "Sheet2", "Column_1");
        if (rs.next()) {
            System.out.println(rs.getString("COLUMN_NAME"));
        }
        
        rs = databaseMetaData.getColumns(null, null, "Sheet2", null);
        System.out.println("All for Sheet2:");
        while(rs.next()) {
            System.out.print("\t" + rs.getString("COLUMN_NAME"));
        }
        System.out.println();

        rs.close();
        statement.close();
        connection.close();
    }

}
