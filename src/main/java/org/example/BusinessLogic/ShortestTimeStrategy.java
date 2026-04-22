package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;
import java.util.List;

public class ShortestTimeStrategy implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        Server bestServer = servers.get(0);
        for (Server s : servers) {
            if (s.getWaitingPeriod() < bestServer.getWaitingPeriod()) {
                bestServer = s;
            }
        }
        bestServer.addTask(t);
    }
}