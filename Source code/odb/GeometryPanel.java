package odb;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import javax.swing.JPanel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GeometryPanel extends JPanel {
	
    private static final long serialVersionUID = 1L;
	
    private List<Point2D.Double> points;

    public GeometryPanel() {
        this.points = new ArrayList<>();
    }

    public void setPoints(List<Point2D.Double> points) {
        this.points = points;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (points.size() < 2) return;

        double minX = points.stream().mapToDouble(Point2D.Double::getX).min().orElse(0);
        double maxX = points.stream().mapToDouble(Point2D.Double::getX).max().orElse(0);
        double minY = points.stream().mapToDouble(Point2D.Double::getY).min().orElse(0);
        double maxY = points.stream().mapToDouble(Point2D.Double::getY).max().orElse(0);

        double dataWidth = maxX - minX;
        double dataHeight = maxY - minY;

        String size = String.format("Size: %.2fx%.2f", dataWidth, dataHeight);
        g2d.drawString(size, 10, getHeight() - 5);

        double scaleFactorX = 0.9 * getWidth() / dataWidth;
        double scaleFactorY = 0.9 * getHeight() / dataHeight;
        double scaleFactor = Math.min(scaleFactorX, scaleFactorY);

        double translateX = (getWidth() - scaleFactor * dataWidth) / 2 - minX * scaleFactor;
        double translateY = (getHeight() - scaleFactor * dataHeight) / 2 - minY * scaleFactor;

        Point2D.Double prev = new Point2D.Double(points.get(0).x * scaleFactor + translateX, points.get(0).y * scaleFactor + translateY);
        for (int i = 1; i < points.size(); i++) {
            Point2D.Double curr = new Point2D.Double(points.get(i).x * scaleFactor + translateX, points.get(i).y * scaleFactor + translateY);
            g2d.drawLine((int) prev.x, (int) prev.y, (int) curr.x, (int) curr.y);
            prev = curr;
        }
    }
    
    public static List<Point2D.Double> extractDataPoints(String jsonString) {
        List<Point2D.Double> points = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject polygon = jsonObject.getJSONObject("polygon");
        JSONArray boundaries = polygon.getJSONArray("boundary");

        for (int i = 0; i < boundaries.length(); i++) {
            JSONObject boundary = boundaries.getJSONObject(i);
            JSONObject line = boundary.getJSONObject("line");
            JSONArray datapoints = line.getJSONArray("datapoints");

            for (int j = 0; j < datapoints.length(); j++) {
                JSONArray pointArray = datapoints.getJSONArray(j);
                double x = pointArray.getDouble(0);
                double y = pointArray.getDouble(1);

                Point2D.Double point = new Point2D.Double(x, y);
                points.add(point);
            }
        }

        return points;
    }
    
}