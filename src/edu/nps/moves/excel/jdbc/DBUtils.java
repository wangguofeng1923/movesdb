package edu.nps.moves.excel.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ahbuss
 */
public class DBUtils {

    private Map<String, Map<String, String>> tableColumnMap;
    
    public DBUtils(Connection connection) throws SQLException {
        tableColumnMap = new HashMap<>();
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getTables(null, null, null, null);
        while (resultSet.next()) {
            if ("TABLE".equalsIgnoreCase(resultSet.getString("TABLE_TYPE"))) {
                String tableName = resultSet.getString("TABLE_NAME");
                Map<String, String> columnMap = new HashMap<>();
                tableColumnMap.put(tableName, columnMap);
                ResultSet columnResultSet = databaseMetaData.getColumns(null, null, tableName, null);
                while (columnResultSet.next()) {
                    String columnName = columnResultSet.getString("COLUMN_NAME");
                    columnMap.put(columnName.toUpperCase(), columnName);
                }
            }
        }
    }
    
    public String getColumnNameFor(String tableName, String columnName) {
        String realName = null;
        
        return realName;
    }

    public Map<String, Map<String, String>> getTableColumnMap() {
        Map<String, Map<String, String>> tcm = new HashMap<>();
        for (String key : tableColumnMap.keySet()) {
            Map<String, String> value = tableColumnMap.get(key);
            tcm.put(key, new HashMap<>(value));
        }
        return tcm;
    }
    
}
