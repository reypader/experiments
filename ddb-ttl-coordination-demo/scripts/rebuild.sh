#!/bin/bash
set -e

echo "=== Quick Rebuild and Restart ==="
eval $(minikube docker-env)

# Build new image
./gradlew jibDockerBuild --image=ddb-ttl-coordination-demo:latest -Djib.dockerClient.executable=/usr/local/bin/docker

# Restart pods to pick up new image
kubectl rollout restart deployment/ddb-ttl-coordination-demo
kubectl rollout status deployment/ddb-ttl-coordination-demo

echo ""
echo "=== Pods Restarted with New Image ==="
kubectl get pods -l app=ddb-ttl-coordination-demo