if (-not $env:DOCKERHUB_USERNAME) {
  Write-Error 'Set DOCKERHUB_USERNAME before running this script. Example: $env:DOCKERHUB_USERNAME="ammarx4"'
  exit 1
}

docker build -f Dockerfile.backend `
  -t "${env:DOCKERHUB_USERNAME}/project-sanad-backend:latest" `
  -t "${env:DOCKERHUB_USERNAME}/project-sanad-backend:v1.0.0" `
  .

docker build -f frontend/Dockerfile `
  -t "${env:DOCKERHUB_USERNAME}/project-sanad-frontend:latest" `
  -t "${env:DOCKERHUB_USERNAME}/project-sanad-frontend:v1.0.0" `
  ./frontend
