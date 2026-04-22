package org.example.GUI;

import org.example.BusinessLogic.SelectionPolicy;
import org.example.BusinessLogic.SimulationManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SimulationFrame extends JFrame {
    private JTextField numberOfClientsTextField = new JTextField("4");
    private JTextField numberOfServersTextField = new JTextField("2");
    private JTextField timeLimitTextField = new JTextField("60");
    private JTextField minArrivalTimeTextField = new JTextField("2");
    private JTextField maxArrivalTimeTextField = new JTextField("30");
    private JTextField minServiceTimeTextField = new JTextField("2");
    private JTextField maxServiceTimeTextField = new JTextField("4");
    private JComboBox<SelectionPolicy> policyComboBox = new JComboBox<>(SelectionPolicy.values());
    private JButton startButton = new JButton("Start Simulation");
    private JTextArea logArea = new JTextArea();

    public SimulationFrame() {
        this.setTitle("Queues Management Application");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        inputPanel.add(new JLabel("Number of clients:"));
        inputPanel.add(numberOfClientsTextField);
        inputPanel.add(new JLabel("Number of queues:"));
        inputPanel.add(numberOfServersTextField);
        inputPanel.add(new JLabel("Simulation Interval (MAX):"));
        inputPanel.add(timeLimitTextField);
        inputPanel.add(new JLabel("Min Arrival Time:"));
        inputPanel.add(minArrivalTimeTextField);
        inputPanel.add(new JLabel("Max Arrival Time:"));
        inputPanel.add(maxArrivalTimeTextField);
        inputPanel.add(new JLabel("Min Service Time:"));
        inputPanel.add(minServiceTimeTextField);
        inputPanel.add(new JLabel("Max Service Time:"));
        inputPanel.add(maxServiceTimeTextField);
        inputPanel.add(new JLabel("Selection Policy:"));
        inputPanel.add(policyComboBox);
        this.add(inputPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(logArea), BorderLayout.CENTER);
        this.add(startButton, BorderLayout.SOUTH);
        this.setVisible(true);
        setupActions();
    }

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
                SelectionPolicy policy = (SelectionPolicy) policyComboBox.getSelectedItem();
                SimulationManager manager = new SimulationManager(numberOfClients, numberOfServers, timeLimit, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime, policy, this);
                Thread t = new Thread(manager);
                t.start();
                startButton.setEnabled(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid integers!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void updateLog(String text) {
        SwingUtilities.invokeLater(() -> {
            logArea.setText(text);
        });
    }
}