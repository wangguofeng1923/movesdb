package edu.nps.moves.excel.jdbc;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author ahbuss
 */
public class CreateTableFromSheet {

    private static final Logger logger = Logger.getLogger(CreateTableFromSheet.class.getName());

    public static final Map<CellType, String> mapping;

    static {
        mapping = new HashMap<>();
        mapping.put(BOOLEAN, "BOOLEAN");
        mapping.put(NUMERIC, "DOUBLE");
        mapping.put(STRING, "VARCHAR(255)");
        mapping.put(BLANK, "VARCHAR(255)");
    }
    
    protected Map<String, List<String>> columnNameMap;
    
    /**
     * <p>Creates a database table with the sheet name and the columns
     * specified by the first row.</p>
     * 
     * <p>First row of sheet should contain only Strings, and ideally should not
     * have any blank cells with data contained in that column. If a first row
     * cell is blank or null, it is replaced with "Blank-0", "Blank-1", etc.</p>
     * 
     * <p>If Cell in first row is a formula, it is evaluated. If a non-String,
     * the cell is converted to a String type and its contents replaced with 
     * a String version of the value. Normally, this should not be the case,
     * however.</p>
     * 
     * <p>The cell types are attempted to be discerned from the second row
     * of the sheet. Ideally, the second row should not have any blank cells.
     * If it does, then the type for that column is VARCHAR(255). Otherwise,
     * the type is set according to the mapping:</p>
     * <UL><LI>CELL_TYPE_BOOLEAN - BOOLEAN</LI>
     * <LI>CELL_TYPE_NUMERIC -&gt; DOUBLE</LI>
     * <LI>CELL_TYPE_STRING -&gt; VARCHAR(255)</LI>
     * <LI>CELL_TYPE_BLANK -&gt; VARCHAR(255)</LI></UL>
     * 
     * @param sheet Sheet from which to create a table
     * @param statement Used to execute CREATE statement
     * @throws SQLException If any queries to wrapped connection throw one
     */

    public void createTable(Sheet sheet, Statement statement) throws SQLException {

        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

        Row firstRow = sheet.getRow(0);
        if (firstRow == null) {
            throw new SQLException("No first row in sheet " + sheet.getSheetName());
        }
        columnNameMap = new LinkedHashMap<>();
        
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE ");
        queryBuilder.append('\"');
        queryBuilder.append(sheet.getSheetName().trim());
        queryBuilder.append('\"');
        queryBuilder.append(' ');
        queryBuilder.append('(');
        List<String> columnNames = new ArrayList<>();
        int nextIndexForMissing = 0;
        int nextDuplicateColumnIndex = 0;
        for (int column = 0; column < firstRow.getLastCellNum(); ++column) {
            Cell cell = firstRow.getCell(column);
            String columnName = null;
            if (cell == null || cell.getCellTypeEnum()== BLANK) {
                cell = firstRow.createCell(column, STRING);
                String missingColumnName = "Blank-" + nextIndexForMissing;
                cell.setCellValue(missingColumnName);
                nextIndexForMissing += 1;
                columnName = cell.getStringCellValue();
                logger.info(String.format("Cell %d in sheet %s was null, named %s",
                        column, sheet.getSheetName(), cell.getStringCellValue()));
            } else if (cell.getCellTypeEnum()!= STRING) {
                switch (cell.getCellTypeEnum()) {
                    case NUMERIC:
                        double value = cell.getNumericCellValue();
                        cell.setCellType(STRING);
                        cell.setCellValue(Double.toString(value));
                        columnName = cell.getStringCellValue();
                        break;
                }
            } else if (cell.getCellTypeEnum()== STRING) {
                columnName = cell.getStringCellValue().trim();
            } else {
                throw new SQLException(String.format("Column %d of sheet %s not a String",
                        column, sheet.getSheetName()));
            }
            if (!columnNames.contains(columnName)) {
                columnNames.add(columnName);
            } else {
                columnNames.add(columnName + "-" + nextDuplicateColumnIndex);
                nextDuplicateColumnIndex += 1;
            }
        }
        
        for (int columnIndex = columnNames.size() - 1; columnIndex >= 0; --columnIndex) {
            if (columnNames.get(columnIndex).startsWith("Blank")) {
                String blankFirstColumn = columnNames.remove(columnIndex);
                logger.info(String.format("Blank column %s at end of row removed",
                        blankFirstColumn));
            } else {
                break;
            }
        }
        columnNameMap.put(sheet.getSheetName(), columnNames);
        
        List<String> columnTypes = new ArrayList<>();
        Row secondRow = sheet.getRow(1);
        if (secondRow == null) {
            logger.log(Level.INFO, "No second row in sheet {0}", sheet.getSheetName());
        }
        for (int column = 0; column < columnNames.size(); ++column) {
            if (secondRow == null) {
                columnTypes.add("VARCHAR(255)");
                continue;
            }
            Cell cell = secondRow.getCell(column);
            if (cell == null) {
                columnTypes.add("VARCHAR(255)");
            } else if (mapping.containsKey(cell.getCellTypeEnum())) {
                columnTypes.add(mapping.get(cell.getCellTypeEnum()));
            } else if (cell.getCellTypeEnum() == FORMULA) {
                CellValue value = evaluator.evaluate(cell);
                cell.setCellType(value.getCellTypeEnum());
                columnTypes.add(mapping.get(cell.getCellTypeEnum()));
            } else {
                throw new SQLException(
                        String.format("Cell %d in sheet %s not required type: %d",
                                column, sheet.getSheetName(), cell.getCellTypeEnum()));
            }
        }
        for (int i = 0; i < columnNames.size(); ++i) {
            queryBuilder.append('\"');
            queryBuilder.append(columnNames.get(i));
            queryBuilder.append('\"');
            queryBuilder.append(' ');
            queryBuilder.append(i < columnTypes.size() ? columnTypes.get(i) : "VARCHAR(255)");
            queryBuilder.append(',');
        }
        if (queryBuilder.lastIndexOf(",") >= 0){
            queryBuilder.replace(queryBuilder.lastIndexOf(","), queryBuilder.length(), ")");
        } else {
            queryBuilder.append(')');
        }

        String query = queryBuilder.toString();

        String dropTableQuery = String.format("DROP TABLE \"%s\" IF EXISTS",
                sheet.getSheetName());
        statement.executeQuery(dropTableQuery);

        logger.fine(query);
        statement.executeQuery(query);

    }

    public void populateTable(Sheet sheet, Statement statement) throws SQLException {
        FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        DataFormatter dataFormatter = new DataFormatter();

//        System.out.println(String.format("Sheet %s has %d rows", sheet.getSheetName(), sheet.getLastRowNum()));
        Row firstRow = sheet.getRow(0);
        if (sheet.getLastRowNum() == 0) {
            return;
        }
        for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); ++rowNumber) {

            StringBuilder insertQueryBuilder = new StringBuilder("INSERT INTO \"");
            insertQueryBuilder.append(sheet.getSheetName());
            insertQueryBuilder.append("\" VALUES ");

            Row row = sheet.getRow(rowNumber);
            if (row == null || isNullRow(row)) {
                continue;
            }
            
            List<String> columnNames = columnNameMap.get(sheet.getSheetName());
            if (columnNames == null) {
                throw new SQLException("No column names for sheet " + sheet.getSheetName());
            }
//            Replace this with DatabaseMetaData info for columns 
            for (int columnIndex = 0; columnIndex < columnNames.size(); ++columnIndex) {
                Cell cell = row.getCell(columnIndex);
                if (cell == null) {
                    insertQueryBuilder.append("NULL");
                } else {
                    CellType cellType = cell.getCellTypeEnum();
                    if (cellType == FORMULA) {
                        CellValue cellValue = evaluator.evaluate(cell);
                        cellType = cellValue.getCellTypeEnum();
                    }
                    switch (cellType) {
                        case BOOLEAN:
                            insertQueryBuilder.append(cell.getBooleanCellValue());
                            break;
                        case NUMERIC:
                            insertQueryBuilder.append(cell.getNumericCellValue());
                            break;
                        case STRING:
                            String cellValue = cell.getStringCellValue();
                            cellValue = cellValue.replaceAll("\\\\", "/");
                            insertQueryBuilder.append('\'');
                            insertQueryBuilder.append(cellValue);
                            insertQueryBuilder.append('\'');
                            break;
                        case BLANK:
                        case ERROR:
                            insertQueryBuilder.append("NULL");
                            break;
                        case FORMULA:
                            throw new SQLException("Cell shouldn't still be formula");
                        default:
                            throw new SQLException("Unrecognize cell type");

                    }
                }
                insertQueryBuilder.append(',');
            }
            insertQueryBuilder.deleteCharAt(insertQueryBuilder.length() - 1);
            String insertQuery = insertQueryBuilder.toString();

            try {
                statement.executeQuery(insertQuery);
            } catch (Exception e) {
                throw new SQLException(
                        String.format("%s%nWhile executing query %s first row had %d columns",
                                e.getMessage(), insertQuery, firstRow.getLastCellNum()));
            }
        }
    }

    public void createHSQLDBFromWorkbook(Workbook workbook, Statement statement) throws SQLException {

        for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); ++sheetIndex) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            createTable(sheet, statement);
            populateTable(sheet, statement);
        }

    }
    
    protected boolean isNullRow(Row row) {
        boolean nullRow = true;

        Sheet sheet = row.getSheet();
        int numberRealColumns = columnNameMap.get(sheet.getSheetName()).size();
        for (int column = 0; column < numberRealColumns; ++column) {
            Cell cell = row.getCell(column);
            if (cell != null) {
                if (cell.getCellTypeEnum()!= BLANK) {
                    nullRow = false;
                    break;
                }
            }
        }
        return nullRow;
    }

}
