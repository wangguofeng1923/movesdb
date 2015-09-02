package edu.nps.moves.access.jdbc;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import edu.nps.moves.excel.util.SQLNames;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahbuss
 */
public class CreateHSQLDB {

    private static final Logger logger
            = Logger.getLogger(CreateHSQLDB.class.getName());

    protected Connection connection;

    public CreateHSQLDB(Connection connection) {
        this.connection = connection;
    }

    protected Statement statement;

    public void createTable(Table table) throws SQLException {
        if (statement == null) {
            statement = connection.createStatement();
        }

        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE ");
        queryBuilder.append('\"');
        queryBuilder.append(table.getName());
        queryBuilder.append('\"');
        queryBuilder.append(' ');
        queryBuilder.append('(');
        List<? extends Column> columns = (List<? extends Column>) table.getColumns();
        for (Column column : columns) {
            queryBuilder.append('\"');
            queryBuilder.append(column.getName());
            queryBuilder.append('\"');
            queryBuilder.append(' ');
            int type = column.getSQLType();
            String columnType = SQLNames.SQL_TYPES.get(type);
            if (type == Types.VARCHAR) {
                columnType += "(255)";
            }
            queryBuilder.append(columnType);
            queryBuilder.append(',');
        }
        if (queryBuilder.lastIndexOf(",") >= 0) {
            queryBuilder.replace(queryBuilder.lastIndexOf(","), queryBuilder.length(), ")");
        } else {
            queryBuilder.append(')');
        }

        String query = queryBuilder.toString();

        String dropTableQuery = String.format("DROP TABLE \"%s\" IF EXISTS",
                table.getName());
        statement.executeQuery(dropTableQuery);

        logger.fine(query);
        statement.executeQuery(query);
    }

    public void populateTable(Table table) throws SQLException, IOException {
        if (statement == null) {
            statement = connection.createStatement();
        }

        if (table.getRowCount() == 0) {
            return;
        }
        table.reset();
        for (Row row = table.getNextRow(); row != null; row = table.getNextRow()) {
            StringBuilder queryBuilder = new StringBuilder("INSERT INTO \"");
            queryBuilder.append(table.getName());
            queryBuilder.append("\" VALUES ");

            for (Column column : table.getColumns()) {
                Object value = row.get(column.getName());
                if (value instanceof String) {
                    value = "\'" + ((String) value).replaceAll("\\\\", "/") + "\'";
                }
                queryBuilder.append(value);
                queryBuilder.append(',');
            }
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);

            String query = queryBuilder.toString();
            logger.fine(query);
            statement.executeQuery(query);
        }
    }

    public void populateDatabase(Database database) throws SQLException {

        try {
            for (String tableName : database.getTableNames()) {
                Table table = database.getTable(tableName);
                createTable(table);
                populateTable(table);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        statement.close();
    }

}
