package businesslogic;

import datamodel.Server;
import datamodel.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {

    @Override
    public Server addTask(List<Server> servers, Task t) {
        if (servers == null || servers.isEmpty()) {
            throw new IllegalStateException("No servers available.");
        }

        // server with the shortest waiting time
        Server bestServer = servers.get(0);
        for (Server server : servers) {
            if (server.getWaitingPeriod()<bestServer.getWaitingPeriod()) {
                bestServer = server;
            }
        }


        bestServer.addTask(t);

       return bestServer;
    }
}
