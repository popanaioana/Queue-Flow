package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;
import java.util.List;

public class ShortestQueueStrategy implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        Server bestServer = servers.get(0);
        for (Server s : servers) {
            if (s.getTasks().size() < bestServer.getTasks().size()) {
                bestServer = s;
            }
        }
        bestServer.addTask(t);
    }
}