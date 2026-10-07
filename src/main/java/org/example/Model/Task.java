package org.example.Model;

public class Task implements Comparable<Task> {

    private final int ID;
    private final int arrivalTime;
    private final int initialServiceTime;

    private int serviceTime;
    private int serviceStartTime = -1;

    public Task(int ID, int arrivalTime, int serviceTime) {
        this.ID = ID;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.initialServiceTime = serviceTime;
    }

    public int getID() {
        return ID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public int getInitialServiceTime() {
        return initialServiceTime;
    }

    public int getServiceStartTime() {
        return serviceStartTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public void setServiceStartTime(int serviceStartTime) {
        if (this.serviceStartTime == -1) {
            this.serviceStartTime = serviceStartTime;
        }
    }

    public int getWaitingTime() {
        if (serviceStartTime == -1) {
            return 0;
        }
        return serviceStartTime - arrivalTime;
    }

    @Override
    public int compareTo(Task task) {
        return Integer.compare(this.arrivalTime, task.arrivalTime);
    }

    @Override
    public String toString() {
        return ID + " " + arrivalTime + " " + serviceTime + "\n";
    }
}