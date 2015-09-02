package edu.nps.moves.access.jdbc;

import com.healthmarketscience.jackcess.ColumnBuilder;
import static com.healthmarketscience.jackcess.DataType.fromSQLType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Database.FileFormat;
import static com.healthmarketscience.jackcess.Database.FileFormat.V2003;
import static com.healthmarketscience.jackcess.Database.FileFormat.V2010;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Note: connection normally will be an HSQLDB connection to a temporary file
 * that is written to first via SQL statements and then output to an Access db
 * file using an instance of this class followed by write();
 * @author ahbuss
 */
public class WriteDBtoFile {

    private static final Logger logger = Logger.getLogger(WriteDBtoFile.class.getName());

    private Connection connection;

    private File outputFile;

    private FileFormat fileFormat;

    public WriteDBtoFile(Connection connection, File outputFile) throws SQLException {
        if (connection.isClosed()) {
            throw new SQLException("Connection is closed");
        }
        this.connection = connection;
        this.outputFile = outputFile;

        String extension = outputFile.getName();
        extension = extension.substring(extension.lastIndexOf(".") + 1);
        switch (extension) {
            case "accdb":
                fileFormat = V2010;
                break;
            case "mdb":
                fileFormat = V2003;
                break;
            default:
                throw new IllegalArgumentException(
                        "Output file must have extension .mdb or .accdb: "
                        + outputFile.getAbsolutePath()
                );
        }

    }

    public void write() throws SQLException, IOException {
        DatabaseBuilder databaseBuilder = new DatabaseBuilder(outputFile);
        databaseBuilder.setFileFormat(fileFormat);
        Database database = databaseBuilder.create();
//        For each table, create schema
        DatabaseMetaData databaseMetadata = connection.getMetaData();
        ResultSet tablesResultSet = databaseMetadata.getTables(null, null, null, null);
        List<String> tableNames = new ArrayList<>();
        while (tablesResultSet.next()) {
            if ("TABLE".equalsIgnoreCase(tablesResultSet.getString("TABLE_TYPE"))) {
                String tableName = tablesResultSet.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        }
//        Now populate each table
        Statement statement = connection.createStatement();
        for (String tableName : tableNames) {
            TableBuilder tableBuilder = new TableBuilder(tableName);
            String query = "SELECT * FROM \"" + tableName + "\"";
            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
                ColumnBuilder columnBuilder
                        = new ColumnBuilder(rsmd.getColumnName(column),
                                fromSQLType(rsmd.getColumnType(column)));
                tableBuilder.addColumn(columnBuilder);
            }
            Table table = tableBuilder.toTable(database);
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int column = 1; column <= rsmd.getColumnCount(); ++column) {
                    row.add(rs.getObject(column));
                }
                table.addRow(row.toArray());
            }

        }
        statement.close();
        database.close();
    }

}
