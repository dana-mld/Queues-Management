package datamodel;

public class Task implements Comparable<Task> {
    private int ID;
    public static int nr_task=0;
    private int arrival_time;
    private int service_time;
    private int keepServiceTime;
    public int waiting_time=0;
    private boolean started=false;
    public int served;//not served
    public Task(int arrival_time, int service_time) {
        this.ID = ++nr_task;
        this.arrival_time = arrival_time;
        this.service_time = service_time;
        keepServiceTime = service_time;
        waiting_time=0;
        served=0;
    }
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public int getArrival_time() {
        return arrival_time;
    }
    public void setArrival_time(int arrival_time) {
        this.arrival_time = arrival_time;
    }
    public int getService_time() {
        return service_time;
    }
    public void setService_time(int service_time) {
        this.service_time = service_time;
    }
    public void decreseService_time() {
        this.service_time--;
    }
    public void increaseWaiting_time() {
        if(served==0) {
            waiting_time++;
        }

    }
    public void hasBeenServed() {
        served=1;
    }
    public int compareTo(Task other) {
        return Integer.compare(this.arrival_time, other.arrival_time);
    }
    public String toString() {
        return "(" + this.getID()+ ", " + this.arrival_time + ", " + this.service_time+")";
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
    public int getKeepServiceTime() {
        return keepServiceTime;
    }
}
