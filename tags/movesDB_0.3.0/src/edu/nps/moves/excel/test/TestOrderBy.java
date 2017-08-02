package edu.nps.moves.excel.test;

import edu.nps.moves.excel.jdbc.ExcelDBDriver;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ahbuss
 */
public class TestOrderBy {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String inputFileName = args.length > 0 ? args[0] : "data/SortByTest.xlsx";
        String url = ExcelDBDriver.URL_PREFIX + inputFileName;

        Class.forName("edu.nps.moves.excel.jdbc.ExcelDBDriver");
        Driver driver = DriverManager.getDriver(url);
        Connection connection = DriverManager.getConnection(url);

        Statement statement = connection.createStatement();

        String query = "SELECT * from TheTable";
        ResultSet rs = statement.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
            System.out.print(rsmd.getColumnName(column) + "\t");
        }
        System.out.println();
        while (rs.next()) {
            for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
                System.out.print(rs.getObject(column) + "\t");
            }
            System.out.println();
        }

        rs.close();
        
        query += " ORDER BY index";
        rs = statement.executeQuery(query);
        
        statement.close();
        connection.close();
    }

}
