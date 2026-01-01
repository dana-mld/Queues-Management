package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StopWatch extends JPanel {

    private JLabel timelabel = new JLabel();
    private JLabel imageLabel;  // Image display
    private int elapsedTime = 0;
    private int seconds = 0;
    private String secondsString = String.format("%03d", seconds);

    private Timer timer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            elapsedTime += 1000;
            seconds = elapsedTime / 1000;
            secondsString = String.format("%03d", seconds);
            timelabel.setText("Time: " + secondsString + "s");
        }
    });

    public StopWatch() {
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));


        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("stopwatch.png"));
        if (icon == null) {
            System.out.println("Image not found");
        }

        Image image = icon.getImage().getScaledInstance(40, 30, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(image);

        imageLabel = new JLabel(resizedIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        timelabel.setText("Time: " + secondsString + "s");
        timelabel.setFont(new Font("Verdana", Font.BOLD, 20));
        timelabel.setHorizontalAlignment(SwingConstants.LEFT);
        timelabel.setOpaque(true);
        timelabel.setBackground(Color.WHITE);
        timelabel.setBorder(BorderFactory.createBevelBorder(1));

        topPanel.add(imageLabel);
        topPanel.add(timelabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        add(topPanel, BorderLayout.NORTH);
    }

    public void start() {
        timer.start();
    }
    public void stop(){timer.stop();}
}
