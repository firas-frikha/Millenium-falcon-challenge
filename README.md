# Millenium-falcon-challenge

# 🌌 Millennium Falcon Challenge

Welcome to the **Millennium Falcon Challenge** — a full-stack project designed to compute the survival probability of the Millennium Falcon on a galactic journey while navigating through hostile environments and bounty hunters.

This project is a complete solution consisting of:
- A **Scala backend** to perform the survival computation using a BFS-based pathfinding algorithm.
- A **React frontend** for file upload and result visualization.
- A **Kubernetes deployment** setup with Docker images, config maps, services, and ingress routing.

---

## 🧱 Repository Structure

```
Millenium-falcon-challenge/
├── MillenniumFalconBackend/     # Scala backend service
├── MillenniumFalconFrontEnd/    # Angular frontend interface
├── Deployment/                  # Kubernetes manifests and Docker deployment setup
└── README.md                    # General project overview (this file)
```

---

## 🧠 Project Overview

The goal is to calculate the maximum survival probability of the Millennium Falcon based on:
- Start and destination planets
- Available fuel autonomy
- Routes between planets
- Bounty hunter appearances

The backend performs a breadth-first search traversal of possible routes, taking into account bounty hunter risks and refueling options, and outputs the best survival probability.

The frontend allows users to upload data configuration file and view the calculated probability interactively.

The Kubernetes deployment includes Dockerization of both services, config maps for mission settings, and ingress configuration to expose the system.

---

## 🚀 Getting Started

Each part of the project contains its own README file. Follow the instructions inside:
- [MillenniumFalconBackend](./MillenniumFalconBackend) - how to run and test the backend API or CLI.
- [MillenniumFalconFrontEnd](./MillenniumFalconFrontEnd) - how to start the Angular-based frontend.
- [Deployment](./Deployment) - how to build Docker images and deploy using Kubernetes.

---

