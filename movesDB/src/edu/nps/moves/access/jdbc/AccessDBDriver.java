package edu.nps.moves.access.jdbc;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahbuss
 */
public class AccessDBDriver implements Driver {

    protected static final Logger logger = Logger.getLogger(AccessDBDriver.class.getName());

    private static final String HSQLDB_PREFIX_MEM = "jdbc:hsqldb:mem:";

    private static final String HSQLDB_PREFIX_FILE = "jdbc:hsqldb:file:";

    public static final String URL_PREFIX = "jdbc:access:";
    public static final boolean JDBC_COMPLIANT = false;
    public static final int MINOR_VERSION = 1;
    public static final int MAJOR_VERSION = 0;

    static {
        try {
            DriverManager.registerDriver(new AccessDBDriver());
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
        Database database = null;

        if (url == null) {
            throw new SQLException("null URL passed to AccessDriver");
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
        String hsqldbURL = null;

        if (info != null) {
            String useFile = info.getProperty("useFile");
            if (useFile != null && Boolean.parseBoolean(useFile)) {
                hsqldbURL = HSQLDB_PREFIX_FILE + inputFile.getAbsolutePath();
            } else {
                hsqldbURL = HSQLDB_PREFIX_MEM + inputFile.getAbsolutePath();
            }
        }

        Connection hsqldbConnection = DriverManager.getConnection(hsqldbURL);
        try {
            database = DatabaseBuilder.open(inputFile);
            CreateHSQLDB createHSQLDB = new CreateHSQLDB(hsqldbConnection);
            createHSQLDB.populateDatabase(database);
            database.close();
            connection = new AccessDBConnection(hsqldbConnection);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        return connection;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        boolean acceptsThis = url.startsWith(URL_PREFIX) && url.length() > URL_PREFIX.length();
        return acceptsThis;
    }

    /**
     * @param url Given URL
     * @param info Given Properties instance
     * @return zero-length array of DriverPropertyInfo
     * @throws SQLException Required by interface
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

    @Override
    public boolean jdbcCompliant() {
        return JDBC_COMPLIANT;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return logger.getParent();
    }

}
