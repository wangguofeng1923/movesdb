package edu.nps.moves.excel.test;

import edu.nps.moves.excel.jdbc.ExcelDBDriver;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author ahbuss
 */
public class TestEmptyCells {

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String fileName = args.length > 0 ? args[0] : "data/Test3.xlsx";
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
        String query = "SELECT * FROM Sheet1";
        ResultSet resultSet = statement.executeQuery(query);
        ResultSetMetaData rsmd = resultSet.getMetaData();
        for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
            System.out.print(rsmd.getColumnName(column) + "\t");
        }
        System.out.println();
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            Object idObj = resultSet.getObject("id");
            String id;
            if (idObj != null) {
                id = idObj.toString();
            } else {
                id = "NULL";
            }
            Double value;
            Object valueObj = resultSet.getObject("value");
            if (valueObj != null) {
                value = resultSet.getDouble("value");
            } else {
                value = Double.NaN;
            }
             
            System.out.print(name + "\t" + id + "\t" + value);
            System.out.println();
        }

        resultSet.close();
        statement.close();
        connection.close();
    }

}
