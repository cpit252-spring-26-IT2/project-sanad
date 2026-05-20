if (-not $env:DOCKERHUB_USERNAME) {
  Write-Error 'Set DOCKERHUB_USERNAME before running this script. Example: $env:DOCKERHUB_USERNAME="ammarx4"'
  exit 1
}

docker push "${env:DOCKERHUB_USERNAME}/project-sanad-backend:latest"
docker push "${env:DOCKERHUB_USERNAME}/project-sanad-backend:v1.0.0"

docker push "${env:DOCKERHUB_USERNAME}/project-sanad-frontend:latest"
docker push "${env:DOCKERHUB_USERNAME}/project-sanad-frontend:v1.0.0"
