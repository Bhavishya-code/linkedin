# LinkedIn Microservices Backend 🚀

This repository houses a highly scalable, enterprise-grade clone of the LinkedIn backend ecosystem designed using a **Microservices Architecture**. Built primarily with **Java 21** and **Spring Boot**, the ecosystem utilizes an event-driven design pattern to handle user accounts, social connections, media uploads, post generation, and asynchronous real-time notifications.

The entire system is cloud-ready and fully containerized with **Kubernetes (k8s)** for orchestration, deployment scaling, and local/production ingress routing.

---

## 🛠️ System Architecture & Tech Stack

The architecture is split into autonomous, decoupled microservices interacting via RESTful communication (via Spring Cloud Feign) and high-throughput event messaging:

### Core Frameworks & Tools
* **Core Runtime:** Java 21 & Spring Boot.
* **Service Discovery:** Netflix Eureka Server (`DiscoverServer`).
* **API Gateway:** Spring Cloud Gateway tracking security parameters and JWT validations.
* **Asynchronous Messaging Broker:** Apache Kafka orchestrating domain event triggers[cite: 3].
* **Object / Storage Providers:** Cloudinary & Google Cloud Storage (GCS) API providers[cite: 3].
* **Container Management:** Kubernetes (Pods, Deployments, Services, Ingress Controllers)[cite: 3].
* **Database Management:** Polyglot persistence layers mapping distinct service databases (PostgreSQL/Neo4j/MongoDB contexts)[cite: 3].

---

## 🏗️ Deep-Dive Microservice Component Topology

The repository is broken down into the following standalone system layers[cite: 3]:

### 1. `APIGateway`
The single entry point for all client requests[cite: 3]. Intercepts ingress traffic, orchestrates path-based routing, and executes secure JWT filtering before dispatching down to target microservices[cite: 3].
* Includes an active `AuthenticationFilter` and an internal `JwtService` payload decoder[cite: 3].

### 2. `DiscoverServer`
A centralized **Netflix Eureka Server** orchestration instance[cite: 3]. Dynamically records host endpoints and active cluster node details to offer real-time service discovery and automated client-side load balancing[cite: 3].

### 3. `userService`
Manages identity access management (IAM), registration, and baseline profile configurations[cite: 3].
* **Key Components:** `UserController`, `AuthService`, `JwtService` signature generator, and `BCrypt` password encoder[cite: 3].
* **Events:** Publishes a transaction message (`UserCreatedEvent`) to Kafka when a new profile clears the database barrier[cite: 3].

### 4. `ConnectionsService`
Orchestrates social network graphs, matching user nodes, computing invitations, and tracking social connections[cite: 3].
* Includes a `UserServiceConsumer` to ingest `UserCreatedEvent` messages and automatically insert a representing `Person` vertex into its relational query model[cite: 3].
* Employs open Feign client interceptor circuits to ensure tokens map gracefully across upstream requests[cite: 3].

### 5. `postsService`
Drives user wall feeds, content interactions, and user feedback metrics[cite: 3].
* **Key Components:** `PostController` (handling text feeds) and `PostLikesController` (managing structural like counters)[cite: 3].
* **Inter-service Feign Clients:** Communicates instantly with `ConnectionsServiceClient` and `UploaderServiceClient`[cite: 3].
* **Events:** Fires event schemas (`PostCreated`, `PostLiked`) down through Kafka to trigger telemetry alerts downstream[cite: 3].

### 6. `uploaderService`
An isolated, high-availability media ingest pipeline built to parse incoming multi-part binary file streams[cite: 3].
* **Service Engines:** Seamlessly abstracts and handles external platform storage targets via a universal `UploaderService` interface, featuring both `CloudinaryUploaderService` and `GoogleCloudStorageUploaderService` wrappers[cite: 3].

### 7. `notification-service`
An asynchronous tracking node monitoring activity across the cluster platform to distribute alerts back to the consumer base[cite: 3].
* **Subscribed Consumers:** Implements a `PostsConsumer` that processes incoming Kafka events (`PostCreated`, `PostLiked`) to construct live `Notification` entities inside the persistent pipeline[cite: 3].

---

## 🗂️ Distributed Data Flow & Event Pipelines

[ Client Request ]
│
▼
┌──────────────┐
│  APIGateway  │ ───► ( Validates JWT and routes request context )[cite: 3]
└──────┬───────┘
│
├─────────────────────────┬─────────────────────────┐
▼                         ▼                         ▼
┌──────────────┐          ┌──────────────┐          ┌──────────────┐
│ userService  │          │ postsService │          │ connections  │
└──────┬───────┘          └──────┬───────┘          └──────▲───────┘
│                         │                         │
│ (UserCreatedEvent)      │ (PostCreated / Liked)   │ Ingests profile to
▼                         ▼                         │ create sync node[cite: 3]
⚡ Kafka Topic ────────────► ⚡ Kafka Topic ───────────────┘
│
└─────────────────────────┐
▼
┌──────────────┐
│ notification │ ───► ( Generates real-time alerts )[cite: 3]
└──────────────┘


---

## ☸️ Kubernetes (k8s) Deployment & Architecture

All resource runtime configurations are located within the `/k8s` cluster folder, separating microservices into individual database backends and application routing blocks[cite: 3]:

* **Infrastructure Core:** `kafka.yml`, `ingress.yml`[cite: 3].
* **Microservices Implementations:** `user-service.yml`, `posts-service.yml`, `connections-service.yml`, `notification-service.yml`, `uploader-service.yml`, `api-gateway.yml`[cite: 3].
* **Dedicated DB Layering Pods:** `user-db.yml`, `posts-db.yml`, `connections-db.yml`, `notification-db.yml`[cite: 3].

---

## 🚀 Local Deployment Instructions

### Prerequisites
* **Java Development Kit (JDK):** Version 21[cite: 3].
* **Container Engine & Orchestrator:** Docker Desktop with an active Kubernetes cluster enabled (or Minikube / k3s)[cite: 3].

### Setup Steps

1. **Clone the Core Ecosystem Repository:**
```bash
   git clone [https://github.com/your-username/linkedin-microservices.git](https://github.com/your-username/linkedin-microservices.git)
   cd linkedin-microservices
Compile and Package Individual Microservices Target Jars:
Execute compilation across the standard platform wrappers (mvnw) located within each distinct service directory[cite: 3]:

Bash
   cd userService && ./mvnw clean package -DskipTests && cd ..
   cd postsService && ./mvnw clean package -DskipTests && cd ..
   # Repeat across APIGateway, ConnectionsService, DiscoverServer, notification-service, uploader-service[cite: 3]
Deploy the Complete Environment to Kubernetes Stack:
Apply the entire orchestration configuration using kubectl[cite: 3]:

Bash
   kubectl apply -f k8s/
Verify Your Cluster Workloads:
Ensure your service nodes and backend databases have initialized securely[cite: 3]:

Bash
   kubectl get pods -w
