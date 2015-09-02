package edu.nps.moves.excel.jdbc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hsqldb.types.Types;

/**
 *
 * @author ahbuss
 */
public class WriteDBtoFile {

    private static final Logger logger = Logger.getLogger(WriteDBtoFile.class.getName());

    private Connection connection;

    private Workbook workbook;

    private File outputFile;

    public WriteDBtoFile(Connection connection, File outputFile) throws SQLException {
        if (connection.isClosed()) {
            throw new SQLException("Connection is closed");
        }
        this.connection = connection;
        this.outputFile = outputFile;

        String fileName = outputFile.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        switch (extension) {
            case "xls":
                workbook = new HSSFWorkbook();
                break;
            case "xlsx":
            case "xlsm":
                workbook = new XSSFWorkbook();
                break;
            default:
                throw new RuntimeException(
                        "Unrecognized extension: " + extension);
        }
    }

    public void write() throws SQLException, IOException {
        DatabaseMetaData databaseMetadata = connection.getMetaData();
        ResultSet tablesResultSet = databaseMetadata.getTables(null, null, null, null);
        List<String> tableNames = new ArrayList<>();
        while (tablesResultSet.next()) {
            if ("TABLE".equalsIgnoreCase(tablesResultSet.getString("TABLE_TYPE"))) {
                String tableName = tablesResultSet.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        }
        Statement statement = connection.createStatement();
        for (String tableName : tableNames) {
            Sheet sheet = workbook.createSheet(tableName);
            String query = "SELECT * FROM \"" + tableName + "\"";
            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            Row headerRow = sheet.createRow(0);
            for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
                Cell cell = headerRow.createCell(column - 1);
                cell.setCellValue(rsmd.getColumnName(column));
            }
            int nextRow = 0;
            while (rs.next()) {
                nextRow += 1;
                Row row = sheet.createRow(nextRow);
                for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
                    Cell cell = row.createCell(column - 1);
                    switch (rsmd.getColumnType(column)) {
                        case Types.BOOLEAN:
                            cell.setCellValue(rs.getBoolean(column));
                            break;
                        case Types.VARCHAR:
                            cell.setCellValue(rs.getString(column));
                            break;
                        case Types.DECIMAL:
                        case Types.DOUBLE:
                            cell.setCellValue(rs.getDouble(column));
                            break;
                        case Types.INTEGER:
                            cell.setCellValue(rs.getInt(column));
                            break;
                        case Types.DATE:
                            cell.setCellValue(rs.getDate(column));
                            break;
                        default:
                            logger.severe("Unknown type: " + rsmd.getColumnType(column));
                    }
                }
            }
        }
        statement.close();
        try {
            if (outputFile.exists()) {
                outputFile.delete();
            }
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            workbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
