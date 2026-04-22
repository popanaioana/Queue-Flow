package org.example.Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private boolean isRunning;

    public Server() {
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
        this.isRunning = true;
    }

    public BlockingQueue<Task> getTasks() {
        return this.tasks;
    }

    public int getWaitingPeriod() {
        return this.waitingPeriod.get();
    }

    public void stop() {
        this.isRunning = false;
    }

    public void addTask(Task task) {
        tasks.add(task);
        waitingPeriod.addAndGet(task.getServiceTime());
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Task currentTask = tasks.peek();
                if (currentTask != null) {
                    int processingTime = currentTask.getServiceTime();
                    while (processingTime > 0) {
                        Thread.sleep(1000);
                        --processingTime;
                        currentTask.setServiceTime(processingTime);
                        waitingPeriod.decrementAndGet();
                        //Thread.sleep(1000);
                    }
                    tasks.poll();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
