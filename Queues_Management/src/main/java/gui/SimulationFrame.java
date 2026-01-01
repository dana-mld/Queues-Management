package gui;

import businesslogic.SimulationManager;
import datamodel.Server;
import datamodel.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SimulationFrame extends JFrame  {
    private final List<TaskPanel> tasks = new ArrayList<>();
    private final SimulationManager simulationManager;
    private final List<ServerPanel> servers = new ArrayList<>();
    public StopWatch stopWatch;
    private final JPanel animationPane = new JPanel(null) {
        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(new Color(0, 0, 0, 0));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    public SimulationFrame(SimulationManager manager) {
        simulationManager = manager;
        setTitle("Task Scheduler");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

         stopWatch = new StopWatch();
        mainPanel.add(stopWatch);

        JPanel taskArea = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        taskArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        taskArea.setBackground(Color.WHITE);

        for (int i = 0; i < manager.GUITasks.size(); i++) {
            TaskPanel task = new TaskPanel(manager.GUITasks.get(i));
            tasks.add(task);
            taskArea.add(task);
        }

        mainPanel.add(taskArea);

        JPanel serversPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 20));
        //sereversPanel is the one that is shown in the gui
        for (int i = 0; i < manager.scheduler.getServers().size(); i++) {
            ServerPanel sp = new ServerPanel(manager.scheduler.getServers().get(i));
            servers.add(sp);
            serversPanel.add(sp);
        }

        mainPanel.add(serversPanel);


        add(mainPanel);
        setGlassPane(animationPane);
        animationPane.setOpaque(false);
        animationPane.setVisible(true);

        stopWatch.start();
        setVisible(true);

    }

    public void moveTaskToServer(Task t, Server s) {
        TaskPanel task = null;
        ServerPanel server = null;

        for (TaskPanel tp : tasks) {
            if (tp.getTask().equals(t)) {
                task = tp;
                break;
            }
        }
        for (ServerPanel sp : servers) {
            if (sp.getServer().equals(s)) {
                server = sp;
                break;
            }
        }
        Container parent = task.getParent();
        JLayeredPane layeredPane = getLayeredPane();

        JPanel glassPane = animationPane;

        //making a deep copy
        Task tt=new Task(t.getArrival_time(), t.getService_time());
        tt.setID(t.getID());
        TaskPanel movingTask = new TaskPanel(tt);
        movingTask.setSize(task.getSize());
        movingTask.setBounds(task.getBounds());

        Point taskLocation = SwingUtilities.convertPoint(parent, task.getLocation(), glassPane);
        movingTask.setLocation(taskLocation);
        glassPane.add(movingTask);

        setGlassPane(glassPane);
        glassPane.setVisible(true);

        Point serverLocation = SwingUtilities.convertPoint(server.getParent(), server.getLocation(), glassPane);
        int verticalOffset = server.getTasksCount() * (movingTask.getHeight() + 5);
        int targetX = serverLocation.x + 10;
        int targetY = serverLocation.y + 25 + verticalOffset;

        if (targetY + movingTask.getHeight() > serverLocation.y + server.getHeight()) {
            server.setPreferredSize(new Dimension(
                    server.getWidth(),
                    targetY + movingTask.getHeight() - serverLocation.y + 10
            ));
            server.getParent().revalidate();
        }

        final int duration = 250;
        final long startTime = System.currentTimeMillis();

        parent.remove(task);
        parent.revalidate();
        parent.repaint();

        final ServerPanel finalServer = server;
        final TaskPanel finalTask = task;
        final Point finalTaskLocation = taskLocation;
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime - startTime;

                if (elapsed >= duration) {
                    glassPane.remove(movingTask);
                    glassPane.repaint();

                    ((Timer) e.getSource()).stop();
                    glassPane.setVisible(false);

                    TaskPanel newTask = new TaskPanel(t);
                    newTask.setPreferredSize(finalTask.getSize());
                    finalServer.addTask(newTask);
                } else {
                    float progress = (float) elapsed / duration;
                    progress = (float) (1 - Math.pow(1 - progress, 2));

                    int currentX = (int) (finalTaskLocation.x + (targetX - finalTaskLocation.x) * progress);
                    int currentY = (int) (finalTaskLocation.y + (targetY - finalTaskLocation.y) * progress);
                    movingTask.setLocation(currentX, currentY);
                    glassPane.repaint();
                }
            }
        });
        timer.start();
    }

    public void removeFromServer(Task t) {
        for (ServerPanel sp : servers) {
            for (Component c : sp.getComponents()) {
                if (c instanceof TaskPanel tp && tp.getTask().equals(t)) {
                    sp.removeTask(t);
                    sp.revalidate();
                    sp.repaint();
                    return;
                }
            }
        }
    }


public void updateTaskServer(Task t)
{
    for (ServerPanel sp : servers) {
        for (Component c : sp.getComponents()) {
            if (c instanceof TaskPanel tp && tp.getTask().equals(t)) {
                tp.refresh();
            }
        }
    }
}

  public class TaskPanel extends JPanel {
        private Task task;

        public TaskPanel(Task task) {
            this.task = task;
            setPreferredSize(new Dimension(80, 35));
            setBackground(Color.getHSBColor(200 / 360f, 1.0f, 1.0f));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            add(new JLabel(task.toString()));
        }

        public Task getTask() {
            return task;
        }

        @Override
        public String toString() {
            return task.toString();
        }
        public void refresh() {
            removeAll();
            add(new JLabel(task.toString()));
            revalidate();
            repaint();
        }

    }

    public class ServerPanel extends JPanel {
        private Server server;

        public ServerPanel(Server server) {
            this.server = server;
            setPreferredSize(new Dimension(200, 350));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createTitledBorder(server.toString()));
            setBackground(Color.lightGray);
        }

        public int getTasksCount() {
            int count = 0;
            for (Component c : getComponents()) {
                if (c instanceof TaskPanel) count++;
            }
            return count;
        }

        public Server getServer() {
            return server;
        }

        public void addTask(TaskPanel task) {
            add(Box.createRigidArea(new Dimension(0, 5)));
            task.setMaximumSize(task.getPreferredSize());
            add(task);
            revalidate();
            repaint();
        }
        public void removeTask(Task taskToRemove) {
            Component toRemove = null;
            Component fillerBefore = null;

            Component[] components = getComponents();
            for (int i = 0; i < components.length; i++) {
                Component comp = components[i];
                if (comp instanceof TaskPanel tp && tp.getTask().equals(taskToRemove)) {
                    toRemove = tp;

                    if (i > 0 && components[i - 1] instanceof Box.Filler) {
                        fillerBefore = components[i - 1];
                    }
                    break;
                }
            }

            if (fillerBefore != null) remove(fillerBefore);
            if (toRemove != null) remove(toRemove);

            revalidate();
            repaint();
        }

        @Override
        public String toString() {
            return "Server " + server.toString();
        }
    }

}
