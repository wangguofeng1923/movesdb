/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nps.moves.excel.jdbc;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 *
 * @author ahbuss
 */
public class ExcelDBStatement implements Statement {

    public static final CCJSqlParserManager ccjSqlParserManager = new CCJSqlParserManager();

    private static final Logger LOGGER = Logger.getLogger(ExcelDBConnection.class.getName());

    private final Statement wrappedStatement;

    public ExcelDBStatement(Statement wrappedStatement) {
        this.wrappedStatement = wrappedStatement;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return wrappedStatement.executeQuery(fixQuery(sql));
//        return wrappedStatement.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return wrappedStatement.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        wrappedStatement.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return wrappedStatement.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        wrappedStatement.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return wrappedStatement.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        wrappedStatement.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        wrappedStatement.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return wrappedStatement.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        wrappedStatement.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        wrappedStatement.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return wrappedStatement.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        wrappedStatement.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        wrappedStatement.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return wrappedStatement.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return wrappedStatement.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return wrappedStatement.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        wrappedStatement.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return wrappedStatement.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        wrappedStatement.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return wrappedStatement.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return wrappedStatement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return wrappedStatement.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        wrappedStatement.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        wrappedStatement.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Connection getConnection() throws SQLException {
        return wrappedStatement.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return wrappedStatement.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return wrappedStatement.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return wrappedStatement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return wrappedStatement.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        wrappedStatement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return wrappedStatement.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        wrappedStatement.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return wrappedStatement.isCloseOnCompletion();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return wrappedStatement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return wrappedStatement.isWrapperFor(iface);
    }

    public static String fixQuery(String query) {
        String fixedQuery = query;

        try {
            net.sf.jsqlparser.statement.Statement statement = ccjSqlParserManager.parse(new StringReader(query));
            if (statement instanceof Select) {
                SelectBody selectBody = ((Select) statement).getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    PlainSelect plainSelect = (PlainSelect) selectBody;
                    List<SelectItem> selectItems = plainSelect.getSelectItems();
                    for (SelectItem item : selectItems) {
                        String select = item.toString();
                        if (!select.equals("*")) {
                            if (item instanceof SelectExpressionItem) {
                                SelectExpressionItem selectItem = (SelectExpressionItem) item;
                                Expression expression = selectItem.getExpression();
                                if (expression instanceof Column) {
                                    Column column = (Column) expression;
                                    column.setColumnName('\"' + select + '\"');
                                }
                            }
                        }
                    }
                    FromItem fromItem = plainSelect.getFromItem();
                    if (fromItem instanceof Table) {
                        fixTableName((Table) fromItem);
                    }

                    List<Join> joins = plainSelect.getJoins();
                    if (joins != null) {
                        for (Join join : joins) {
                            fromItem = join.getRightItem();
                            if (fromItem instanceof Table) {
                                fixTableName((Table) fromItem);
                            }
                            Expression onExpression = join.getOnExpression();
                            if (onExpression != null && onExpression instanceof BinaryExpression) {
                                fixBinaryExpression((BinaryExpression) onExpression);
                            }
                        }
                    }

                    Expression whereExpression = plainSelect.getWhere();
                    if (whereExpression != null) {
                        if (whereExpression instanceof ComparisonOperator) {
                            fixComparisonOperator((ComparisonOperator) whereExpression);
                        } else if (whereExpression instanceof AndExpression) {
                            fixBinaryExpression((BinaryExpression) whereExpression);
                        }
                    }

                    List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
                    if (orderByElements != null) {
                        for (OrderByElement obe : orderByElements) {
                            Expression expression = obe.getExpression();
                            if (expression instanceof Column) {
                                Column column = (Column) expression;
                                column.setColumnName('\"' + column.getColumnName() + '\"');
                            }
                        }
                    }

                    fixedQuery = ((Select) statement).toString();
                }
            }

        } catch (JSQLParserException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return fixedQuery;
    }

    private static void fixBinaryExpression(BinaryExpression binaryExpression) {
        Expression leftExpression = binaryExpression.getLeftExpression();
        if (leftExpression instanceof Column) {
            Column column = (Column) leftExpression;
            column.setColumnName('\"' + column.getColumnName() + '\"');
            Table table = column.getTable();
            if (table.getName() != null) {
                fixTableName(table);
            }
        } else if (leftExpression instanceof ComparisonOperator) {
            fixComparisonOperator((ComparisonOperator) leftExpression);
        }

        Expression rightExpression = binaryExpression.getRightExpression();
        if (rightExpression instanceof Column) {
            Column column = (Column) rightExpression;
            column.setColumnName('\"' + column.getColumnName() + '\"');
            Table table = column.getTable();
            if (table.getName() != null) {
                fixTableName(table);
            }
        } else if (rightExpression instanceof ComparisonOperator) {
            fixComparisonOperator((ComparisonOperator) rightExpression);
        }
    }

    private static void fixComparisonOperator(ComparisonOperator comparisonOperator) {
        Expression leftExpression = comparisonOperator.getLeftExpression();
        if (leftExpression instanceof Column) {
            Column column = (Column) leftExpression;
            Table table = column.getTable();
            column.setColumnName('\"' + column.getColumnName() + '\"');
            if (table.getName() != null) {
                table.setName("\"" + table.getName() + "\"");
            }
        }
        Expression rightExpression = comparisonOperator.getRightExpression();
        if (rightExpression instanceof Column) {
            Column column = (Column) rightExpression;
            Table table = column.getTable();
            column.setColumnName('\"' + column.getColumnName() + '\"');
            if (table.getName() != null) {
                table.setName("\"" + table.getName() + "\"");
            }
        }
    }

    private static void fixTableName(Table table) {
        String name = table.getName();
        if (name != null) {
            table.setName("\"" + name + "\"");
        }
    }
}
