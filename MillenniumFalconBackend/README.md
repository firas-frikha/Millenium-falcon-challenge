# 🌌 Millennium Falcon Backend

A Scala backend system designed to calculate the **maximum survival probability** of the Millennium Falcon, navigating across interplanetary routes while considering ship autonomy and the presence of bounty hunters.

---

## 🚀 Project Overview

The objective is to compute the optimal path for the Millennium Falcon to travel from a starting planet to a destination planet within a limited countdown of days. The survival probability is influenced by:
- The ship’s **autonomy** (maximum distance it can travel before refueling),
- **Routes** between planets (with associated travel times),
- **Bounty hunter appearances**, which reduce survival odds if encountered.

This backend applies a **Breadth-First Search (BFS)** approach to explore possible paths and determine the one with the **highest survival probability**.

---

## 🧠 Algorithm Summary

### 1. Build Graph (Adjacency Map)

The input routes are transformed into a bidirectional graph, where each node represents a planet, and edges indicate travel time between them.

### 2. BFS Traversal

The core logic uses a BFS traversal over the graph, maintaining a queue of BfsState objects. Each state holds:

planet: current location,
currentDay: current mission day,
autonomy: remaining distance the ship can travel without refueling,
survivalProbability: current survival chance (starts at 1.0 and can decrease).
At each step, the algorithm explores two possible actions:

#### 1. Travel to an adjacent planet if the ship has enough remaining autonomy for the route. 
The new state is calculated with updated day count, reduced autonomy, and potentially reduced survival probability if bounty hunters are present on the destination planet at the arrival day.
#### 2.Stay on the current planet for one extra day to refuel. 
This consumes one day but fully restores autonomy. A new state is then enqueued with updated day, full autonomy, and survival probability (which may also decrease if bounty hunters are present on the current planet during that extra day).

The algorithm continues until all possible states have been explored or discarded due to exceeding the countdown. It returns the maximum survival probability across all valid paths from the starting planet to the destination.

## 🛠️ How to Run

### Prerequisites
- Scala 2.13+
- sbt (Scala Build Tool)

### Required Environment Variables

Before starting the server, make sure to define the following environment variables:

- **`SERVER_CONFIG_PATH`**  
  Path to the `application.conf` file, which contains the server configuration (e.g. server port, host, and Akka settings).

- **`MILLENNIUM_CONFIG_PATH`**  
  Path to a JSON configuration file that includes:
    - `autonomy`: the fuel autonomy of the Millennium Falcon,
    - `routes_db`: path to the SQLite file containing the space routes,
    - `departure`: the starting planet (e.g. `Tatooine`),
    - `arrival`: the target planet (e.g. `Endor`),
    - `bounty_hunters`: an array of bounty hunter appearances (planet + day).


### 🔨 How to Compile

To compile the project without running it:

```bash
sbt compile
```

This will check for any syntax errors and ensure all dependencies are properly resolved.

### Example Run

```
export SERVER_CONFIG_PATH=/absolute/path/to/application.conf
export MILLENNIUM_CONFIG_PATH=/absolute/path/to/config.json
```

```bash
sbt run
```
When running sbt run, the project will prompt you to choose a main class. This project contains two main entry points:

#### 1 entrypoint.ServerApp 
— the HTTP server backend (select this when prompted).

#### 1entrypoint.CliApp 
— a CLI-based interface (alternative usage).
