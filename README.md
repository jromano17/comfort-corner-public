# Comfort Corner

Welcome to Comfort Corner, distributed e-commerce app for the Comfort Corner furniture brand. This platform is designed to provide users with detailed insights for the chairs available in the Comfort Corner catalogue and to make online order requests possible. Comfort Corner admins can use this app to handle all the catalogue data and to track shipments and incomes in one place. 

🎬 Watch the full video demonstration [here](https://drive.google.com/drive/folders/1OGQ_0E_vZabq9AqHK_b_ef14jF9dPZ3v?usp=drive_link) to see the platform in action.

## Introduction

This project is a modern, distributed e-commerce platform built on Spring Boot using a microservices architecture. This README file will guide you through the setup process and provide an overview of the different microservices used in the project.

## Getting Started

### Prerequisites
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.
  
To get started with ComfortCorner, follow these steps:

1. **Clone the Repository**: Begin by creating a new empty folder on your computer (e.g., `comfort-corner-app`), open your terminal in that folder, and clone both BE and FE repositories to your local machine:
    ```bash
    git clone [https://github.com/your-username/comfort-corner-public.git](https://github.com/your-username/comfort-corner-public.git)
    git clone [https://github.com/your-username/comfort-corner-fe.git](https://github.com/your-username/comfort-corner-fe.git)
    ```

2. **Start the microservices** The docker-compose.yml file is located inside the backend repository. Navigate into the backend folder and start the containers. This will download the necessary images, build the backend and frontend containers, and start the network.
   ```bash
    docker-compose up -d --build
    ```
3. **Access the application** Once the containers are successfully running, you can access the services locally:
    
    - Frontend: http://localhost:3000
    
    - Backend API Gateway: http://localhost:8080
    
    - RabbitMQ Management UI: http://localhost:15672 (Username: guest / Password: guest)

4. **Stopping the Application**  To stop all running containers and free up your system resources, run:
    ```bash
    docker-compose down
    ```
    
## Microservices Overview

Comfort Corner is comprised of the following microservices, each serving a specific function:

1. **Discovery Server**:
    - Eureka Discovery Server is used for service discovery and registration. It helps in locating a service with its name.

2. **Config Server**:
    - Config Server centralizes external configuration for applications across all environments.

3. **Security Service**:
    - Handles user authentication and issues JWT tokens for authorized access to Comfort Corner.

4. **Product Service**:
    - Provides CRUD operations for managing catalogue information, including chairs, chair variants, color options, dimensions, materials and categories.

5. **System Events Service**:
    - Logs events and activities within the Comfort Corner platform using gRPC.

6. **Order Service**:
    - Manages order-related functionalities, including creating, updating, and retrieving orders, income records and shipments.

7. **Gateway Service**:
    - Acts as an API gateway for routing requests to the appropriate microservices. It provides centralized security.

## Contact

If you have any questions, suggestions, or feedback, please feel free to reach out to us at:
- jromano1@etf.unsa.ba

Thank you for choosing Comfort Corner! Enjoy our platform!
