#!/bin/bash
set -e

echo "=== Quick Rebuild and Restart ==="
eval $(minikube docker-env)

# Build new image
./gradlew jibDockerBuild --image=kubernetes-informer-demo:latest -Djib.dockerClient.executable=/usr/local/bin/docker

# Restart pods to pick up new image
kubectl rollout restart deployment/kubernetes-informer-demo
kubectl rollout status deployment/kubernetes-informer-demo

echo ""
echo "=== Pods Restarted with New Image ==="
kubectl get pods -l app=kubernetes-informer-demo