#!/bin/bash
set -e

echo "=== Building and Loading Image to Minikube ==="
# Jib can directly build to Minikube's Docker daemon
eval $(minikube docker-env)
./gradlew jibDockerBuild --image=ddb-ttl-coordination-demo:latest -Djib.dockerClient.executable=/usr/local/bin/docker

source ../../local_creds.env

echo ""
echo "=== Creating Secrets ==="
kubectl create secret generic aws-credentials \
  --from-literal=AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID}" \
  --from-literal=AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY}" \
  --from-literal=AWS_REGION="${AWS_REGION}" \
  --dry-run=client -o yaml | kubectl apply -f -

echo ""
echo "=== Applying Kubernetes Manifests ==="
kubectl apply -f k8s/deployment.yaml

echo ""
echo "=== Waiting for Deployment ==="
kubectl rollout status deployment/ddb-ttl-coordination-demo --timeout=120s

echo ""
echo "=== Current Status ==="
kubectl get pods -l app=ddb-ttl-coordination-demo

echo ""
echo "=== Deployment Complete ==="
echo "Scale: ./scale up|down|set <n>"
echo "Logs:  ./view-logs.sh"