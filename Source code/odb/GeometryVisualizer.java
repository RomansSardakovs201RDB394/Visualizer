package odb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.List;

public class GeometryVisualizer extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private final int FRAME_WIDTH = 1000;
    private final int FRAME_HEIGHT = 600;
    private DBConnection connection;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private GeometryPanel geometryPanel = new GeometryPanel();
    
    private List<String> geoJsonList;
    
    public GeometryVisualizer(DBConnection connection) {
        this.connection = connection;
        initializeUI();
        Start();
    }

    private void initializeUI() {
        setTitle("Visualization Geometric data query");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        
        // Left Side
        JPanel leftSide = new JPanel();
        leftSide.setPreferredSize(new Dimension(200, 600));
        leftSide.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        leftSide.setLayout(new BorderLayout());

        JLabel leftSideText = new JLabel("Found Elements", SwingConstants.CENTER);
        leftSideText.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        leftSideText.setFont(new Font("Arial", Font.BOLD, 14));
        leftSide.add(leftSideText, BorderLayout.NORTH);

        JList<String> list = new JList<>(listModel);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { 
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex != -1 && geoJsonList != null && selectedIndex < geoJsonList.size()) {
                    String selectedGeoJson = geoJsonList.get(selectedIndex);
                    List<Point2D.Double> points = GeometryPanel.extractDataPoints(selectedGeoJson);
                    geometryPanel.setPoints(points);
                }
            }
        });
        list.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        leftSide.add(list, BorderLayout.CENTER);
        add(leftSide, BorderLayout.WEST);

        // Right Side
        JPanel rightSide = new JPanel();
        rightSide.setLayout(new BorderLayout());

        geometryPanel.setBackground(Color.WHITE);
        geometryPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        rightSide.add(geometryPanel, BorderLayout.CENTER);

        JPanel queryView = new JPanel();
        queryView.setLayout(new BorderLayout());
        queryView.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        queryView.setPreferredSize(new Dimension(800, 150));

        JLabel queryTitle = new JLabel("Write a query to find graphical data", SwingConstants.CENTER);
        queryTitle.setFont(new Font("Arial", Font.BOLD, 14));
        queryView.add(queryTitle, BorderLayout.NORTH);

        JTextArea queryArea = new JTextArea();
        queryArea.setWrapStyleWord(true);
        queryArea.setLineWrap(true);
        queryArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane queryScrollPane = new JScrollPane(queryArea);
        queryView.add(queryScrollPane, BorderLayout.CENTER);

        JButton launchQuery = new JButton("Find");
        launchQuery.setFont(new Font("Arial", Font.BOLD, 14));
        launchQuery.addActionListener(e -> {
            String query = queryArea.getText().trim();
            if (!query.isEmpty()) {
                executeAndDisplayQuery(query);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid query!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });
        queryView.add(launchQuery, BorderLayout.SOUTH);
        rightSide.add(queryView, BorderLayout.SOUTH);
        add(rightSide, BorderLayout.CENTER);
    }
    
    public void Start() {
        this.addWindowListener(new WindowAdapter() {
             @Override
             public void windowClosing(WindowEvent e) {
                 HandleClose();
             }
         });
         setVisible(true);
    }
    
    private void HandleClose() {
        if (this.connection != null) {
            this.connection.closeConnection();
        }
        System.exit(0);
    }
    
    private void executeAndDisplayQuery(String query) {
        GeometryHandler handler = new GeometryHandler(connection);
        try {
            geoJsonList = handler.fetchGeometries(query);
            listModel.clear();
            for (int i = 0; i < geoJsonList.size(); i++) {
                listModel.addElement("Element #" + (i + 1));
            }

            if (!geoJsonList.isEmpty()) {
                String geoJson = geoJsonList.get(0);
                List<Point2D.Double> dataPoints = GeometryPanel.extractDataPoints(geoJson);
                geometryPanel.setPoints(dataPoints);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error executing the query: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}