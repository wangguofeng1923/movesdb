package edu.nps.moves.access.jdbc.test;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import edu.nps.moves.access.jdbc.CreateHSQLDB;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 *
 * @author ahbuss
 */
public class TestCreateHSQDB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");

        String inputFileName = args.length > 0 ? args[0] : "data/dsLbc.mdb";
        File inputFile = new File(inputFileName);
        System.out.println(inputFile.getAbsoluteFile() + " "
                + inputFile.exists());

        Connection connection = DriverManager.getConnection(
                "jdbc:hsqldb:mem:" + inputFileName, "SA", "");

        Statement statement = connection.createStatement();

        Database database = DatabaseBuilder.open(inputFile);

        Set<String> tableNames = database.getTableNames();

        CreateHSQLDB createHSQLDB = new CreateHSQLDB(connection);
        createHSQLDB.populateDatabase(database);

//        for (String tableName : tableNames) {
//            Table table = database.getTable(tableName);
//            createHSQLDB.createTable(table);
//        }
        System.out.println("Tables:");
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet tableRS = databaseMetaData.getTables(null, null, null, null);
        for (String tableName : tableNames) {
            System.out.println(tableName);
            ResultSet columnRS = databaseMetaData.getColumns(null, null, tableName, null);
            while (columnRS.next()) {
                System.out.print("\t" + columnRS.getString("COLUMN_NAME"));
            }
            System.out.println();
        }


        connection.close();
        database.close();
    }

}
