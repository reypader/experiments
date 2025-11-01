#!/bin/bash
# View logs from all pods
kubectl logs -l app=ddb-ttl-coordination-demo --all-containers=true -f --max-log-requests=100