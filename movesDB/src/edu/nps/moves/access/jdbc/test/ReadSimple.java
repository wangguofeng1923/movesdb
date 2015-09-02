/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nps.moves.access.jdbc.test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ahbuss
 */
public class ReadSimple {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        
        Class.forName("edu.nps.moves.access.jdbc.AccessDBDriver");
        
        String fileName = "data/simple.accdb";
        File file = new File(fileName);
        System.out.println(file.getAbsoluteFile() + " " + file.exists());
        
        String url = "jdbc:access:" + fileName;
        System.out.println("URL: " + url);
        Connection connection = DriverManager.getConnection(url);
        
        Statement statement = connection.createStatement();
        
        String query = "SELECT * FROM \"Ships\"";
        
        ResultSet rs= statement.executeQuery(query);
        System.out.println("Ships:");
        while (rs.next()) {
            System.out.println(rs.getString("ShipName"));
        }
        
        query = "SELECT * FROM \"Sensors\"";
        rs = statement.executeQuery(query);
        
        System.out.println("Sensors:");
        while (rs.next()) {
            int id = rs.getInt("Ship");
            ResultSet rs2 = statement.executeQuery("SELECT * from \"Ships\" WHERE ID=" + id);
            rs2.next();
            System.out.print(id);
            System.out.print('\t');
            System.out.print(rs2.getString("ShipName"));
            System.out.print('\t');
            System.out.println(rs.getDouble("maxRange"));
        }
        statement.close();
        connection.close();
    }
    
}
