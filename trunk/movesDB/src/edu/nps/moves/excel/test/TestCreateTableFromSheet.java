package edu.nps.moves.excel.test;

import edu.nps.moves.excel.jdbc.ExcelDBDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ahbuss
 */
public class TestCreateTableFromSheet {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {
        Class.forName("edu.nps.moves.excel.jdbc.ExcelDBDriver");

        String inputFileName = args.length > 0 ? args[0] : "data/Test.xlsx";
        File inputFile = new File(inputFileName);
        System.out.println("Input file: " + inputFile.getAbsolutePath() + " " + inputFile.exists());

        FileInputStream inputStream = new FileInputStream(inputFile);
        Workbook workbook = inputFile.getName().endsWith(".xls")
                ? new HSSFWorkbook(inputStream)
                : new XSSFWorkbook(inputStream);
        inputStream.close();

        String dbName = inputFile.getAbsolutePath();
//        dbName = dbName.substring(0, dbName.lastIndexOf("."));

        String excelURL = ExcelDBDriver.URL_PREFIX  + dbName;
        System.out.println("movesDB URL: " + excelURL);
        Connection connection = DriverManager.getConnection(excelURL);

        Statement statement = connection.createStatement();
//        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); ++sheetIndex) {
//            Sheet sheet = workbook.getSheetAt(sheetIndex);
//            CreateTableFromSheet.createTable(sheet, statement);
//        }

//        CreateTableFromSheet createTableFromSheet = new CreateTableFromSheet();
//        createTableFromSheet.createHSQLDBFromWorkbook(workbook, statement);

        DatabaseMetaData databaseMetadata = connection.getMetaData();
        List<String> tableNames = new ArrayList<>();
        ResultSet tableResultSet = databaseMetadata.getTables(null, null, null, null);
        while (tableResultSet.next()) {
            if (!"TABLE".equalsIgnoreCase(tableResultSet.getString("TABLE_TYPE"))) {
                continue;
            }
            String tableName = tableResultSet.getString("TABLE_NAME");
            tableNames.add(tableName);
            System.out.println("Table: " + tableName);
            ResultSet columnRS = databaseMetadata.getColumns(null, null, tableName, null);
            while (columnRS.next()) {
                System.out.print("\t" + columnRS.getString("COLUMN_NAME"));
            }
            System.out.println();
//            ResultSet columnTypeRS = databaseMetadata.getColumns(null, null, tableName, null);
//            while (columnTypeRS.next()) {
//                System.out.print("\t" + columnTypeRS.getString("DATA_TYPE"));
//            }
//            System.out.println();
            Sheet sheet = workbook.getSheet(tableName);

//            CreateTableFromSheet.populateTable(sheet, statement);
            ResultSet rs = statement.executeQuery("SELECT * FROM \"" + sheet.getSheetName() + "\"");
            ResultSetMetaData rsMeta = rs.getMetaData();
            for (int column = 1; column <= rsMeta.getColumnCount(); ++column) {
                System.out.print("\t" + rsMeta.getColumnTypeName(column));
            }
            System.out.println();
            while (rs.next()) {
                for (int column = 1; column <= rsMeta.getColumnCount(); ++column) {
                    System.out.print("\t" + rs.getObject(column));
                }
                System.out.println();
            }
        }

        if (tableNames.contains("Seeds")) {

            String query = "SELECT \"Index\", \"Seed\" FROM \"Seeds\" ORDER BY \"Index\"";
            ResultSet rs = statement.executeQuery(query);
            System.out.println();
            System.out.println("Seeds:");
            while (rs.next()) {
                int index = rs.getInt("Index");
                long seed = rs.getLong("Seed");
                System.out.println(index + " -> " + seed);
            }
        }
        System.out.println();

        if (tableNames.contains("ForceStructure")) {
            String query = "SELECT DISTINCT \"Unit\" FROM \"ForceStructure\"";
            ResultSet rs = statement.executeQuery(query);
            System.out.println("ForceStructure (Distinct units only):");
            while (rs.next()) {
                String unit = rs.getString("Unit");
                System.out.println(unit);
            }
        }
        
        statement.close();
        connection.close();

    }

}
