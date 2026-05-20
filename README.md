# Project Sanad سند

Project Sanad is a CPIT-252 marketplace prototype for comparing building-material prices across multiple shops.

This repository now contains:
- Spring Boot Java backend API
- React + Vite + TypeScript frontend
- PostgreSQL setup with Docker Compose

## Tech Stack

### Backend
- Java 17
- Spring Boot (Web, Validation, Data JPA)
- PostgreSQL
- Flyway migrations + seed data
- JUnit / Spring Boot tests

### Frontend
- React + TypeScript + Vite
- TanStack React Query
- Wouter routing
- Tailwind UI components

## Project Structure

```text
project-sanad/
  pom.xml
  src/main/java/sa/edu/kau/fcit/cpit252/project/...
  src/main/resources/
  frontend/
    package.json
    index.html
    src/...
    vite.config.ts
  docker-compose.yml
  README.md
```

## Database Setup (PostgreSQL)

1. Start PostgreSQL:

```bash
docker compose up -d
```

2. Default dev DB values in `docker-compose.yml`:
- database: `sanad`
- username: `sanad`
- password: `sanad`
- port: `5432`

3. Override with environment variables if needed:
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`

## Backend Run

Set environment variables (optional, defaults already provided):
- `SPRING_DATASOURCE_URL` (default `jdbc:postgresql://localhost:5432/sanad`)
- `SPRING_DATASOURCE_USERNAME` (default `sanad`)
- `SPRING_DATASOURCE_PASSWORD` (default `sanad`)
- `CORS_ALLOWED_ORIGINS` (default `http://localhost:5173`)

Run backend:

```bash
mvn spring-boot:run
```

Backend base URL:
- `http://localhost:8080`

Health check:
- `GET http://localhost:8080/api/health`

## Frontend Run

From `frontend/`:

```bash
npm install
npm run dev
```

Frontend URL:
- `http://localhost:5173`

Notes:
- Vite dev proxy forwards `/api` to `http://localhost:8080`
- `BASE_PATH` and Replit-specific Vite plugins were removed for local dev simplicity

## API Overview

Base path: `/api`

### Health
- `GET /api/health`

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me` (Bearer token)

### Categories
- `GET /api/categories`

### Products / Listings
- `GET /api/products`
  - query params: `search`, `category`, `minPrice`, `maxPrice`, `availableOnly`, `minRating`, `sort`, `page`, `limit`
- `GET /api/products/{id}`
- `GET /api/products/{id}/offers?sort=price_asc|price_desc`
- `GET /api/compare?productId={id}&sort=price_asc|price_desc`

### Reviews (1-5 stars only)
- `POST /api/reviews` (Bearer token)
- `GET /api/reviews/summary?targetType=PRODUCT&targetId={id}`
- `GET /api/reviews/summary?targetType=SHOP&targetId={id}`

## Demo Accounts (Seeded)

Customer:
- email: `customer@sanad.sa`
- password: `customer123`

Shop owner:
- email: `toney@sanad.sa`
- password: `shop123`

## Seeded Demo Data

Shops:
- Toney Flooring
- Jeddah Plumbing Supplies
- Al Noor Electrical
- BuildPro Materials
- Red Sea Paints

Categories:
- Flooring
- Plumbing
- Electrical
- Paint
- Tools
- Bathroom Fixtures
- Building Materials

Products:
- Ceramic Floor Tile
- Copper Pipe
- Plastic Pipe
- Cement Bag
- Paint Bucket
- Hammer
- Bathroom Sink
- Electrical Cable

All seeded prices use SAR.

## Implemented Features

- Account registration/login with hashed passwords
- Customer and shop-owner roles
- Category tree API
- Product browsing with advanced filtering
- Price comparison endpoint (low/high sort)
- Product offers endpoint
- Reviews with 1-5 star ratings only (no text comments)
- Review summaries (average + count + visual stars)
- Frontend integration to Java backend APIs
- Global configurable CORS for frontend dev

## Pending / Prototype-Only Features

These pages remain intentionally non-final placeholders in this integration scope:
- Cart and checkout
- Orders history/details
- Support messaging workflow
- Recommendation engine
- Live inventory synchronization

## Design Patterns Used

- Factory Method: account creation for customer/shop-owner registration
- Composite: category tree composition
- Strategy: price comparison sorting (lowest/highest)
- Chain of Responsibility: advanced product filtering

The reviews feature is implemented as a straightforward service + persistence flow; no additional reviews-specific pattern is claimed.

## Docker Hub Publishing

The project publishes two custom images:
- `ammarx4/project-sanad-backend:latest`
- `ammarx4/project-sanad-backend:v1.0.0`
- `ammarx4/project-sanad-frontend:latest`
- `ammarx4/project-sanad-frontend:v1.0.0`

PostgreSQL uses the official `postgres:16` image.

Do not put Docker Hub passwords, access tokens, or private `.env` files in this repository. Run `docker login` locally and use a Docker Hub access token if Docker asks for one.

### macOS / Linux / Git Bash

```bash
export DOCKERHUB_USERNAME=ammarx4
docker login

docker build -f Dockerfile.backend \
  -t "$DOCKERHUB_USERNAME/project-sanad-backend:latest" \
  -t "$DOCKERHUB_USERNAME/project-sanad-backend:v1.0.0" \
  .

docker build -f frontend/Dockerfile \
  -t "$DOCKERHUB_USERNAME/project-sanad-frontend:latest" \
  -t "$DOCKERHUB_USERNAME/project-sanad-frontend:v1.0.0" \
  ./frontend

docker push "$DOCKERHUB_USERNAME/project-sanad-backend:latest"
docker push "$DOCKERHUB_USERNAME/project-sanad-backend:v1.0.0"

docker push "$DOCKERHUB_USERNAME/project-sanad-frontend:latest"
docker push "$DOCKERHUB_USERNAME/project-sanad-frontend:v1.0.0"
```

### Windows PowerShell

```powershell
$env:DOCKERHUB_USERNAME="ammarx4"
docker login

docker build -f Dockerfile.backend `
  -t "${env:DOCKERHUB_USERNAME}/project-sanad-backend:latest" `
  -t "${env:DOCKERHUB_USERNAME}/project-sanad-backend:v1.0.0" `
  .

docker build -f frontend/Dockerfile `
  -t "${env:DOCKERHUB_USERNAME}/project-sanad-frontend:latest" `
  -t "${env:DOCKERHUB_USERNAME}/project-sanad-frontend:v1.0.0" `
  ./frontend

docker push "${env:DOCKERHUB_USERNAME}/project-sanad-backend:latest"
docker push "${env:DOCKERHUB_USERNAME}/project-sanad-backend:v1.0.0"

docker push "${env:DOCKERHUB_USERNAME}/project-sanad-frontend:latest"
docker push "${env:DOCKERHUB_USERNAME}/project-sanad-frontend:v1.0.0"
```

### Helper Scripts

The scripts require `DOCKERHUB_USERNAME` and never store passwords or tokens.

macOS / Linux / Git Bash:

```bash
export DOCKERHUB_USERNAME=ammarx4
docker login
./scripts/docker-build.sh
./scripts/docker-push.sh
```

Windows PowerShell:

```powershell
$env:DOCKERHUB_USERNAME="ammarx4"
docker login
.\scripts\docker-build.ps1
.\scripts\docker-push.ps1
```

## Docker Compose / Portainer Stack

The stack-ready docker-compose.yml runs:
- Frontend: Nginx serving the built React app
- Backend: Spring Boot API
- Database: PostgreSQL `postgres:16`

Default host ports:
- Frontend: `5973`
- Backend health: `8284`
- PostgreSQL: `2832`

The Compose file uses `DOCKERHUB_USERNAME` for image names:
- `${DOCKERHUB_USERNAME:-ammarx4}/project-sanad-backend:latest`
- `${DOCKERHUB_USERNAME:-ammarx4}/project-sanad-frontend:latest`

### Run Locally With Docker Compose

macOS / Linux / Git Bash:

```bash
export DOCKERHUB_USERNAME=ammarx4
docker compose up -d
```

Windows PowerShell:

```powershell
$env:DOCKERHUB_USERNAME="ammarx4"
docker compose up -d
```

Useful commands:

```bash
docker compose ps
docker compose logs -f backend
docker compose logs -f frontend
docker compose down
```

Expected local URLs:
- Frontend: `http://localhost:5973`
- Backend health: `http://localhost:8284/api/health`

### Deploy in Portainer

1. Build and push the backend and frontend images to Docker Hub.
2. Create a new Stack in Portainer.
3. Paste the content of docker-compose.yml.
4. Set stack environment variables as needed:
   - `DOCKERHUB_USERNAME=ammarx4`
   - `POSTGRES_DB=sanad`
   - `POSTGRES_USER=sanad`
   - `POSTGRES_PASSWORD=<choose-a-secure-password>`
   - Optional: `FRONTEND_PORT`, `BACKEND_PORT`, `POSTGRES_HOST_PORT`, `CORS_ALLOWED_ORIGINS`
5. Deploy the stack.

## Final Docker Hub Links

Backend image:
https://hub.docker.com/r/ammarx4/project-sanad-backend

Frontend image:
https://hub.docker.com/r/ammarx4/project-sanad-frontend
