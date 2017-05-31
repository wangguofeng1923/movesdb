package edu.nps.moves.excel.util;

import edu.nps.moves.excel.jdbc.ExcelDBDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahbuss
 */
public class VersionInfo {
    

    static {
        FileInputStream inputStream = null;
        try {
            File excelPropertiesFile = new File("excel.properties");
            inputStream = new FileInputStream(excelPropertiesFile);
            Properties versionProps = new Properties();
            versionProps.load(inputStream);
            for (String key : versionProps.stringPropertyNames()) {
                System.setProperty(key, versionProps.getProperty(key));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VersionInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VersionInfo.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(VersionInfo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static int getMajorVersion() {
        return ExcelDBDriver.MAJOR_VERSION;
    }
    
    public int getMinorVersion() {
        return ExcelDBDriver.MINOR_VERSION;
    }
}
