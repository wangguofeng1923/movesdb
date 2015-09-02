package edu.nps.moves.excel.test;

import edu.nps.moves.excel.jdbc.ExcelDBDriver;
import static edu.nps.moves.excel.jdbc.ExcelDBDriver.URL_PREFIX;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahbuss
 */
public class TestParseURL {

    static {
        try {
            Class.forName("edu.nps.moves.excel.jdbc.ExcelDBDriver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TestParseURL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:excel:data/DS_LBC_new.xlsx";
        System.out.println(url.toLowerCase().startsWith(URL_PREFIX));
        File inputFile = new File(url.substring(URL_PREFIX.length()));
        System.out.println(inputFile.getAbsoluteFile());

        String extension = inputFile.getName().substring(
                inputFile.getName().lastIndexOf("."));
        System.out.println(extension);
        System.out.println(ExcelDBDriver.isOldFormat(inputFile.getName()));
        System.out.println(ExcelDBDriver.isNewFormat(inputFile.getName()));

        url = url.toUpperCase();
        System.out.println(url);
        System.out.println(url.toLowerCase().startsWith(URL_PREFIX));
        System.out.println(ExcelDBDriver.isOldFormat(url));
        System.out.println(ExcelDBDriver.isNewFormat(url));

        String hsqldbURL = "jdbc:hsqldb:mem:" + inputFile.getAbsolutePath();
        System.out.println(hsqldbURL);

        Connection connection = DriverManager.getConnection(hsqldbURL);

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet tablesRS = databaseMetaData.getTables(null, null, null, null);

        while (tablesRS.next()) {
            System.out.println(
                    tablesRS.getString("TABLE_TYPE") + ": "
                    + tablesRS.getString("TABLE_NAME"));
        }

        connection.close();

    }

}
