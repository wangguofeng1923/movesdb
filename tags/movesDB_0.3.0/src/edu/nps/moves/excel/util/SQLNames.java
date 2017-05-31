package edu.nps.moves.excel.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ahbuss
 */
public class SQLNames {

    public static final Map<Integer, String> SQL_TYPES;

    static {
        SQL_TYPES = new TreeMap<>();

        // Get all field in java.sql.Types
        Field[] fields = java.sql.Types.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                String name = fields[i].getName();
                Integer value = (Integer) fields[i].get(null);
                SQL_TYPES.put(value, name);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public static void main(String[] args) {
        for (int key : SQL_TYPES.keySet()) {
            System.out.println(key + " = " + SQL_TYPES.get(key));
        }
    }
}
