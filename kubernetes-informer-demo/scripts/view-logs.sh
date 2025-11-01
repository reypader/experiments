#!/bin/bash
# View logs from all pods
kubectl logs -l app=kubernetes-informer-demo --all-containers=true -f --max-log-requests=100