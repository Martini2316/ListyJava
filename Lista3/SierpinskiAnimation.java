import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// teskt

/**
 * Program wielowątkowej animacji fraktala Sierpińskiego z 10 niezależnymi wątkami
 * Zgodny z zadaniem - każdy kwadracik to osobny wątek z priorytetem
 */
public class SierpinskiAnimation extends JFrame {
    
    // Stałe programu
    private static final int GRID_COLS = 6;
    private static final int GRID_ROWS = 4;
    private static final int CELL_WIDTH = 180;
    private static final int CELL_HEIGHT = 130;
    private static final int ANIMATION_COUNT = 24; // Zwiększamy do 24 wątków
    
    // Komponenty GUI
    private AnimationPanel animationPanel;
    private List<SierpinskiAnimationThread> animationThreads;
    private boolean animationStarted = false;
    private volatile boolean isRunning = false;
    
    // Panel konfiguracji priorytetów
    private JDialog priorityDialog;
    private JSpinner[] prioritySpinners;
    
    public SierpinskiAnimation() {
        super("Fraktal Sierpińskiego - Ustawienia");
        initializeComponents();
        setupWindow();
        createAnimationThreads();
        showPriorityDialog();
    }
    
    private void initializeComponents() {
        // Utworzenie panelu do rysowania
        animationPanel = new AnimationPanel();
        animationPanel.setPreferredSize(new Dimension(
            GRID_COLS * CELL_WIDTH, 
            GRID_ROWS * CELL_HEIGHT
        ));
        animationPanel.setBackground(Color.WHITE);
        
        // Dodanie obsługi myszy do pauzowania/wznawiania
        animationPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (animationStarted) {
                    toggleAnimation();
                }
            }
        });
    }
    
    private void setupWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Główny panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Panel informacyjny
        JLabel infoLabel = new JLabel(
            "<html><center>" +
            "24 Wątki (Testuje sobie na maczku)<br>" +
            "Każdy kwadracik = osobny wątek z priorytetem<br>" +
            "Opcje: FPS, Iter (iteracje), Load (obciążenie)<br>" +
            "Kliknij myszą aby wstrzymać/wznowić po starcie" +
            "</center></html>",
            JLabel.CENTER
        );
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(infoLabel, BorderLayout.NORTH);
        mainPanel.add(animationPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        
        // Obsługa zamykania okna
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopAllThreads();
                System.exit(0);
            }
        });
    }
    
    private void createAnimationThreads() {
        animationThreads = new ArrayList<>();
        
        // Tworzenie 10 wątków animacji
        for (int i = 0; i < ANIMATION_COUNT; i++) {
            int row = i / GRID_COLS;
            int col = i % GRID_COLS;
            
            SierpinskiAnimationThread thread = new SierpinskiAnimationThread(
                i + 1, // ID wątku (1-10)
                col * CELL_WIDTH,
                row * CELL_HEIGHT,
                CELL_WIDTH,
                CELL_HEIGHT,
                animationPanel
            );
            
            animationThreads.add(thread);
        }
    }
    
    private void showPriorityDialog() {
        priorityDialog = new JDialog(this, "Wybór Priorytetów Wątków", true);
        priorityDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel dialogPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Tytuł
        JLabel titleLabel = new JLabel("<html><center>Ustaw priorytety wątków (1-10) - Test z 24 wątkami:<br><small>Wyższy priorytet = więcej czasu CPU = wyższe FPS i więcej iteracji<br></small></center></html>");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.insets = new Insets(10, 10, 20, 10);
        dialogPanel.add(titleLabel, gbc);
        
        // Spinnery dla priorytetów
        prioritySpinners = new JSpinner[ANIMATION_COUNT];
        gbc.gridwidth = 1;
        gbc.insets = new Insets(3, 5, 3, 5);
        
        for (int i = 0; i < ANIMATION_COUNT; i++) {
            int row = (i / 6) + 1;
            int col = i % 6;
            
            // Etykieta
            JLabel label = new JLabel("W" + (i + 1) + ":");
            label.setFont(new Font("Arial", Font.PLAIN, 10));
            gbc.gridx = col * 2;
            gbc.gridy = row;
            gbc.anchor = GridBagConstraints.EAST;
            dialogPanel.add(label, gbc);
            
            // Spinner (1-10, gdzie 10 = Thread.MAX_PRIORITY)
            prioritySpinners[i] = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
            ((JSpinner.DefaultEditor) prioritySpinners[i].getEditor()).getTextField().setColumns(3);
            gbc.gridx = col * 2 + 1;
            gbc.anchor = GridBagConstraints.WEST;
            dialogPanel.add(prioritySpinners[i], gbc);
        }
        
        // Przyciski
        JPanel buttonPanel = new JPanel();
        
        JButton resetButton = new JButton("Resetuj");
        resetButton.addActionListener(e -> {
            for (int i = 0; i < prioritySpinners.length; i++) {
                prioritySpinners[i].setValue(5); // Domyślny priorytet
            }
        });
        
        JButton extremeButton = new JButton("Test Kompa z NASA xD");
        extremeButton.addActionListener(e -> {
            // Pierwsze 3 wątki MAX_PRIORITY (10), ostatnie 3 MIN_PRIORITY (1), reszta średnio (5)
            for (int i = 0; i < 3; i++) {
                prioritySpinners[i].setValue(10); // Wysokie priorytety
            }
            for (int i = 3; i < prioritySpinners.length - 3; i++) {
                prioritySpinners[i].setValue(5); // Średnie priorytety
            }
            for (int i = prioritySpinners.length - 3; i < prioritySpinners.length; i++) {
                prioritySpinners[i].setValue(1); // Niskie priorytety
            }
        });
        
        JButton stressTestButton = new JButton("Odpalenie na zajęciach");
        stressTestButton.addActionListener(e -> {
            prioritySpinners[0].setValue(10);
            for (int i = 1; i < prioritySpinners.length; i++) {
                prioritySpinners[i].setValue(1);
            }
        });
        
        JButton startButton = new JButton("Jak się nie wypierdoli to startuj!");
        startButton.addActionListener(e -> startAnimationWithPriorities());
        startButton.setBackground(Color.GREEN);
        startButton.setFont(startButton.getFont().deriveFont(Font.BOLD));
        
        buttonPanel.add(resetButton);
        buttonPanel.add(extremeButton);
        buttonPanel.add(stressTestButton);
        buttonPanel.add(startButton);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 12;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        dialogPanel.add(buttonPanel, gbc);
        
        priorityDialog.add(dialogPanel);
        priorityDialog.pack();
        priorityDialog.setLocationRelativeTo(this);
        priorityDialog.setVisible(true);
    }
    
    private void startAnimationWithPriorities() {
        // Przypisanie priorytetów wątkom
        for (int i = 0; i < animationThreads.size(); i++) {
            int priority = (Integer) prioritySpinners[i].getValue();
            animationThreads.get(i).setThreadPriority(priority);
        }
        
        // Zamknięcie dialogu i start animacji
        priorityDialog.dispose();
        animationStarted = true;
        isRunning = true;
        
        // Uruchomienie wszystkich wątków
        for (SierpinskiAnimationThread thread : animationThreads) {
            thread.start();
        }
    }
    
    private void toggleAnimation() {
        isRunning = !isRunning;
        
        for (SierpinskiAnimationThread thread : animationThreads) {
            thread.setPaused(!isRunning);
        }
    }
    
    private void stopAllThreads() {
        isRunning = false;
        
        for (SierpinskiAnimationThread thread : animationThreads) {
            thread.stopThread();
        }
        
        // Czekamy na zakończenie wszystkich wątków
        for (SierpinskiAnimationThread thread : animationThreads) {
            try {
                if (thread.isAlive()) {
                    thread.join(1000); // Maksymalnie 1 sekunda oczekiwania
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Panel do rysowania animacji
     */
    private class AnimationPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Ustawienia antyaliasingu
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (!animationStarted) {
                // Rysowanie statycznych trójkątów przed startem
                g2d.setColor(Color.BLACK);
                for (int i = 0; i < ANIMATION_COUNT; i++) {
                    int row = i / GRID_COLS;
                    int col = i % GRID_COLS;
                    int cellX = col * CELL_WIDTH;
                    int cellY = row * CELL_HEIGHT;
                    
                    // Ramka komórki
                    g2d.drawRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                    
                    // Numer wątku
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    g2d.drawString("Wątek " + (i + 1), cellX + 5, cellY + 15);
                    
                    // Statyczny trójkąt w środku
                    drawSierpinskiTriangle(g2d, cellX + CELL_WIDTH/2, cellY + CELL_HEIGHT/2, 60, 4);
                }
            } else {
                // Rysowanie animacji przez wątki
                for (SierpinskiAnimationThread thread : animationThreads) {
                    thread.draw(g2d);
                }
            }
            
            g2d.dispose();
        }
        
        private void drawSierpinskiTriangle(Graphics2D g2d, double centerX, double centerY, double size, int depth) {
            if (depth <= 0) {
                return;
            }
            
            double height = size * Math.sqrt(3) / 2;
            
            int[] xPoints = {
                (int) (centerX),
                (int) (centerX - size / 2),
                (int) (centerX + size / 2)
            };
            
            int[] yPoints = {
                (int) (centerY - height / 2),
                (int) (centerY + height / 2),
                (int) (centerY + height / 2)
            };
            
            g2d.drawPolygon(xPoints, yPoints, 3);
            
            if (depth > 1) {
                double newSize = size / 2;
                double newHeight = height / 2;
                
                drawSierpinskiTriangle(g2d, centerX, centerY - newHeight / 2, newSize, depth - 1);
                drawSierpinskiTriangle(g2d, centerX - newSize / 2, centerY + newHeight / 2, newSize, depth - 1);
                drawSierpinskiTriangle(g2d, centerX + newSize / 2, centerY + newHeight / 2, newSize, depth - 1);
            }
        }
    }
    
    /**
     * Klasa wątku animacji fraktala Sierpińskiego
     */
    private class SierpinskiAnimationThread extends Thread {
        private int id;
        private int cellX, cellY, cellWidth, cellHeight;
        private double triangleX, triangleY;
        private double velocity = 1.5;
        private boolean movingRight = true;
        private int triangleSize = 60;
        private AnimationPanel panel;
        private volatile boolean running = true;
        private volatile boolean paused = false;
        
        // Dla obliczeniowego obciążenia (symulacja złożonych obliczeń)
        private double complexCalculationResult = 0;
        private long iterationCount = 0;
        private long lastFrameTime = System.currentTimeMillis();
        private double actualFPS = 0;
        private int bounceCount = 0; // Licznik odbić od krawędzi
        
        public SierpinskiAnimationThread(int id, int cellX, int cellY, int cellWidth, int cellHeight, AnimationPanel panel) {
            super("SierpinskiThread-" + id);
            this.id = id;
            this.cellX = cellX;
            this.cellY = cellY;
            this.cellWidth = cellWidth;
            this.cellHeight = cellHeight;
            this.panel = panel;
            
            // Pozycja startowa trójkąta w środku komórki
            this.triangleX = cellX + cellWidth / 2.0;
            this.triangleY = cellY + cellHeight / 2.0;
        }
        
        public void setThreadPriority(int priority) {
            setPriority(priority);
        }
        
        public void setPaused(boolean paused) {
            this.paused = paused;
        }
        
        public void stopThread() {
            running = false;
            interrupt();
        }
        
        @Override
        public void run() {
            while (running && !isInterrupted()) {
                try {
                    if (!paused) {
                        long frameStart = System.currentTimeMillis();
                        
                        // Bardzo intensywne obliczenia (zależne od priorytetu)
                        performComplexCalculations();
                        
                        // Aktualizacja pozycji trójkąta
                        updatePosition();
                        
                        // Obliczenie rzeczywistych FPS
                        long frameTime = System.currentTimeMillis() - frameStart;
                        actualFPS = frameTime > 0 ? 1000.0 / frameTime : 0;
                        iterationCount++;
                        
                        // Odświeżenie panelu
                        SwingUtilities.invokeLater(() -> panel.repaint());
                    }
                    
                    // Bardzo krótka pauza (aby uwydatnić różnice priorytetów)
                    Thread.sleep(10); 
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        private void performComplexCalculations() {
            // BARDZO intensywne obliczenia - więcej pracy = większa rywalizacja o CPU
            int iterations = 1000 + (10 - getPriority()) * 2000; // Niższy priorytet = więcej pracy
            
            for (int i = 0; i < iterations; i++) {
                for (int j = 0; j < 50; j++) {
                    // Symulacja fraktala - intensywne obliczenia matematyczne
                    double x = (double) i / iterations;
                    double y = (double) j / 50;
                    
                    complexCalculationResult += Math.sin(x * Math.PI) * Math.cos(y * Math.PI);
                    complexCalculationResult += Math.sqrt(x * x + y * y);
                    complexCalculationResult += Math.log(1 + Math.abs(complexCalculationResult % 100));
                    
                    // Symulacja rekurencyjnych obliczeń fraktala
                    for (int k = 0; k < 5; k++) {
                        complexCalculationResult = complexCalculationResult * 0.5 + Math.sin(k);
                    }
                }
                
                // Dodatkowa praca dla wątków o niskim priorytecie
                if (getPriority() <= 3) {
                    // Jeszcze więcej obliczeń dla wątków o niskim priorytecie
                    for (int extra = 0; extra < 100; extra++) {
                        complexCalculationResult += Math.tan(extra * 0.01);
                    }
                }
            }
            
            // Upewniamy się, że wynik nie stanie się zbyt duży
            complexCalculationResult = complexCalculationResult % 10000;
        }
        
        private void updatePosition() {
            // Ruch w prawo i lewo w obrębie komórki
            if (movingRight) {
                triangleX += velocity;
                if (triangleX + triangleSize/2 >= cellX + cellWidth - 10) {
                    movingRight = false;
                    bounceCount++; // Odbicie od prawej krawędzi
                }
            } else {
                triangleX -= velocity;
                if (triangleX - triangleSize/2 <= cellX + 10) {
                    movingRight = true;
                    bounceCount++; // Odbicie od lewej krawędzi
                }
            }
        }
        
        public void draw(Graphics2D g2d) {
            // Rysowanie ramki komórki
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(cellX, cellY, cellWidth, cellHeight);
            
            // Informacje o wątku (mniejsza czcionka dla 24 wątków)
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("W" + id + " P:" + getPriority(), cellX + 3, cellY + 12);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 8));
            g2d.drawString(paused ? "STOP" : "Aktywny", cellX + 3, cellY + 25);
            g2d.drawString("Wykonania: " + iterationCount, cellX + 3, cellY + 35);
            g2d.drawString("FPS: " + String.format("%.1f", actualFPS), cellX + 3, cellY + 45);
            g2d.drawString("Load: " + String.format("%.0f", Math.abs(complexCalculationResult) % 1000), cellX + 3, cellY + 55);
            // g2d.drawString("Odbicia: " + bounceCount, cellX + 3, cellY + 65);
            
            // Kolorowy trójkąt w animacji - intensywność koloru zależy od priorytetu
            Color triangleColor = getColorForPriority(getPriority());
            
            // Dodatkowy efekt: miganie dla wątków o wysokim priorytecie
            if (getPriority() >= 8 && (iterationCount % 10 < 5)) {
                triangleColor = triangleColor.brighter();
            }
            
            g2d.setColor(triangleColor);
            
            // Rysowanie animowanego trójkąta
            drawSierpinskiTriangle(g2d, triangleX, triangleY, triangleSize, 4);
        }
        
        private Color getColorForPriority(int priority) {
            // Kolory od czerwonego (niski priorytet) do zielonego (wysoki priorytet)
            float hue = (priority - 1) / 9.0f * 0.33f; // 0.0 (czerwony) do 0.33 (zielony)
            return Color.getHSBColor(hue, 0.8f, 0.9f);
        }
        
        private void drawSierpinskiTriangle(Graphics2D g2d, double centerX, double centerY, double size, int depth) {
            if (depth <= 0) {
                return;
            }
            
            double height = size * Math.sqrt(3) / 2;
            
            int[] xPoints = {
                (int) (centerX),
                (int) (centerX - size / 2),
                (int) (centerX + size / 2)
            };
            
            int[] yPoints = {
                (int) (centerY - height / 2),
                (int) (centerY + height / 2),
                (int) (centerY + height / 2)
            };
            
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.drawPolygon(xPoints, yPoints, 3);
            
            if (depth > 1) {
                double newSize = size / 2;
                double newHeight = height / 2;
                
                drawSierpinskiTriangle(g2d, centerX, centerY - newHeight / 2, newSize, depth - 1);
                drawSierpinskiTriangle(g2d, centerX - newSize / 2, centerY + newHeight / 2, newSize, depth - 1);
                drawSierpinskiTriangle(g2d, centerX + newSize / 2, centerY + newHeight / 2, newSize, depth - 1);
            }
        }
    }
    
    public static void main(String[] args) {
        // Ustawienie wyglądu systemu
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Użyj domyślnego wyglądu jeśli nie można ustawić systemowego
        }
        
        // Uruchomienie aplikacji w wątku EDT
        SwingUtilities.invokeLater(() -> {
            new SierpinskiAnimation().setVisible(true);
        });
    }
}