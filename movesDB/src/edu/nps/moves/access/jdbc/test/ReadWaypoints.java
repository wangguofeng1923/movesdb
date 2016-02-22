package edu.nps.moves.access.jdbc.test;

import edu.nps.moves.excel.test.ReadWaypoints.Waypoint;
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
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("edu.nps.moves.access.jdbc.AccessDBDriver");

        String inputFileName = args.length > 0 ? args[0] : "data/Waypoints.accdb";
        File inputFile = new File(inputFileName);
        System.out.println(inputFile.getAbsoluteFile() + " " + inputFile.exists());
        String url = "jdbc:access:" + inputFileName;

        Connection connection = DriverManager.getConnection(url);

        Statement statement = connection.createStatement();

//        String query = "SELECT * FROM \"Waypoints\" WHERE \"Ship\" = 'Nina' ORDER BY ID";
        String query = "SELECT * FROM \"Waypoints\" ORDER BY ID";
        ResultSet rs = statement.executeQuery(query);

        Map<String, List<edu.nps.moves.excel.test.ReadWaypoints.Waypoint>> waypoints = new HashMap<>();

        while (rs.next()) {
            String ship = rs.getString("Ship");

            List<edu.nps.moves.excel.test.ReadWaypoints.Waypoint> waypoint = waypoints.get(ship);
            if (waypoint == null) {
                waypoint = new ArrayList<>();
                waypoints.put(ship, waypoint);
            }
            double x = rs.getDouble("X");
            double y = rs.getDouble("Y");
            double speed = rs.getDouble("Speed");
            waypoint.add(new Waypoint(x, y, speed));
        }
        query = "SELECT * FROM \"Waypoints\" WHERE \"Ship\" = 'Nina' ORDER BY ID";
        rs = statement.executeQuery(query);
        
        while (rs.next()) {
            Waypoint waypoint = new Waypoint(rs.getDouble("X"), rs.getDouble("Y"), rs.getDouble("Speed"));
            System.out.printf("%s -> %s%n", rs.getString("Ship"), waypoint);
            
        }
        
        statement.close();

        for (String ship : waypoints.keySet()) {
            System.out.println(ship);
            List<edu.nps.moves.excel.test.ReadWaypoints.Waypoint> waypoint = waypoints.get(ship);
            for (int i = 0; i < waypoint.size(); ++i) {
                System.out.println("\t" + i + ": " + waypoint.get(i));
            }
        }
        connection.close();
    }

}
