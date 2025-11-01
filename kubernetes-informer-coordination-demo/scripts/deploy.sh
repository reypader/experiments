#!/bin/bash
set -e

echo "=== Building and Loading Image to Minikube ==="
# Jib can directly build to Minikube's Docker daemon
eval $(minikube docker-env)
./gradlew jibDockerBuild --image=kubernetes-informer-coordination-demo:latest -Djib.dockerClient.executable=/usr/local/bin/docker

echo ""
echo "=== Applying Kubernetes Manifests ==="
kubectl apply -f k8s/rbac.yaml
kubectl apply -f k8s/deployment.yaml

echo ""
echo "=== Waiting for Deployment ==="
kubectl rollout status deployment/kubernetes-informer-coordination-demo --timeout=120s

echo ""
echo "=== Current Status ==="
kubectl get pods -l app=kubernetes-informer-coordination-demo

echo ""
echo "=== Deployment Complete ==="
echo "Scale: ./scale up|down|set <n>"
echo "Logs:  ./view-logs.sh"