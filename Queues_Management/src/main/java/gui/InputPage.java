package gui;

import businesslogic.SelectionPolicy;
import businesslogic.SimulationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InputPage extends JFrame {

    private JTextField timeLimitField;
    private JTextField maxProcessingTimeField;
    private JTextField minProcessingTimeField;
    private JTextField maxArrivalTimeField;
    private JTextField minArrivalTimeField;
    private JTextField numberOfServersField;
    private JTextField numberOfClientsField;
    private JRadioButton byTimeRadio;
    private JRadioButton byQueueRadio;
    private ButtonGroup strategyGroup;

    public InputPage() {
        setTitle("Input data");
        setSize(400, 500);
        getContentPane().setBackground(Color.LIGHT_GRAY);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(10, 2, 10, 10));
        setLocationRelativeTo(null);
        // for input fields
        timeLimitField = createLabeledField("Time Limit:");
        maxProcessingTimeField = createLabeledField("Max Processing Time:");
        minProcessingTimeField = createLabeledField("Min Processing Time:");
        maxArrivalTimeField = createLabeledField("Max Arrival Time:");
        minArrivalTimeField = createLabeledField("Min Arrival Time:");
        numberOfServersField = createLabeledField("Number of Servers:");
        numberOfClientsField = createLabeledField("Number of Clients:");


        add(new JLabel("Strategy:"));
        JPanel radioPanel = new JPanel();
        byTimeRadio = new JRadioButton("By Time");
        byQueueRadio = new JRadioButton("By Queue");
        strategyGroup = new ButtonGroup();
        strategyGroup.add(byTimeRadio);
        strategyGroup.add(byQueueRadio);
        byTimeRadio.setSelected(true);
        radioPanel.add(byTimeRadio);
        radioPanel.add(byQueueRadio);
        add(radioPanel);


        JButton submitButton = new JButton("Submit");
        add(submitButton);
        add(new JLabel());

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int timeLimit = Integer.parseInt(timeLimitField.getText());
                    int maxProcessingTime = Integer.parseInt(maxProcessingTimeField.getText());
                    int minProcessingTime = Integer.parseInt(minProcessingTimeField.getText());
                    int maxArrivalTime = Integer.parseInt(maxArrivalTimeField.getText());
                    int minArrivalTime = Integer.parseInt(minArrivalTimeField.getText());
                    int numberOfServers = Integer.parseInt(numberOfServersField.getText());
                    int numberOfClients = Integer.parseInt(numberOfClientsField.getText());
                    String strategy = byTimeRadio.isSelected() ? "By Time" : "By Queue";

                    SelectionPolicy s = strategy.equals("By Time") ?
                            SelectionPolicy.SHORTEST_TIME : SelectionPolicy.SHORTEST_QUEUE;

                    SimulationManager manager = new SimulationManager(
                            timeLimit, maxProcessingTime, minProcessingTime, maxArrivalTime, minArrivalTime,
                            numberOfServers, numberOfClients, s
                    );
                    SimulationFrame frame = new SimulationFrame( manager);
                    manager.setFrame(frame);
                    Thread simulationThread = new Thread(manager);
                    simulationThread.start();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid values.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "An error occurred: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

    private JTextField createLabeledField(String labelText) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.BLUE);
        JTextField field = new JTextField();
        add(label);
        add(field);
        return field;
    }

    public static void main(String[] args) {
        new InputPage();
    }
}
