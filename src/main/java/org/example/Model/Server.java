package org.example.Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private final BlockingQueue<Task> tasks;
    private final AtomicInteger waitingPeriod;

    public Server() {
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }

    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        tasks.add(task);
        waitingPeriod.addAndGet(task.getServiceTime());
    }

    public void processOneSecond(int currentTime) {
        Task currentTask = tasks.peek();
        if (currentTask == null) {
            return;
        }
        currentTask.setServiceStartTime(currentTime);
        int remainingTime = currentTask.getServiceTime() - 1;
        currentTask.setServiceTime(remainingTime);
        waitingPeriod.decrementAndGet();
        if (remainingTime <= 0) {
            tasks.poll();
        }
    }
}