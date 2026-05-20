#!/usr/bin/env bash
set -euo pipefail

: "${DOCKERHUB_USERNAME:?Set DOCKERHUB_USERNAME before running this script. Example: export DOCKERHUB_USERNAME=ammarx4}"

docker build -f Dockerfile.backend \
  -t "$DOCKERHUB_USERNAME/project-sanad-backend:latest" \
  -t "$DOCKERHUB_USERNAME/project-sanad-backend:v1.0.0" \
  .

docker build -f frontend/Dockerfile \
  -t "$DOCKERHUB_USERNAME/project-sanad-frontend:latest" \
  -t "$DOCKERHUB_USERNAME/project-sanad-frontend:v1.0.0" \
  ./frontend
