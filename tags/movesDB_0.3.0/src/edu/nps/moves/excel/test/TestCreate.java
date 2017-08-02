package edu.nps.moves.excel.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ahbuss
 */
public class TestCreate {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Pattern pattern = Pattern.compile("CREATE\\s+DATABASE\\s+\\w");
        pattern = Pattern.compile("^CREATE");
        
        String sql = "CREATE DATABASE myDB";
        
        Matcher matcher = pattern.matcher(sql);
        System.out.println(matcher.matches());
        
        System.out.println(sql.toUpperCase().startsWith("CREATE DATABASE"));
        
        if (sql.toUpperCase().startsWith("CREATE DATABASE")) {
            String dbName = sql.substring("CREATE DATABASE".length()).trim();
            System.out.println(dbName);
            String fileName = dbName + ".xlsx";
            File file = new File(fileName);
            
            Workbook workbook = new XSSFWorkbook();
            workbook.createSheet("MyTable");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                workbook.write(fileOutputStream);
                fileOutputStream.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TestCreate.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TestCreate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
