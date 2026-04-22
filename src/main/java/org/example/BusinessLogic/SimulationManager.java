package org.example.BusinessLogic;

import org.example.GUI.SimulationFrame;
import org.example.Model.Server;
import org.example.Model.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SimulationManager implements Runnable {
    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> tasks;
    private SelectionPolicy selectionPolicy;
    private int timeLimit;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int minServiceTime;
    private int maxServiceTime;
    private int numberOfServers;
    private int numberOfClients;
    private double totalWaitingTime;
    private double totalServiceTime;

    public SimulationManager(int numberOfClients, int numberOfServers, int timeLimit, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime, SelectionPolicy selectionPolicy, SimulationFrame frame) {
        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;
        this.timeLimit = timeLimit;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
        this.selectionPolicy = selectionPolicy;
        this.frame = frame;
        this.scheduler = new Scheduler(this.numberOfServers, 100);
        this.scheduler.changeStrategy(this.selectionPolicy);
        generateRandomTasks();
    }

    private void generateRandomTasks() {
        this.tasks = new ArrayList<>();
        this.totalServiceTime = 0;
        Random random = new Random();
        for (int i = 1; i <= this.numberOfClients; i++) {
            int randomArrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int randomServiceTime = random.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
            tasks.add(new Task(i, randomArrivalTime, randomServiceTime));
            this.totalServiceTime += randomServiceTime;
        }
        Collections.sort(tasks);
    }

    @Override
    public void run() {
        int currentTime = 0;
        int maxClientsAtATime = 0;
        int peakHour = 0;
        try (PrintWriter logWriter = new PrintWriter(new FileWriter("log_events.txt"))) {
            while (currentTime <= timeLimit) {
                Iterator<Task> iterator = tasks.iterator();
                while (iterator.hasNext()) {
                    Task t = iterator.next();
                    if (t.getArrivalTime() == currentTime) {
                        scheduler.dispatchTask(t);
                        iterator.remove();
                    }
                }
                int currentClientsInQueue = 0;
                for (Server s : scheduler.getServers()) {
                    int queueSize = s.getTasks().size();
                    totalWaitingTime += queueSize;
                    currentClientsInQueue += queueSize;
                }
                if (currentClientsInQueue > maxClientsAtATime) {
                    maxClientsAtATime = currentClientsInQueue;
                    peakHour = currentTime;
                }
                String logEntry = generateLogString(currentTime);
                logWriter.println(logEntry);
                frame.updateLog(logEntry);
                if (tasks.isEmpty() && areQueuesEmpty()) {
                    break;
                }
                ++currentTime;
                Thread.sleep(1000);
            }
            /*for (Server s : scheduler.getServers()) {
                s.stop();
            }*/
            double avgWaiting = calculateAverageWaiting();
            double avgService = calculateAverageServiceTime();
            String finalStats = "\nSimulation Over.\n" +
                    "Average Waiting Time: " + String.format("%.2f", avgWaiting) + "\n" +
                    "Average Service Time: " + String.format("%.2f", avgService) + "\n" +
                    "Peak Hour: " + peakHour;
            logWriter.println(finalStats);
            frame.updateLog(finalStats);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean areQueuesEmpty() {
        for (Server s : scheduler.getServers()) {
            if (!s.getTasks().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String generateLogString(int currentTime) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Time ").append(currentTime).append("\n");
        stringBuilder.append("Waiting clients: ");
        for (int i = 0; i < tasks.size(); ++i) {
            Task t = tasks.get(i);
            stringBuilder.append("(").append(t.getID()).append(", ").append(t.getArrivalTime()).append(", ").append(t.getServiceTime()).append(")");
            if (i < tasks.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("\n");
        List<Server> servers = scheduler.getServers();
        for (int i = 0; i < servers.size(); ++i) {
            stringBuilder.append("Queue ").append(i + 1).append(": ");
            if (servers.get(i).getTasks().isEmpty()) {
                stringBuilder.append("closed\n");
            } else {
                int j = 0;
                for (Task t : servers.get(i).getTasks()) {
                    stringBuilder.append("(").append(t.getID()).append(", ").append(t.getArrivalTime()).append(", ").append(t.getServiceTime()).append(")");
                    if (j < servers.get(i).getTasks().size() - 1) {
                        stringBuilder.append(", ");
                    }
                    ++j;
                }
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    private double calculateAverageWaiting() {
        if (numberOfClients == 0) {
            return 0.0;
        }
        return totalWaitingTime / numberOfClients;
    }

    private double calculateAverageServiceTime() {
        if (numberOfClients == 0) {
            return 0.0;
        }
        return totalServiceTime / numberOfClients;
    }
}