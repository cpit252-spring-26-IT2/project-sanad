#!/usr/bin/env bash
set -euo pipefail

: "${DOCKERHUB_USERNAME:?Set DOCKERHUB_USERNAME before running this script. Example: export DOCKERHUB_USERNAME=ammarx4}"

docker push "$DOCKERHUB_USERNAME/project-sanad-backend:latest"
docker push "$DOCKERHUB_USERNAME/project-sanad-backend:v1.0.0"

docker push "$DOCKERHUB_USERNAME/project-sanad-frontend:latest"
docker push "$DOCKERHUB_USERNAME/project-sanad-frontend:v1.0.0"
