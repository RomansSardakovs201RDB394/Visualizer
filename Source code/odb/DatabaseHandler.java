package odb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {

private DBConnection connection;

	DatabaseHandler(DBConnection connection){
		this.connection = connection;
	}
	
    public void query(String query, ResultSetProcessor processor) throws SQLException {
        try (Statement statement = connection.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
             processor.process(resultSet);
        }
    }
    
    @FunctionalInterface
    public interface ResultSetProcessor {
        void process(ResultSet resultSet) throws SQLException;
    }

}