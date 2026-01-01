package businesslogic;

import datamodel.Server;
import datamodel.Task;

public interface ServerListener {
    void onTaskAdded(Task task, Server server);
    void onTaskUpdated(Task task);
    void onTaskRemoved(Task task);
}