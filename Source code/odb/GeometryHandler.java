package odb;

import java.sql.CallableStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Struct;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class GeometryHandler {
    
	private DBConnection connection;
	
	public GeometryHandler(DBConnection connection) {
		this.connection = connection;
	}
	
	public List<String> fetchGeometries(String query) throws SQLException {
		DatabaseHandler handler = new DatabaseHandler(connection);
        List<String> geoJsonList = new ArrayList<>();

        handler.query(query, resultSet -> {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    if (metaData.getColumnTypeName(i).equals("MDSYS.SDO_GEOMETRY")) {
                        Object sdoGeometry = resultSet.getObject(i);
                        if (sdoGeometry != null) {
                            String geoJson = convertSDOGeometry(sdoGeometry);
                            geoJsonList.add(geoJson);
                        }
                    }
                }
            }
        });
        
        return geoJsonList;
    }

	private String convertSDOGeometry(Object sdoGeometryData) throws SQLException {
	    String geoJson = null;

	    if (sdoGeometryData instanceof Struct) {
	        try (CallableStatement cstmt = connection.getConnection().prepareCall("{call ConvertToGeoJSON(?, ?)}")) {
	            cstmt.setObject(1, sdoGeometryData);
	            cstmt.registerOutParameter(2, Types.CLOB);
	            
	            cstmt.execute();
	            
	            geoJson = cstmt.getString(2);
	        }
	    } else {
	        throw new IllegalArgumentException("Provided object is not SDO_GEOMETRY");
	    }
	    
	    return geoJson;
	}
	
}
