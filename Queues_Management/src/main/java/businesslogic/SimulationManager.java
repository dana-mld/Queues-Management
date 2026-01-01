package businesslogic;

import datamodel.Server;
import datamodel.Task;
import gui.SimulationFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.io.PrintWriter;
import java.io.IOException;

/*
When multiple threads access shared resources (like variables or objects)
and at least one thread modifies the resource, there's a risk of race conditions.
synchronized makes sure only one thread can execute that critical section at a time.
 */
public class SimulationManager implements Runnable, ServerListener {
    private int timeLimit;  // total simulation time
    private int maxProcessingTime;
    private int minProcessingTime;
    private int maxArrivalTime;
    private int minArrivalTime;
    private int numberOfServers;
    private int numberOfClients;
    public SelectionPolicy selectionPolicy;
    public Scheduler scheduler;
    public SimulationFrame frame;
    public List<Task> generatedTasks;
    public List<Task> GUITasks;
   public Server currentServer = null;
    public Task currentTask = null;
    String fileName = "log.txt";
    public float avgWaitingTime=0;
    public float avgServingTime=0;
    public int peakHour;
    public  int maxTasksAtOnce=0;
    private volatile boolean running = true;

    public SimulationManager(int timeLimit, int maxProcessingTime, int minProcessingTime, int maxArrivalTime, int minArrivalTime, int numberOfServers, int numberOfClients, SelectionPolicy selectionPolicy) {
        scheduler = new Scheduler(numberOfServers, 5, this);
        this.timeLimit=timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minArrivalTime = minArrivalTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.minProcessingTime = minProcessingTime;
        this.selectionPolicy = selectionPolicy;

        scheduler.setStrategy(selectionPolicy);
        generateNRandomTask();
    }

    public void generateNRandomTask() {
        generatedTasks = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < numberOfClients; i++) {
            int arrival = rand.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int service = rand.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            Task t = new Task(arrival, service);

            generatedTasks.add(t);
        }
        // Sort tasks by arrival time
        Collections.sort(generatedTasks, Comparator.comparingInt(Task::getArrival_time));
        GUITasks=generatedTasks;
        //for service time
        calculateServiceTime(generatedTasks);
    }

public void calculateServiceTime(List<Task> tasks) {
    for(int i=0; i<numberOfClients;i++)
    {
        avgServingTime+=tasks.get(i).getService_time();
    }
}
    public void setFrame(SimulationFrame frame) {
        this.frame = frame;
    }
    public void notifyServerUpdated(Task task) {
        if (frame != null) {
            SwingUtilities.invokeLater(() -> {
                frame.updateTaskServer(task);
            });
        }
    }
    public void notifyRemoveTask(Task task) {
        if (frame != null) {
            SwingUtilities.invokeLater(() -> {
                frame.removeFromServer(task);
            });
        }
    }

    public void stopSimulation() {
        running = false;
        Server.running=false;
    }
    @Override
    public void run() {
        int currentTime = 0;

        while (running && currentTime <= timeLimit && (!generatedTasks.isEmpty() || verifyServers()>0)) {

            int finalCurrentTime = currentTime;
            //clients that arrived at the current time
            List<Task> toRemove = new ArrayList<>();
            for (Task t : generatedTasks) {
                if (t.getArrival_time() == currentTime) {

                    currentTask = t;
                    currentServer = scheduler.dispatchTask(t);
                    if(t.waiting_time==0){t.waiting_time=currentServer.getWaitingPeriod() - t.getService_time();
                    avgWaitingTime += currentServer.getWaitingPeriod() - t.getService_time();}
                    toRemove.add(t);
                }
            }
            if (maxTasksAtOnce < verifyServers()) {
                maxTasksAtOnce = verifyServers();
                peakHour = currentTime;
            }
            generatedTasks.removeAll(toRemove); //remove from the waiting lists, the tasks that had been given to some server
            fillInLog(currentTime);
            currentTime++;
            if (currentTime==timeLimit+1){running=false;break;}
                try {

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        //close the simulation
        ///either all clients have been served of the time limit has been surpassed
        if(currentTime<timeLimit)fillInLog(currentTime);
        this.frame.stopWatch.stop();
        fillStatisticsData();
        this.stopSimulation();

    }

    public void fillInLog(int currentTime)
    {
        try {
            PrintWriter writer = new PrintWriter(new java.io.FileWriter(fileName, true));

            writer.println("Time "+currentTime);
            //print the clients that are still waiting
            writer.println("Waiting tasks: ");
            for (int i = 0; i < generatedTasks.size(); i++) {
                String c = generatedTasks.get(i).toString();
                writer.println("Task " + c);
            }

            //print the qs
            writer.println("ALL SERVERS: ");
          for (int i = 0; i < scheduler.getQueueStates().size(); i++) {
              writer.println(scheduler.getQueueStates().get(i).toString());
          }
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public int verifyServers()
    {
        int cont=0;
        for (int i = 0; i < scheduler.getServers().size(); i++) {
           cont+=scheduler.getServers().get(i).getTasks().size();
        }
        return cont;
    }
  public int calculateNoOfClientsServed()
  {
      return numberOfClients-generatedTasks.size()-verifyServers();
  }
    public void calculateAvgWaitingTimeAndServingTime()
    {
        int nr=calculateNoOfClientsServed();
        if(!scheduler.getServers().isEmpty()) {
            for (int i = 0; i < scheduler.getServers().size(); i++) {
                if (!scheduler.getServers().get(i).getTasks().isEmpty()) {
                    for(int j=0;j<scheduler.getServers().get(i).getTasks().size();j++)
                    {
                        avgWaitingTime-=scheduler.getServers().get(i).getTasks().get(j).waiting_time;
                        avgServingTime-=scheduler.getServers().get(i).getTasks().get(j).getKeepServiceTime();
                    }
                }
            }
        }

        if(!generatedTasks.isEmpty())
        {
            for(int i=0;i<generatedTasks.size();i++)
            {
                avgWaitingTime-=generatedTasks.get(i).waiting_time;
                avgServingTime-=generatedTasks.get(i).getService_time();
            }
        }
        avgWaitingTime=avgWaitingTime/nr;
        avgServingTime=avgServingTime/nr;
    }

    public void fillStatisticsData()
    {
        try {
            PrintWriter writer = new PrintWriter(new java.io.FileWriter(fileName, true));

            writer.println("-----------------Raport----------------");
            calculateAvgWaitingTimeAndServingTime();
            writer.println("Average waiting time: " +avgWaitingTime);
            writer.println("Average serving time: " +avgServingTime);
            writer.println("Peak hour: " +peakHour);

            writer.flush();
            writer.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
    public void clearLogFile() {
        try {
            PrintWriter writer = new PrintWriter(fileName);
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskAdded(Task task, Server server) {
        SwingUtilities.invokeLater(() -> frame.moveTaskToServer(task, server));
    }

    @Override
    public void onTaskUpdated(Task task) {
        SwingUtilities.invokeLater(() -> notifyServerUpdated(task));
    }

    @Override
    public void onTaskRemoved(Task task) {
        SwingUtilities.invokeLater(() -> notifyRemoveTask(task));
    }

}
