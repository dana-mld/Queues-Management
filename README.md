# Queues Management Application

Multi-threaded Java simulation for managing clients across multiple service queues.  
The system dynamically assigns clients to the optimal queue to minimize waiting time.

---

## Project Overview

This application simulates a real-world queue management system using concurrency and multithreading.

The main objective is to:
- Synchronize multiple service queues
- Dynamically assign incoming clients
- Minimize total waiting time

The project presents concepts of:
- Threads
- Synchronization
- Concurrent data handling
- Simulation modeling

---

## Key Features

- **Real-Time Multithreading:** Each queue runs on its own thread and processes clients independently.
- **Dynamic Client Generation:** Generates `N` clients with random arrival and service times within user-defined bounds.
- **Smart Dispatching:** A scheduler thread manages the simulation clock and assigns clients to the queue with the shortest waiting time.
- **Graphical User Interface:** Setup panel for simulation parameters and live queue visualization.
- **Logging System:** Generates a `log.txt` file with detailed simulation steps and queue states.

---

## Simulation Logic

Each client follows this process in order:

1. **Generation:**  
   Clients are created with unique IDs, arrival times, and service durations.

2. **Waiting State:**  
   Clients remain in a waiting pool until their arrival time matches the simulation clock.

3. **Dispatching:**  
   The scheduler assigns the client to the queue with the minimum total waiting time.

4. **Processing:**  
   The queue thread decreases the service time every simulation second.

5. **Completion:**  
   When service time reaches zero, the client leaves the system and the next client is processed.

---

## Technical Details

- **Language:** Java  
- **Concurrency:** Threads, synchronized data structures, and synchronization mechanisms  
- **Architecture Constraints:**
  - Maximum 300 lines per class
  - Maximum 30 lines per method
  - Strict Java naming conventions

---

## Input Parameters

The simulation requires:

- `N` – Number of clients  
- `Q` – Number of queues (servers)  
- Simulation Interval – Total runtime  
- Arrival Time (Min / Max)  
- Service Time (Min / Max)

---

## Performance Metrics

At the end of the simulation, the application calculates:

- Average Waiting Time  
- Average Service Time  
- Peak Hour (interval with highest number of clients in the system)

---

## Setup & Usage

1. Compile the Java source files.
2. Run the application to open the GUI.
3. Configure simulation parameters (example: `N = 50`, `Q = 5`).
4. Start the simulation.
5. Monitor queue evolution in real time.
6. Check `log.txt` for the detailed report.

---

## Academic Context

Course: Fundamental Programming Techniques  
University: Technical University of Cluj-Napoca
