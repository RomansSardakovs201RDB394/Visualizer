package odb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private final String URL = "jdbc:oracle:thin:@localhost:1521:ora";
	private final String USERNAME = "system";
	private final String PASSWORD = "Wotplayer145";
	private Connection connection = null;
	
	DBConnection(){
		openConnection();
	}
	
	public Connection openConnection() {
		if (connection == null) {
			try {
				connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
				System.out.println("Connection successful!");
			} catch (SQLException e) {
				System.out.println("Connection failed!");
				e.printStackTrace();
			}
		}
		return connection;
	}
	
    public Connection getConnection() {
    	return connection;
    }
	
	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
				System.out.println("Connection closed!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
