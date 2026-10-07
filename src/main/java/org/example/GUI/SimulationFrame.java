package org.example.GUI;

import org.example.BusinessLogic.SelectionPolicy;
import org.example.BusinessLogic.SimulationManager;
import org.example.Model.Server;
import org.example.Model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationFrame extends JFrame {

    // =========================
    // COLORS
    // =========================
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color SIDEBAR = new Color(255, 255, 255);
    private static final Color PRIMARY = new Color(55, 88, 249);
    private static final Color PRIMARY_HOVER = new Color(43, 72, 220);
    private static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color COUNTER_COLOR = new Color(239, 246, 255);

    // =========================
    // INPUT COMPONENTS
    // =========================
    private final JTextField numberOfClientsTextField = createTextField("30");
    private final JTextField numberOfServersTextField = createTextField("4");
    private final JTextField timeLimitTextField = createTextField("60");
    private final JTextField minArrivalTimeTextField = createTextField("1");
    private final JTextField maxArrivalTimeTextField = createTextField("30");
    private final JTextField minServiceTimeTextField = createTextField("2");
    private final JTextField maxServiceTimeTextField = createTextField("8");
    private final JComboBox<SelectionPolicy> policyComboBox = new JComboBox<>(SelectionPolicy.values());
    private final JButton startButton = new JButton("Start Simulation");
    private final JButton resetButton = new JButton("Reset");

    // =========================
    // SIMULATION UI
    // =========================
    private final JPanel queuesPanel = new JPanel();
    private final JLabel statusLabel = new JLabel("Ready");
    private final JLabel timeLabel = new JLabel("0 s");
    private final JLabel clientsLabel = new JLabel("0");
    private final JLabel queuesLabel = new JLabel("0");
    private final JTextArea logArea = new JTextArea();
    private final List<QueuePanel> queuePanels = new ArrayList<>();

    public SimulationFrame() {
        setTitle("QueueFlow - Bank Queue Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);
        add(createMainPanel(), BorderLayout.CENTER);
        setupActions();
        setVisible(true);
    }

    // =========================================================
    // HEADER
    // =========================================================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("QueueFlow");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Bank Queue Simulation & Optimization");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_SECONDARY);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(subtitle);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        statusLabel.setForeground(SUCCESS);
        header.add(titlePanel, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);
        return header;
    }

    // =========================================================
    // SIDEBAR
    // =========================================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 1, BORDER), new EmptyBorder(20, 20, 20, 20)));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        JLabel settingsTitle = new JLabel("SIMULATION SETTINGS");
        settingsTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        settingsTitle.setForeground(TEXT_SECONDARY);
        settingsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(settingsTitle);
        sidebar.add(Box.createVerticalStrut(20));
        addField(sidebar, "Customers", numberOfClientsTextField);
        addField(sidebar, "Bank Counters", numberOfServersTextField);
        addField(sidebar, "Simulation Duration", timeLimitTextField);
        addSectionTitle(sidebar, "ARRIVAL TIME");
        JPanel arrivalPanel = createRangePanel(minArrivalTimeTextField, maxArrivalTimeTextField);
        sidebar.add(arrivalPanel);
        sidebar.add(Box.createVerticalStrut(15));
        addSectionTitle(sidebar, "SERVICE TIME");
        JPanel servicePanel = createRangePanel(minServiceTimeTextField, maxServiceTimeTextField);
        sidebar.add(servicePanel);
        sidebar.add(Box.createVerticalStrut(15));
        addSectionTitle(sidebar, "SCHEDULING STRATEGY");
        styleComboBox();
        policyComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        policyComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(policyComboBox);
        sidebar.add(Box.createVerticalGlue());
        stylePrimaryButton(startButton);
        styleSecondaryButton(resetButton);
        startButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(startButton);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(resetButton);
        return sidebar;
    }

    // =========================================================
    // MAIN PANEL
    // =========================================================
    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(BACKGROUND);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        main.add(createBankPanel(), BorderLayout.CENTER);
        main.add(createBottomPanel(), BorderLayout.SOUTH);
        return main;
    }

    // =========================================================
    // BANK VISUALIZATION
    // =========================================================
    private JPanel createBankPanel() {
        JPanel container = createCardPanel();
        container.setLayout(new BorderLayout());
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel("Bank Hall");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Live customer queues");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_SECONDARY);
        JPanel headingText = new JPanel();
        headingText.setOpaque(false);
        headingText.setLayout(new BoxLayout(headingText, BoxLayout.Y_AXIS));
        headingText.add(title);
        headingText.add(subtitle);
        heading.add(headingText, BorderLayout.WEST);
        container.add(heading, BorderLayout.NORTH);
        queuesPanel.setBackground(Color.WHITE);
        queuesPanel.setBorder(new EmptyBorder(25, 5, 10, 5));
        JScrollPane scrollPane = new JScrollPane(queuesPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        container.add(scrollPane, BorderLayout.CENTER);
        showEmptyBank();
        return container;
    }

    private void showEmptyBank() {
        queuesPanel.removeAll();
        queuesPanel.setLayout(new GridBagLayout());
        JLabel message = new JLabel("Configure the simulation and press Start");
        message.setFont(new Font("SansSerif", Font.PLAIN, 16));
        message.setForeground(TEXT_SECONDARY);
        queuesPanel.add(message);
        queuesPanel.revalidate();
        queuesPanel.repaint();
    }

    // =========================================================
    // CREATE QUEUES
    // =========================================================
    private void createQueues(int numberOfQueues) {
        queuesPanel.removeAll();
        queuePanels.clear();
        int columns = Math.min(numberOfQueues, 5);
        queuesPanel.setLayout(new GridLayout(0,columns, 15,15));
        for (int i = 0; i < numberOfQueues; i++) {
            QueuePanel queue = new QueuePanel(i + 1);
            queuePanels.add(queue);
            queuesPanel.add(queue);
        }
        queuesPanel.revalidate();
        queuesPanel.repaint();
    }

    // =========================================================
    // BOTTOM AREA
    // =========================================================
    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(15, 0));
        bottom.setOpaque(false);
        JPanel statistics = createStatisticsPanel();
        JPanel logPanel = createLogPanel();
        bottom.add(statistics, BorderLayout.WEST);
        bottom.add(logPanel, BorderLayout.CENTER);
        return bottom;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = createCardPanel();
        panel.setPreferredSize(new Dimension(330, 160));
        panel.setLayout(new GridLayout(1, 3, 10, 10));
        panel.add(createStatCard("TIME", timeLabel));
        panel.add(createStatCard("CUSTOMERS", clientsLabel));
        panel.add(createStatCard("COUNTERS", queuesLabel));
        return panel;
    }

    private JPanel createStatCard(String title, JLabel value) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        titleLabel.setForeground(TEXT_SECONDARY);
        value.setFont(new Font("SansSerif", Font.BOLD, 22));
        value.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        value.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(value);
        card.add(Box.createVerticalGlue());
        return card;
    }

    // =========================================================
    // EVENT LOG
    // =========================================================
    private JPanel createLogPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());
        JLabel title = new JLabel("Event Log");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(TEXT_PRIMARY);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setForeground(TEXT_SECONDARY);
        logArea.setBackground(Color.WHITE);
        logArea.setText("Waiting for simulation...");
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(null);
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // ACTIONS
    // =========================================================
    private void setupActions() {
        startButton.addActionListener(e -> {
            try {
                int numberOfClients = Integer.parseInt(numberOfClientsTextField.getText());
                int numberOfServers = Integer.parseInt(numberOfServersTextField.getText());
                int timeLimit = Integer.parseInt(timeLimitTextField.getText());
                int minArrivalTime = Integer.parseInt(minArrivalTimeTextField.getText());
                int maxArrivalTime = Integer.parseInt(maxArrivalTimeTextField.getText());
                int minServiceTime = Integer.parseInt(minServiceTimeTextField.getText());
                int maxServiceTime = Integer.parseInt(maxServiceTimeTextField.getText());
                if (numberOfClients <= 0 || numberOfServers <= 0 || timeLimit <= 0) {
                    showError("Values must be greater than zero.");
                    return;
                }
                if (minArrivalTime > maxArrivalTime) {
                    showError("Minimum arrival time cannot be greater than maximum arrival time.");
                    return;
                }
                if (minServiceTime > maxServiceTime) {
                    showError("Minimum service time cannot be greater than maximum service time.");
                    return;
                }
                SelectionPolicy policy = (SelectionPolicy) policyComboBox.getSelectedItem();
                createQueues(numberOfServers);
                clientsLabel.setText(String.valueOf(numberOfClients));
                queuesLabel.setText(String.valueOf(numberOfServers));
                timeLabel.setText("0 s");
                statusLabel.setText("● Simulation running");
                statusLabel.setForeground(SUCCESS);
                startButton.setEnabled(false);
                SimulationManager manager = new SimulationManager(numberOfClients, numberOfServers, timeLimit, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime, policy, this);
                Thread simulationThread = new Thread(manager);
                simulationThread.start();
            } catch (NumberFormatException ex) {
                showError("Please enter valid integer values.");
            }
        });


        resetButton.addActionListener(e -> {

            logArea.setText(
                    "Waiting for simulation..."
            );

            timeLabel.setText("0 s");
            clientsLabel.setText("0");
            queuesLabel.setText("0");

            statusLabel.setText("Ready");
            statusLabel.setForeground(SUCCESS);

            startButton.setEnabled(true);

            showEmptyBank();
        });
    }

    // =========================================================
    // CALLED BY SIMULATION MANAGER
    // =========================================================
    public void updateLog(String text) {
        SwingUtilities.invokeLater(() -> logArea.setText(text));
    }

    public void updateSimulation(String logText, List<Server> servers, int currentTime, int waitingClients) {
        List<List<TaskSnapshot>> queueSnapshots = new ArrayList<>();
        for (Server server : servers) {
            List<TaskSnapshot> queue = new ArrayList<>();
            for (Task task : server.getTasks()) {
                queue.add(new TaskSnapshot(task.getID(), task.getServiceTime()));
            }
            queueSnapshots.add(queue);
        }
        SwingUtilities.invokeLater(() -> {
            logArea.setText(logText);
            timeLabel.setText(currentTime + " s");
            for (int i = 0; i < queuePanels.size() && i < queueSnapshots.size(); i++) {
                queuePanels.get(i).setCustomers(queueSnapshots.get(i));
            }
        });
    }

    public void simulationFinished() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("● Simulation completed");
            startButton.setEnabled(true);
        });
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private JTextField createTextField(String value) {
        JTextField field = new JTextField(value);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private void addField(JPanel panel, String label, JTextField field) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fieldLabel.setForeground(TEXT_PRIMARY);
        fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(fieldLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
    }

    private void addSectionTitle(JPanel panel, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(7));
    }

    private JPanel createRangePanel(JTextField min, JTextField max) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(min);
        panel.add(max);
        return panel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), new EmptyBorder(15, 15, 15, 15)));
        return panel;
    }

    private void styleComboBox() {
        policyComboBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        policyComboBox.setBackground(Color.WHITE);
    }

    private void stylePrimaryButton(JButton button) {
        button.setText("▶  Start Simulation");
        button.setFont(new Font("SansSerif", Font.BOLD,13));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12,15,12,15));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        if (button.isEnabled())
                            button.setBackground(PRIMARY_HOVER);
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        if (button.isEnabled())
                            button.setBackground(PRIMARY);
                    }
                }
        );
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), new EmptyBorder(10, 15,10,15)));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid configuration", JOptionPane.ERROR_MESSAGE
        );
    }

    // =========================================================
    // TASKSNAPSHOT AND QUEUE VISUAL COMPONENT
    // =========================================================
    private static class TaskSnapshot {
        private final int id;
        private final int serviceTime;

        public TaskSnapshot(int id, int serviceTime) {
            this.id = id;
            this.serviceTime = serviceTime;
        }

        public int getId() {
            return id;
        }

        public int getServiceTime() {
            return serviceTime;
        }
    }

    private class QueuePanel extends JPanel {
        private final int queueNumber;

        private List<TaskSnapshot> customers = new ArrayList<>();

        public QueuePanel(int queueNumber) {
            this.queueNumber = queueNumber;
            setPreferredSize(new Dimension(170, 430));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(BORDER));
        }

        public void setCustomers(List<TaskSnapshot> customers) {
            this.customers = new ArrayList<>(customers);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int center = getWidth() / 2;

            // ==========================================
            // COUNTER
            // ==========================================
            g2.setColor(COUNTER_COLOR);
            g2.fillRoundRect(center - 55, 25, 110, 58, 15, 15);
            g2.setColor(PRIMARY);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            String counter = "COUNTER " + queueNumber;
            drawCenteredString(g2, counter, center, 48);

            // ==========================================
            // OPEN / IDLE
            // ==========================================
            boolean hasCustomers = !customers.isEmpty();
            if (hasCustomers) {
                g2.setColor(SUCCESS);
                g2.fillOval(center - 28, 60, 7,7);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                g2.drawString("SERVING",center - 16, 67);
            } else {
                g2.setColor(new Color(148,163,184));
                g2.fillOval(center - 22,60,7,7);
                g2.setFont(new Font("SansSerif", Font.BOLD,9));
                g2.drawString("IDLE",center - 10, 67);
            }

            // ==========================================
            // EMPTY QUEUE
            // ==========================================
            if (customers.isEmpty()) {
                drawEmptyQueue(g2, center);
                g2.dispose();
                return;
            }

            // ==========================================
            // CUSTOMERS
            // ==========================================
            int startY = 105;
            int verticalSpacing = 78;
            int maxVisible = Math.min(customers.size(), 4);
            for (int i = 0; i < maxVisible; i++) {
                TaskSnapshot customer = customers.get(i);
                int y = startY + i * verticalSpacing;
                boolean beingServed = i == 0;
                drawCustomer(g2, center, y, customer, beingServed);
            }

            // ==========================================
            // EXTRA CUSTOMERS
            // ==========================================
            if (customers.size() > maxVisible) {
                int hidden = customers.size() - maxVisible;
                g2.setColor(TEXT_SECONDARY);
                g2.setFont(new Font("SansSerif", Font.BOLD,11));
                drawCenteredString(g2, "+ " + hidden + " more waiting",center, startY + maxVisible * verticalSpacing);
            }

            // ==========================================
            // QUEUE COUNT
            // ==========================================
            g2.setColor(TEXT_SECONDARY);
            g2.setFont(new Font("SansSerif", Font.PLAIN,10));
            String queueInfo;
            if (customers.size() == 1) {
                queueInfo = "No customers waiting";
            } else {
                queueInfo = (customers.size() - 1) + " waiting";
            }
            drawCenteredString(g2, queueInfo, center,getHeight() - 15);
            g2.dispose();
        }

        // =====================================================
        // CUSTOMER
        // =====================================================
        private void drawCustomer(Graphics2D g2, int center, int y, TaskSnapshot customer, boolean beingServed) {
            Color personColor;
            if (beingServed) {
                personColor = PRIMARY;
            } else {
                personColor = new Color(100,116,139);
            }

            // -------------------------
            // HEAD
            // -------------------------
            g2.setColor(personColor);
            g2.fillOval(center - 9, y,18,18);

            // -------------------------
            // BODY
            // -------------------------
            g2.fillRoundRect(center - 13,y + 20,26,29,10,10);

            // -------------------------
            // ARMS
            // -------------------------
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(center - 12,y + 26,center - 20,y + 40);
            g2.drawLine(center + 12,y + 26,center + 20,y + 40);

            // -------------------------
            // LEGS
            // -------------------------
            g2.drawLine(center - 6, y + 46, center - 8, y + 58);
            g2.drawLine(center + 6, y + 46, center + 8, y + 58);

            // -------------------------
            // CUSTOMER ID
            // -------------------------
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2.setColor(TEXT_PRIMARY);
            String id = "#" + customer.getId();
            drawCenteredString(g2, id, center + 38, y + 20);

            // -------------------------
            // SERVICE TIME
            // -------------------------
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            g2.setColor(TEXT_SECONDARY);
            String serviceTime = customer.getServiceTime() + "s";
            drawCenteredString(g2, serviceTime, center + 38, y + 35);

            // -------------------------
            // SERVING LABEL
            // -------------------------
            if (beingServed) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 8));
                g2.setColor(SUCCESS);
                drawCenteredString(g2, "SERVING", center - 38, y + 30);
            }
        }

        // =====================================================
        // EMPTY QUEUE
        // =====================================================
        private void drawEmptyQueue(Graphics2D g2, int center) {
            int y = 130;
            g2.setColor(new Color(203, 213, 225));
            // head
            g2.fillOval(center - 9, y, 18, 18);
            // body
            g2.fillRoundRect(center - 13, y + 20, 26, 30, 10, 10);
            g2.setColor(TEXT_SECONDARY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            drawCenteredString(g2, "Waiting for customers", center, y + 80);
        }

        // =====================================================
        // CENTER TEXT
        // =====================================================
        private void drawCenteredString(Graphics2D g2, String text, int center, int y) {
            FontMetrics metrics = g2.getFontMetrics();
            int width = metrics.stringWidth(text);
            g2.drawString(text, center - width / 2, y);
        }
    }
}