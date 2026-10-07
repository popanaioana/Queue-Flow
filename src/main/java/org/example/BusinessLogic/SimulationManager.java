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
    private final Scheduler scheduler;
    private final SimulationFrame frame;
    private final List<Task> tasks;
    private final List<Task> allTasks;
    private final int timeLimit;
    private final int numberOfClients;
    private double totalServiceTime;

    public SimulationManager(int numberOfClients, int numberOfServers, int timeLimit, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime, SelectionPolicy selectionPolicy, SimulationFrame frame) {
        this.numberOfClients = numberOfClients;
        this.timeLimit = timeLimit;
        this.frame = frame;
        this.scheduler = new Scheduler(numberOfServers, 100);
        this.scheduler.changeStrategy(selectionPolicy);
        this.tasks = generateRandomTasks(numberOfClients, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime);
        this.allTasks = new ArrayList<>(tasks);
    }

    private List<Task> generateRandomTasks(int numberOfClients, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime) {
        List<Task> generatedTasks = new ArrayList<>();
        Random random = new Random();
        totalServiceTime = 0;
        for (int i = 1; i <= numberOfClients; i++) {
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = random.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
            generatedTasks.add(new Task(i, arrivalTime, serviceTime));
            totalServiceTime += serviceTime;
        }
        Collections.sort(generatedTasks);
        return generatedTasks;
    }

    @Override
    public void run() {
        int currentTime = 0;
        int maxClientsAtATime = 0;
        int peakHour = 0;
        try (PrintWriter logWriter = new PrintWriter(new FileWriter("log_events.txt"))) {
            while (currentTime <= timeLimit) {
                dispatchArrivingTasks(currentTime);
                int currentClientsInQueue = calculateCurrentClientsInQueues();
                if (currentClientsInQueue > maxClientsAtATime) {
                    maxClientsAtATime = currentClientsInQueue;
                    peakHour = currentTime;
                }
                String logEntry = generateLogString(currentTime);
                logWriter.println(logEntry);
                logWriter.flush();
                frame.updateSimulation(logEntry, scheduler.getServers(), currentTime, tasks.size());
                if (tasks.isEmpty() && areQueuesEmpty()) {
                    break;
                }
                Thread.sleep(1000);
                scheduler.processOneSecond(currentTime);
                currentTime++;
            }
            double averageWaitingTime = calculateAverageWaiting();
            double averageServiceTime = calculateAverageServiceTime();
            String finalStats ="\nSimulation Over.\n" + "Average Waiting Time: " + String.format("%.2f", averageWaitingTime) + "\nAverage Service Time: " + String.format("%.2f", averageServiceTime) + "\nPeak Hour: " + peakHour;
            logWriter.println(finalStats);
            logWriter.flush();
            frame.updateLog(finalStats);
            frame.simulationFinished();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void dispatchArrivingTasks(int currentTime) {
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getArrivalTime() == currentTime) {
                scheduler.dispatchTask(task);
                iterator.remove();
            }
        }
    }

    private int calculateCurrentClientsInQueues() {
        int currentClients = 0;
        for (Server server : scheduler.getServers()) {
            currentClients += server.getTasks().size();
        }
        return currentClients;
    }

    private boolean areQueuesEmpty() {
        for (Server server : scheduler.getServers()) {
            if (!server.getTasks().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String generateLogString(int currentTime) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Time ").append(currentTime).append("\n");
        stringBuilder.append("Waiting clients: ");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            stringBuilder.append("(").append(task.getID()).append(", ").append(task.getArrivalTime()).append(", ").append(task.getServiceTime()).append(")");
            if (i < tasks.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("\n");
        List<Server> servers = scheduler.getServers();
        for (int i = 0; i < servers.size(); i++) {
            Server server = servers.get(i);
            stringBuilder.append("Queue ").append(i + 1).append(": ");
            if (server.getTasks().isEmpty()) {
                stringBuilder.append("closed");
            } else {
                int taskIndex = 0;
                for (Task task : server.getTasks()) {
                    stringBuilder.append("(").append(task.getID()).append(", ").append(task.getArrivalTime()).append(", ").append(task.getServiceTime()).append(")");
                    if (taskIndex < server.getTasks().size() - 1) {
                        stringBuilder.append(", ");
                    }
                    taskIndex++;
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private double calculateAverageWaiting() {
        if (numberOfClients == 0) {
            return 0.0;
        }
        double totalWaitingTime = 0;
        for (Task task : allTasks) {
            totalWaitingTime += task.getWaitingTime();
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