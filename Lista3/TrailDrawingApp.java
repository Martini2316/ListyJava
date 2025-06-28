import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TrailDrawingApp extends JFrame implements KeyListener {
    private DrawingPanel drawingPanel;
    private JCheckBox trailCheckBox;
    private JCheckBox canWalkOnTrailCheckBox;
    private JButton saveButton;
    private JButton loadButton;
    private JButton resetButton;
    private JTextField fileNameField;
    
    public TrailDrawingApp() {
        setTitle("Aplikacja Rysowania Śladu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        initComponents();
        setupLayout();
        setupEventListeners();
        
        setFocusable(true);
        addKeyListener(this);
        
        setVisible(true);
        
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            setAlwaysOnTop(true);
            setAlwaysOnTop(false);
        });
    }
    
    private void initComponents() {
        drawingPanel = new DrawingPanel();
        trailCheckBox = new JCheckBox("Zostaw ślad", true);
        canWalkOnTrailCheckBox = new JCheckBox("Mogę wejść na ślad?", true);
        saveButton = new JButton("Zapisz");
        loadButton = new JButton("Wczytaj");
        resetButton = new JButton("Reset");
        fileNameField = new JTextField("slad.txt", 15);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Nazwa pliku:"));
        topPanel.add(fileNameField);
        topPanel.add(saveButton);
        topPanel.add(loadButton);
        topPanel.add(resetButton);
        topPanel.add(trailCheckBox);
        topPanel.add(canWalkOnTrailCheckBox);
        topPanel.add(new JLabel("Sterowanie: WSAD lub strzałki"));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });
        
        add(topPanel, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        saveButton.addActionListener(e -> {
            saveTrail();
            requestFocusInWindow();
        });
        loadButton.addActionListener(e -> {
            loadTrail();
            requestFocusInWindow();
        });
        resetButton.addActionListener(e -> {
            resetTrail();
            requestFocusInWindow();
        });
        
        fileNameField.addActionListener(e -> requestFocusInWindow());
    }
    
    private void saveTrail() {
        String fileName = fileNameField.getText().trim();
        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Podaj nazwę pliku!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            List<Point> trail = drawingPanel.getTrail();
            for (Point point : trail) {
                writer.println(point.x + "," + point.y);
            }
            JOptionPane.showMessageDialog(this, "Ślad zapisany do pliku: " + fileName, "Sukces", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas zapisywania: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadTrail() {
        String fileName = fileNameField.getText().trim();
        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Podaj nazwę pliku!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        File file = new File(fileName);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Plik nie istnieje: " + fileName, "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<Point> trail = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    trail.add(new Point(x, y));
                }
            }
            drawingPanel.setTrail(trail);
            JOptionPane.showMessageDialog(this, "Ślad wczytany z pliku: " + fileName, "Sukces", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas wczytywania: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetTrail() {
        drawingPanel.resetTrail();
        JOptionPane.showMessageDialog(this, "Ślad został zresetowany", "Reset", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        boolean moved = false;
        
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                moved = drawingPanel.movePlayer(0, -10, canWalkOnTrailCheckBox.isSelected());
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                moved = drawingPanel.movePlayer(0, 10, canWalkOnTrailCheckBox.isSelected());
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                moved = drawingPanel.movePlayer(-10, 0, canWalkOnTrailCheckBox.isSelected());
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                moved = drawingPanel.movePlayer(10, 0, canWalkOnTrailCheckBox.isSelected());
                break;
        }
        
        if (moved && trailCheckBox.isSelected()) {
            drawingPanel.addTrailPoint();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TrailDrawingApp();
        });
    }
}

class DrawingPanel extends JPanel {
    private Point playerPosition;
    private List<Point> trail;
    private final int PLAYER_SIZE = 20;
    private final int TRAIL_SIZE = 8;
    
    public DrawingPanel() {
        setBackground(Color.WHITE);
        setFocusable(false);
        playerPosition = new Point(100, 100);
        trail = new ArrayList<>();
        
        Timer refreshTimer = new Timer(50, e -> repaint());
        refreshTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.YELLOW);
        for (Point point : trail) {
            g2d.fillRect(point.x - TRAIL_SIZE/2, point.y - TRAIL_SIZE/2, TRAIL_SIZE, TRAIL_SIZE);
            g2d.setColor(Color.ORANGE);
            g2d.drawRect(point.x - TRAIL_SIZE/2, point.y - TRAIL_SIZE/2, TRAIL_SIZE, TRAIL_SIZE);
            g2d.setColor(Color.YELLOW);
        }
        
        g2d.setColor(Color.RED);
        g2d.fillRect(playerPosition.x - PLAYER_SIZE/2, playerPosition.y - PLAYER_SIZE/2, PLAYER_SIZE, PLAYER_SIZE);
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(playerPosition.x - PLAYER_SIZE/2, playerPosition.y - PLAYER_SIZE/2, PLAYER_SIZE, PLAYER_SIZE);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Pozycja: (" + playerPosition.x + ", " + playerPosition.y + ")", 10, 20);
        g2d.drawString("Punktów śladu: " + trail.size(), 10, 35);
    }
    
    public boolean movePlayer(int dx, int dy, boolean canWalkOnTrail) {
        int newX = playerPosition.x + dx;
        int newY = playerPosition.y + dy;
        
        if (newX >= PLAYER_SIZE/2 && newX <= getWidth() - PLAYER_SIZE/2 &&
            newY >= PLAYER_SIZE/2 && newY <= getHeight() - PLAYER_SIZE/2) {
            
            if (!canWalkOnTrail) {
                Point newPosition = new Point(newX, newY);
                if (isOnTrail(newPosition)) {
                    return false;
                }
            }
            
            playerPosition.x = newX;
            playerPosition.y = newY;
            return true;
        }
        return false;
    }
    
    private boolean isOnTrail(Point position) {
        for (Point trailPoint : trail) {
            double distance = Math.sqrt(Math.pow(position.x - trailPoint.x, 2) + 
                                      Math.pow(position.y - trailPoint.y, 2));
            if (distance < (PLAYER_SIZE/2 + TRAIL_SIZE/2)) {
                return true;
            }
        }
        return false;
    }
    
    public void addTrailPoint() {
        trail.add(new Point(playerPosition.x, playerPosition.y));
    }
    
    public List<Point> getTrail() {
        return new ArrayList<>(trail);
    }
    
    public void setTrail(List<Point> newTrail) {
        this.trail = new ArrayList<>(newTrail);
        repaint();
    }
    
    public void resetTrail() {
        trail.clear();
        playerPosition = new Point(100, 100);
        repaint();
    }
}