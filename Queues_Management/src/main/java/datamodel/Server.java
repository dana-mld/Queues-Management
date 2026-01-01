package datamodel;

import businesslogic.ServerListener;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private int id;
    private static int nr=0;
    public static boolean running = true;
    private ServerListener listener;

    public Server( ) {
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
        id=nr++;
    }

    public void addTask(Task task) {
        tasks.add(task);
        if (listener != null) {
            SwingUtilities.invokeLater(() -> listener.onTaskAdded(task, this));
        }
        waitingPeriod.addAndGet(task.getService_time());
    }
    public void setListener(ServerListener listener) {
        this.listener = listener;
    }
    public void run() {
        while (running) {
            if (waitingPeriod.get() != 0) {
                Task inProcess = tasks.peek();
                if (inProcess != null) {
                    if (!inProcess.isStarted()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        inProcess.setStarted(true);
                    } else {
                        while (inProcess.getService_time() > 1 && running) {

                            try {
                               notifyTaskUpdated(inProcess);
                                inProcess.decreseService_time();
                                waitingPeriod.addAndGet(-1);
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(running) {
                            tasks.poll(); // task done, remove it
                           notifyTaskRemoved(inProcess);
                        }
                }
            }
        }

        }
    }
    private void notifyTaskUpdated(Task task) {
        if (listener != null) {
            SwingUtilities.invokeLater(() -> listener.onTaskUpdated(task));
        }
    }

    private void notifyTaskRemoved(Task task) {
        if (listener != null) {
            SwingUtilities.invokeLater(() -> listener.onTaskRemoved(task));
        }
    }
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }
    public String toString()
    {
        return "Server " + id;
    }

}
