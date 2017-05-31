package edu.nps.moves.excel.test;

/**
 *
 * @author ahbuss
 */
public class TestStringBuilder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE (Foo INTEGER,BAR VARCHAR(255),");
        int lastIndex = stringBuilder.lastIndexOf(",");
        System.out.println(lastIndex + " " + stringBuilder.length());
        System.out.println(stringBuilder);
        stringBuilder.replace(lastIndex, stringBuilder.length(), ")");
        System.out.println(stringBuilder);
        
        
        String fileName = "foobar.xlsx";
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        System.out.printf("File: %s extension: %s%n", fileName, extension);
    }

}
