package businesslogic;

import datamodel.Server;
import datamodel.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;
    private SelectionPolicy policy;
    private ServerListener listener;

    public Scheduler(int maxNoServers, int maxTasksPerService, ServerListener listener) {
        this.servers = new ArrayList<>();
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerService;
        this.listener = listener;

        //initialising the servers (the qs)
        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server();
            server.setListener(listener);
            servers.add(server);
            Thread thread = new Thread(server); //one thread for each q
            thread.start();
        }

    }
    public void setStrategy(SelectionPolicy policy) {
        this.policy = policy;
        switch (policy) {
            case SelectionPolicy.SHORTEST_QUEUE:
                strategy = new ConcreteQueueStrategy();
                break;
            default:
                strategy = new ConcreteStrategyTime();
                break;
        }
    }
    public void changeStrategy(SelectionPolicy policy) {
        if(policy==SelectionPolicy.SHORTEST_QUEUE)
            strategy=new ConcreteQueueStrategy();
        if (policy == SelectionPolicy.SHORTEST_TIME)
            strategy=new ConcreteStrategyTime();
    }

    public int getNumberOfServers() {
        return servers.size();
    }

    public Server dispatchTask(Task t) {
        //the q us chosen based on the strategy chosen
     return strategy.addTask(servers, t);

    }

    //list of q wrapper --> list of all qs
    public List<QueueWrapper> getQueueStates() {
        List<QueueWrapper> queueStates = new ArrayList<>();
        for (Server server : servers) {
            queueStates.add(new QueueWrapper(server));
        }
        return queueStates;
    }

    public List<Server> getServers() {
        return servers;
    }

    public static class QueueWrapper {
        private List<Task> tasks;

        public QueueWrapper(Server server) {
            this.tasks = server.getTasks();
        }

        public List<Task> getTasks() {
            return tasks;
        }

        @Override
        public String toString() {
            if (tasks.isEmpty()) return "Empty";
            StringBuilder sb = new StringBuilder();
            sb.append("SERVER: ");
            //sb.append();
            for (Task t : tasks) {
                sb.append(t.getID()).append("[Arrival: ").append(t.getArrival_time())
                        .append(", Service: ").append(t.getService_time()).append("] ");
            }
            return sb.toString();
        }
    }

}
