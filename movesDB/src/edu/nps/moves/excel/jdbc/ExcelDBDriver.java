package edu.nps.moves.excel.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ahbuss
 */
public class ExcelDBDriver implements Driver {

    protected static final Logger logger = Logger.getLogger(ExcelDBDriver.class.getName());

    public static final List<String> oldExtensions
            = Arrays.asList(new String[]{"xls"});

    public static final List<String> newExtensions
            = Arrays.asList(new String[]{"xlsx", "xlsm"});
    
    private static final String HSQLDB_PREFIX_MEM = "jdbc:hsqldb:mem:";

    public static final String URL_PREFIX = "jdbc:excel:";
    public static final boolean JDBC_COMPLIANT = false;
    public static final int MINOR_VERSION = 2;
    public static final int MAJOR_VERSION = 0;

    static {
        try {
            DriverManager.registerDriver(new ExcelDBDriver());
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            logger.severe("HSQLDB Driver Not Found");
            throw new RuntimeException(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        Connection connection = null;
        Workbook workbook = null;
        if (url == null) {
            throw new SQLException("null URL passed to ExcelDBDriver");
        }

        if (!acceptsURL(url)) {
            return null;
        }

        if (!url.toLowerCase().startsWith(URL_PREFIX)) {
            throw new SQLException("URL does not start with "
                    + URL_PREFIX);
        }

        File inputFile = new File(url.substring(URL_PREFIX.length()));
        if (!inputFile.exists()) {
            throw new SQLException("File not found: " + inputFile);
        }

        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            if (isOldFormat(inputFile.getName())) {
                workbook = new HSSFWorkbook(inputStream);
                inputStream.close();
            } else if (isNewFormat(inputFile.getName())) {
                workbook = new XSSFWorkbook(inputStream);
                inputStream.close();
            } else {
                throw new SQLException("Input file not Excel format: "
                        + inputFile.getAbsolutePath());
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, inputFile.getAbsolutePath(), ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, inputFile.getAbsolutePath(), ex);
        }

        String hsqldbURL = HSQLDB_PREFIX_MEM + inputFile.getAbsolutePath();
        Connection hsqldbConnection = DriverManager.getConnection(hsqldbURL);
        Statement hsqlStatement = hsqldbConnection.createStatement();
        new CreateTableFromSheet().createHSQLDBFromWorkbook(workbook, hsqlStatement);
        
        connection = new ExcelDBConnection(hsqldbConnection);
        return connection;
    }

    public static boolean isOldFormat(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return oldExtensions.contains(extension);
    }

    public static boolean isNewFormat(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return newExtensions.contains(extension);
    }

    /**
     *
     * @param url URL to consider
     * @return true if url starts with URL_PREFIX and has more characters than
     * the prefix
     * @throws SQLException
     */
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        boolean acceptsThis = url.startsWith(URL_PREFIX) && url.length() > URL_PREFIX.length();
        return acceptsThis;
    }

    /**
     *
     * @param url
     * @param info
     * @return zero-length array of DriverPropertyInfo
     * @throws SQLException
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    @Override
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    /**
     *
     * @return false
     */
    @Override
    public boolean jdbcCompliant() {
        return JDBC_COMPLIANT;
    }

    /**
     *
     * @return parent of logger
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return logger.getParent();
    }

}
