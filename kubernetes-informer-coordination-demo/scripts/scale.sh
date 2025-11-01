#!/bin/bash
set -e

DEPLOYMENT_NAME="kubernetes-informer-demo"
LABEL="app=kubernetes-informer-demo"

# Get current replica count
get_current_replicas() {
    kubectl get deployment $DEPLOYMENT_NAME -o jsonpath='{.spec.replicas}' 2>/dev/null || echo "0"
}

# Display usage
usage() {
    echo "Usage: ./scale [up|down]"
    echo "  up   - Increase replicas by 1"
    echo "  down - Decrease replicas by 1 (minimum 0)"
    exit 1
}

# Check if deployment exists
if ! kubectl get deployment $DEPLOYMENT_NAME &> /dev/null; then
    echo "Error: Deployment '$DEPLOYMENT_NAME' not found"
    echo "Please deploy the application first: ./deploy.sh"
    exit 1
fi

# Get current replicas
CURRENT_REPLICAS=$(get_current_replicas)
echo "Current replicas: $CURRENT_REPLICAS"

# Process command
case "${1:-}" in
    up)
        NEW_REPLICAS=$((CURRENT_REPLICAS + 1))
        echo "Scaling up to $NEW_REPLICAS replicas..."
        kubectl scale deployment $DEPLOYMENT_NAME --replicas=$NEW_REPLICAS
        ;;
    down)
        if [ $CURRENT_REPLICAS -eq 0 ]; then
            echo "Already at 0 replicas, cannot scale down further"
            exit 0
        fi
        NEW_REPLICAS=$((CURRENT_REPLICAS - 1))
        echo "Scaling down to $NEW_REPLICAS replicas..."
        kubectl scale deployment $DEPLOYMENT_NAME --replicas=$NEW_REPLICAS
        ;;
    *)
        usage
        ;;
esac

echo ""
echo "=== Waiting for scaling to complete ==="
kubectl rollout status deployment/$DEPLOYMENT_NAME --timeout=60s

echo ""
echo "=== Current Pods ==="
kubectl get pods -l $LABEL -o wide

echo ""
echo "Done! Current replica count: $(get_current_replicas)"