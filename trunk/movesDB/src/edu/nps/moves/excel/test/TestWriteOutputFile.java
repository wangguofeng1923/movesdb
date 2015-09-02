package edu.nps.moves.excel.test;

import edu.nps.moves.excel.jdbc.WriteDBtoFile;
import edu.nps.moves.excel.jdbc.ExcelDBDriver;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ahbuss
 */
public class TestWriteOutputFile {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

        Class.forName("edu.nps.moves.excel.jdbc.ExcelDBDriver");
        String outputFileName = args.length > 0 ? args[0] : "data/output.xlsx";
        File outputFile = new File(outputFileName);

        System.out.println(outputFile.getAbsoluteFile() + " " + outputFile.exists());

        String url = ExcelDBDriver.URL_PREFIX + outputFile.getAbsolutePath();
        Connection connection = DriverManager.getConnection(url);
        Statement statement = connection.createStatement();

        String dropSQL = "DROP TABLE IF EXISTS \"Table_1\"";
        statement.executeUpdate(dropSQL);
        String createSQL = "CREATE TABLE \"Table_1\" (\"Foo\" VARCHAR(255), \"Bar\" DOUBLE)";
        statement.executeUpdate(createSQL);
        String insertSQL = "INSERT INTO \"Table_1\" VALUES 'aFoo', 3.141";
        statement.executeUpdate(insertSQL);

        WriteDBtoFile writeDBtoFile = new WriteDBtoFile(connection, outputFile);
        writeDBtoFile.write();
        
        statement.close();
        connection.close();
    }

}
