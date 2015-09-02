package edu.nps.moves.excel.test;

import edu.nps.moves.excel.jdbc.DBUtils;
import edu.nps.moves.excel.jdbc.ExcelDBDriver;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author ahbuss
 */
public class TestDBUtils {

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

        DBUtils dbUtils = new DBUtils(connection);
        
        Map<String, Map<String, String>> map = dbUtils.getTableColumnMap();
        for (String tableName : map.keySet()) {
            System.out.println(tableName);
            Map<String, String> columnMap = map.get(tableName);
            for (String key : columnMap.keySet()) {
                System.out.println("\t" + key + " = " + columnMap.get(key));
            }
            System.out.println();
        }
        
        connection.close();
    }

}
