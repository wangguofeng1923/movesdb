package edu.nps.moves.access.jdbc.test;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ahbuss
 */
public class TestAccessDBDriver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("edu.nps.moves.access.jdbc.AccessDBDriver");

        String inputFileName = args.length > 0 ? args[0] : "data/AgeRange.mdb";
        File inputFile = new File(inputFileName);
        System.out.println(inputFile.getAbsoluteFile() + " " + inputFile.exists());

        String url = "jdbc:access:" + inputFileName;
        System.out.println("URL: " + url);
        Connection connection = DriverManager.getConnection(url);

        Statement statement = connection.createStatement();

        DatabaseMetaData databaseMetadata = connection.getMetaData();
        ResultSet tableRS = databaseMetadata.getTables(null, null, null, null);
        while (tableRS.next()) {
            if ("TABLE".equalsIgnoreCase(tableRS.getString("TABLE_TYPE"))) {
                String tableName = tableRS.getString("TABLE_NAME");
                System.out.println(tableName);
                ResultSet columnRS = databaseMetadata.getColumns(null, null, tableName, null);
                while (columnRS.next()) {
                    System.out.print("\t" + columnRS.getString("COLUMN_NAME"));
                }
                System.out.println();
                    String query = String.format("SELECT * FROM \"%s\"", tableName);
//                System.out.println(query);
                ResultSet rs = statement.executeQuery(query);
                ResultSetMetaData rsMD = rs.getMetaData();
//                for (int column = 1; column <= rsMD.getColumnCount(); ++column) {
//                    System.out.print("\t" + rsMD.getColumnName(column));
//                }
//                System.out.println();
                while (rs.next()) {
                    for (int column = 1; column <= rsMD.getColumnCount(); ++column) {
                        System.out.print("\t" + rs.getString(column));
                    }
                    System.out.println();
                }
            }
        }
        
//        String tableName = "ScenarioData";
//        
//        String query = String.format("SELECT * FROM \"%s\"", tableName);
//        System.out.println(query);
//        
//        ResultSet rs = statement.executeQuery(query);
//        ResultSetMetaData rsMD = rs.getMetaData();
//        while (rs.next()) {
//            for (int column = 1; column <= rsMD.getColumnCount(); ++column) {
//                System.out.print("\t " + rs.getString(column));
//            }
//            System.out.println();
//        }
//        System.out.println();

        connection.close();

    }

}
