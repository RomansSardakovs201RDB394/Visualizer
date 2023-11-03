package odb;

public class Main {

	public static void main(String[] args) {
	    DBConnection connectionManager = new DBConnection();
	    GeometryVisualizer visualizer = new GeometryVisualizer(connectionManager);
	    visualizer.Start();
	}
}
