package edu.nps.moves.excel.test;

import edu.nps.moves.excel.jdbc.ExcelDBDriver;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ahbuss
 */
public class TestCommands {

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

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet tablesRS = databaseMetaData.getTables(null, null, null, null);
        List<String> tables = new ArrayList<>();
        while (tablesRS.next()) {
            if ("TABLE".equalsIgnoreCase(tablesRS.getString("TABLE_TYPE"))) {
                tables.add(tablesRS.getString("TABLE_NAME"));
            }
        }

        for (String table : tables) {
            System.out.println(table);
            ResultSet rs = databaseMetaData.getColumns(null, null, table, null);
            while (rs.next()) {
                System.out.print("\t" + rs.getString("COLUMN_NAME"));
            }
            System.out.println();
        }
        connection.close();
    }

}
