package businesslogic;

import datamodel.Server;
import datamodel.Task;

import java.util.List;

public class ConcreteQueueStrategy implements Strategy {

    public ConcreteQueueStrategy() {
        super();
    }

    @Override
    public Server addTask(List<Server> servers, Task t) {
        if (servers == null || servers.isEmpty()) {
            throw new IllegalStateException("No servers available.");
        }

        //server with the shortest queue
        Server bestServer = servers.get(0);
        for (Server server : servers) {
            if (server.getTasks().size() < bestServer.getTasks().size()) {
                bestServer = server;
            }
        }


        bestServer.addTask(t);
        return bestServer;
    }
}
