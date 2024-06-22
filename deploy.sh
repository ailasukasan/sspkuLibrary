#!/bin/bash

# Function to print messages
print_message() {
    echo ""
    echo "======================================="
    echo "$1"
    echo "======================================="
    echo ""
}

# Build and package the Spring Boot application
print_message "Building Spring Boot application..."
cd backend
mvn clean package -DskipTests

# Navigate back to the root directory
cd ..

# Build Docker images
print_message "Building Docker images..."
docker-compose build

# Run Docker containers
print_message "Starting Docker containers..."
docker-compose up -d

print_message "Deployment completed. Applications are running."
print_message "Backend: http://localhost:8080"
print_message "Frontend: http://localhost"
