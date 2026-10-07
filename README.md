# QueueFlow

QueueFlow is a Java desktop application that simulates and visualizes customer queue management in a bank-like environment.

The application generates customers with random arrival and service times, assigns them to available service counters using different scheduling strategies, and displays the simulation in real time through a Java Swing interface.

## Features

- Real-time visualization of customer queues
- Configurable number of customers and service counters
- Custom simulation duration
- Configurable arrival and service time intervals
- Two queue management strategies:
    - **Shortest Queue** – assigns a customer to the queue with the fewest customers
    - **Shortest Time** – assigns a customer to the queue with the lowest remaining processing time
- Live simulation clock and event log
- Customer service time visualization
- Automatic queue processing
- Simulation statistics:
    - Average waiting time
    - Average service time
    - Peak hour
- Simulation events are also written to a log file

## Technologies

- Java
- Java Swing
- Maven
- Object-Oriented Programming
- Strategy Design Pattern
- Java Collections

## How It Works

Each customer is represented by a task containing:

- Customer ID
- Arrival time
- Service time

Customers are generated before the simulation begins and sorted by arrival time.

When a customer arrives, the selected scheduling strategy determines which service counter should receive the customer.

### Shortest Queue

The customer is assigned to the service counter with the smallest number of customers currently waiting or being served.

### Shortest Time

The customer is assigned to the service counter with the lowest total remaining service time.

During each simulation step, every active counter processes one second of the current customer's service time. When the service time reaches zero, the customer leaves the queue and the next customer begins service.

## Statistics

At the end of the simulation, QueueFlow calculates:

**Average Waiting Time**

The average time between a customer's arrival and the moment their service begins.

**Average Service Time**

The average initial service duration of all generated customers.

**Peak Hour**

The simulation time at which the highest number of customers were present in the service queues.

## Project Structure

```text
src/main/java/org/example/
│
├── BusinessLogic/
│   ├── Scheduler.java
│   ├── SelectionPolicy.java
│   ├── ShortestQueueStrategy.java
│   ├── ShortestTimeStrategy.java
│   ├── SimulationManager.java
│   └── Strategy.java
│
├── GUI/
│   └── SimulationFrame.java
│
├── Model/
│   ├── Server.java
│   └── Task.java
│
└── Main.java
```

## Architecture

The application separates the simulation into three main components:

- **Model** – represents customers and service counters
- **Business Logic** – handles scheduling, queue processing, simulation timing, and statistics
- **GUI** – visualizes the current state of the simulation and allows the user to configure and start simulations

The scheduling behavior is implemented using the **Strategy Design Pattern**, allowing different queue assignment algorithms to be selected without changing the core simulation logic.

## Running the Project

### Requirements

- Java JDK
- Maven

Clone the repository:

```bash
git clone https://github.com/popanaioana/queueflow.git
```

Navigate to the project directory:

```bash
cd queueflow
```

Open the project in IntelliJ IDEA or another Java IDE and run:

```text
Main.java
```

## Example Simulation

A simulation can be configured by selecting:

```text
Customers:          20
Service Counters:    4
Duration:            40 s
Arrival Time:         1–10 s
Service Time:         3–7 s
Strategy:             Shortest Time
```

The application then visualizes customers entering the queues, being processed by service counters, and leaving the system.

## Future Improvements

Possible future improvements include:

- Additional queue scheduling algorithms
- Simulation speed controls
- Exportable statistics
- Charts for queue length and waiting time
- Persistent simulation history
- Automated tests for scheduling strategies

## Author

**Ana-Ioana Pop**

Computer Science Student  
Technical University of Cluj-Napoca