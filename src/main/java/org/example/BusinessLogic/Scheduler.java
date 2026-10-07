package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scheduler {

    private final List<Server> servers;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.servers = new ArrayList<>();
        for (int i = 0; i < maxNoServers; i++) {
            servers.add(new Server());
        }
    }

    public void changeStrategy(SelectionPolicy selectionPolicy) {
        if (selectionPolicy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ShortestQueueStrategy();
        } else if (selectionPolicy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ShortestTimeStrategy();
        } else {
            throw new IllegalArgumentException("Unsupported selection policy: " + selectionPolicy);
        }
    }

    public void dispatchTask(Task task) {
        if (strategy == null) {
            throw new IllegalStateException("A scheduling strategy must be selected before dispatching tasks.");
        }
        strategy.addTask(servers, task);
    }

    public void processOneSecond(int currentTime) {
        for (Server server : servers) {
            server.processOneSecond(currentTime);
        }
    }

    public List<Server> getServers() {
        return Collections.unmodifiableList(servers);
    }
}