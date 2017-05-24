package edu.nps.moves.excel.test;

import edu.nps.moves.excel.jdbc.ExcelDBDriver;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ahbuss
 */
public class TeatReadNOLH {

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException if driver not found
     * @throws java.sql.SQLException if problem with SQL query
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("edu.nps.moves.excel.jdbc.ExcelDBDriver");

        String inputFileName = args.length > 0 ? args[0] : "data/nolh.xlsx";
        String url = ExcelDBDriver.URL_PREFIX + inputFileName;

        File inputFile = new File(inputFileName);
        System.out.println("Input file: " + inputFile.getAbsolutePath() + " " + inputFile.exists());

        Connection connection = DriverManager.getConnection(url);
        Statement statement = connection.createStatement();

        String query = "SELECT * FROM OLH7";
        ResultSet rs = statement.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int column = 2; column <= rsmd.getColumnCount(); ++column) {
            System.out.print(rsmd.getColumnName(column) + "\t");
        }
        System.out.println();
        while (rs.next()) {
            String firstColumn = rs.getString(1);
            if (firstColumn == null) {
                for (int column = 2; column <= rsmd.getColumnCount(); ++column) {
                    System.out.print(rs.getObject(column) + "\t");
                }
                System.out.println();
            }
        }
        rs.close();
        statement.close();
        connection.close();

    }

}
