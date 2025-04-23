# 🚀 Millennium Falcon Deployment Guide

This guide walks you through the complete deployment process of the Millennium Falcon project, including building Docker images, publishing to Docker Hub, setting up Kubernetes deployments, and exposing services through an ingress controller.

---

## 🧱 Configuration Maps

The required configuration maps already exist in the repository:

- `application-config.yaml`: Contains backend configuration such as Akka settings and server parameters.
- `millennium-config.yaml`: Contains mission parameters such as autonomy, database location, start and end planets, and bounty hunter details.

Ensure these files are applied before launching the deployments:

```bash
kubectl apply -f application-config.yaml
kubectl apply -f millennium-config.yaml
```

---

## 🐳 Docker Images

### 1. Build Docker Images
Go to each repository and build the Docker images:

**Backend:**
```bash
cd MillenniumFalconBackend
docker build -t your-dockerhub-username/millennium-backend .
```

**Frontend:**
```bash
cd MillenniumFalconFrontEnd
docker build -t your-dockerhub-username/millennium-frontend .
```

### 2. Push to Docker Hub
push:
```bash
docker push your-dockerhub-username/millennium-backend

docker push your-dockerhub-username/millennium-frontend
```

---

## ☸️ Kubernetes Deployments

### 1. Backend Deployment
Edit the `backend-deployment.yaml` file:
- Set the correct image name in `spec.template.spec.containers.image`
- Update the `hostPath` in the `db-file` volume to point to the actual path of your `routes.db` file on the host machine

Then deploy:
```bash
kubectl apply -f backend-deployment.yaml
```

### 2. Frontend Deployment
Edit the `frontend-deployment.yaml` file:
- Set the correct image name in `spec.template.spec.containers.image`

Then deploy:
```bash
kubectl apply -f frontend-deployment.yaml
```

---

## 📡 Services

Apply the service manifests to expose the applications within the cluster:

```bash
kubectl apply -f backend-service.yaml
kubectl apply -f frontend-service.yaml
```


## 🌐 Ingress

Deploy the ingress resource to expose the applications externally:

```bash
kubectl apply -f ingress.yaml
```