package edu.nps.moves.excel.test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ahbuss
 */
public class ReadWaypoints {

    /**
     * @param args the command line arguments
     * @throws ClassNotFoundException if driver not found
     * @throws SQLException if problem with SQL query
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("edu.nps.moves.excel.jdbc.ExcelDBDriver");

        String inputFileName = args.length > 0 ? args[0] : "data/Waypoints.xlsx";
        File inputFile = new File(inputFileName);
        System.out.println(inputFile.getAbsoluteFile() + " " + inputFile.exists());
                String url = "jdbc:excel:" + inputFileName;

        Connection connection = DriverManager.getConnection(url);
        
        Statement statement = connection.createStatement();
        
//        String query = "SELECT * FROM \"Waypoints\" WHERE \"Ship\" = 'Nina' ORDER BY ID";
        String query = "SELECT * FROM Waypoints WHERE Ship = Nina ORDER BY ID";
        
//        String query = "SELECT * FROM \"Waypoints\" ORDER BY ID";
        ResultSet rs = statement.executeQuery(query);
        
        Map<String, List<Waypoint>> waypoints = new HashMap<>();
        
        while (rs.next()) {
            String ship = rs.getString("Ship");
            
            List<Waypoint> waypoint = waypoints.get(ship);
            if ( waypoint == null) {
                waypoint = new ArrayList<>();
                waypoints.put(ship, waypoint);
            }
            double x = rs.getDouble("X");
            double y = rs.getDouble("Y");
            double speed = rs.getDouble("Speed");
            waypoint.add(new Waypoint(x, y, speed));
        }
        
        statement.close();
        
        for (String ship: waypoints.keySet()) {
            System.out.println(ship);
            List<Waypoint> waypoint = waypoints.get(ship);
            for (int i = 0; i < waypoint.size(); ++i) {
                System.out.println("\t" + i + ": " + waypoint.get(i));
            }
        }
        connection.close();
    }
    
    public static class Waypoint {
        private final double x;
        private final double y;
        private final double speed;
        public Waypoint(double x, double y, double speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }
        
        @Override
        public String toString() {
            return String.format("(%.3f, %.3f) %.3f", this.x, this.y, this.speed);
        }
    }
}
