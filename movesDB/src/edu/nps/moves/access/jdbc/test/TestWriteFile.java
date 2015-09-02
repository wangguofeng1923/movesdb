package edu.nps.moves.access.jdbc.test;

import edu.nps.moves.access.jdbc.WriteDBtoFile;
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
public class TestWriteFile {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        File tempFile = File.createTempFile("temp", null);
        System.out.println("Temp file: " + tempFile.getAbsolutePath());
        
        Connection connection = DriverManager.getConnection(
                "jdbc:hsqldb:mem:" + tempFile.getAbsolutePath(), "SA", "");
        
        Statement statement = connection.createStatement();
        
        String dropIfExists = "DROP TABLE IF EXISTS \"ScenarioData\"";
        statement.executeQuery(dropIfExists);
        
        String createSQL = "CREATE TABLE \"ScenarioData\" "
                + "(\"ScenarioLength\" DOUBLE, \"Replications\" INTEGER,"
                + " \"Verbose\" BOOLEAN, \"ReallyVerbose\" BOOLEAN,"
                + " \"ScenarioType\" VARCHAR(255))";
        statement.executeQuery(createSQL);
        
        String insertValuesSQL = "INSERT INTO \"ScenarioData\" " 
//                +  "(ScenarioLength, Replications, Verbose, ReallyVerbose)"
                + " VALUES "
                + "144, 10, FALSE, FALSE, 'DS_LBC'";
        statement.executeQuery(insertValuesSQL);
        
        File outputFile = new File("data/output.accdb");
        WriteDBtoFile writeDBToFile = new WriteDBtoFile(connection, outputFile);
        writeDBToFile.write();
        
        connection.close();
    }

}
