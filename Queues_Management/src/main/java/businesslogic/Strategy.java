package businesslogic;

import datamodel.Server;
import datamodel.Task;

import java.util.List;

public  interface Strategy {

    public Server addTask(List<Server> servers, Task t);

}
