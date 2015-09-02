package edu.nps.moves.excel.test;

import java.io.StringReader;
import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 *
 * @author ahbuss
 */
public class TestParser {

    private static CCJSqlParserManager ccjSqlParserManager = new CCJSqlParserManager();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JSQLParserException {
        String sql = "SELECT * FROM ScenarioData";
        parse(sql);
        sql = "SELECT ScenarioLength, Replications FROM ScenarioData";
        parse(sql);
//        NOTE: INDEX is a reserved word in strict SQL!
//        sql = "SELECT INDEX, SEED FROM Seeds ORDER BY INDEX";
//        parse(sql);
        sql = "SELECT X, SEED FROM Seeds ORDER BY X";
        parse(sql);
        sql = "SELECT DISTINCT Unit from Units";
        parse(sql);
        
    }

    public static void parse(String sql) throws JSQLParserException {
        System.out.println("parsing " + sql);
        System.out.println("===================");
        Statement statement = ccjSqlParserManager.parse(new StringReader(sql));
        
        System.out.println("Statement: " + statement);
        if (statement instanceof Select) {
            SelectBody selectBody = ((Select) statement).getSelectBody();
            System.out.println("SelectBody:" + selectBody);

            if (selectBody instanceof PlainSelect) {
                PlainSelect plainSelectBody = (PlainSelect) selectBody;
                FromItem fromItem = plainSelectBody.getFromItem();
                System.out.println("FromItem: " + fromItem);
                List<SelectItem> selectItems = plainSelectBody.getSelectItems();
                System.out.println("SelectItems:");
                for (SelectItem selectItem : selectItems) {
                    System.out.println("\t" + selectItem);
                }
                Distinct distinct = plainSelectBody.getDistinct();
                if (distinct != null) {
                    System.out.println("Distinct? " + distinct);
                }
            }
        }

    }

}
