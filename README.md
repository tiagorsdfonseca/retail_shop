# retail_shop

Repository Name: retail-shop-project

![Build Status Catalog CI](https://github.com/tiagorsdfonseca/retail_shop/actions/workflows/catalog-ci.yaml/badge.svg)
![Build Status Order CI](https://github.com/tiagorsdfonseca/retail_shop/actions/workflows/order-ci.yaml/badge.svg)


# Retail Shop Template

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://events.development.outlook.com)
[![Spring Boot 4](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=flat&logo=spring&logoColor=white)](https://shields.io/)

This repository contains the microservices engine for the **Retail Shop** project. The ecosystem demonstrates a resilitent, distributed e-commerce architecture showcasing polyglot persistence, with native Spring HTTP interfaces, assynchronous event streaming, and automated CI/CD pipeline structures.

## Core Architecture Highlights
* **Polyglot Persistence:** Optimized data layers using **MongoDB** for highly fluid, polymorphic catalog schemas alongside **PostgreSQL** for strict, ACID-compliant transactional order workflows.
* **Modern Inter-Service Proxies:** Synchronous inter-service communication utilizes natives Spring 6 `@HttpExchange` client factories built over `RestClient`.
* **Asynchronous Event-Driven:** Decoupled architecture integrating **Apache Kafka** templates optimized for structured transactional event emission ('OrderCreatedEvent').

---

## Development Environment

Ensure you have the following installed on your local machine:
* **Java Development Kit (JDK) 21**
* **Apache Maven 3.9+**
* An active Docker environment


## Useful commands

### Verify Java 21 Environment
Confirm that your shell is pointing to your active Java 21 installation:
```bash
java -version
mvn -version
```

### Build and Install Dependencies

```bash
mvn clean install
```

### Execute Test Suites

```bash
mvn clean test
```

## Build & Package Modules Locally
```bash
mvn clean package -DskipTests
```

## Orchestrate the Cluster via Docker Compose

```bash
docker compose down
docker compose up --build
```

## Project layout

The codebase is split into two autonomous microservices: **catalog** and **order**.
* **Catalog Service:** Manages inventory definitions, schema-agnostic product specifications, and promotional logic backed by MongoDB.
* **Order Service:** Evaluates customer transaction schemas, authenticates external financial gateways, streams transactional logs via Kafka, and records audit history inside PostgreSQL.

## Catalog Architecture

```
Dockerfile
src/
├── main/
|   ├── java.com.example.catalog/
|   |   ├── CatalogApplication.java
|   |   ├── controller/
|   |   |  ├── CatalogController.java
|   |   |  ├── PromotionController.java 
|   |   ├── model/
|   |   |   ├── Product.java
|   |   ├── repository/
|   |   |   ├── ProductRepository.java
|   |   ├── service/
|   |   |   ├── CatalogService.java
|   ├── resources/
|   |   ├── application.properties
|   |   ├── application-prod.properties
|   |   ├── bdd/
|   |   |   ├── steps/
|   |   |   |   ├── inventory_management.feature
|   |   |   |   ├── promotional_pricing.feature
├── test/
|   ├── java.com.example.catalog/
|   |   ├── CatalogApplicationTests.java
|   |   ├── bdd/
|   |   |   ├── steps/
|   |   |   |   ├── InventorySteps.java
|   |   |   |   ├── PromotionSteps.java
|   |   |   ├── CucumberSpringConfiguration.java
|   |   |   ├── CucumberTestRunner.java
|   ├── resources
|   |   ├── cucumber.properties 
|   |   ├── application-test.properties 
```

## Order Architecture

```
Dockerfile
src/
├── main/
|   ├── java.com.example.order/
|   |   ├── OrderApplication.java
|   |   ├── client/
|   |   |   ├── CatalogClient.java
|   |   |   ├── CatalogResponse.java
|   |   |   ├── PaymentClient.java
|   |   ├── config/
|   |   |   ├── ClientConfig.java
|   |   |   ├── KafkaConfig.java
|   |   ├── consumer/
|   |   |   ├── OrderTransactionConsumer.java
|   |   ├── controller/
|   |   |  ├── OrderController.java
|   |   ├── dto/
|   |   |   ├── OrderItemRequest.java
|   |   |   ├── OrderRequest.java
|   |   ├── event/
|   |   |   ├── OrderCreatedEvent.java
|   |   ├── model/
|   |   |  ├── Order.java
|   |   |  ├── OrderItem.java
|   |   ├── repository/
|   |   |  ├── OrderRepository.java
|   |   ├── service/
|   |   |   ├── OrderService.java
|   ├── resources/
|   |   ├── application.properties
├── test/
|   ├── java.com.example.order
|   |   ├── OrderApplicationTests.java
|   |   ├── bdd/
|   |   |   ├── config/
|   |   |   |   ├── MockClientConfig.java
|   |   |   ├── steps/
|   |   |   |   ├── CatalogMockSteps.java
|   |   |   |   ├── OrderProcessingSteps.java
|   |   |   |   ├── PaymentMockSteps.java
|   |   |   ├── CucumberSpringConfiguration.java
|   |   |   ├── CucumberTestRunner.java
|   ├── resources/
|   |   ├── application-test.properties
|   |   ├── features/
|   |   |   ├── checkout_validation.feature
|   |   |   ├── order_history_and_compliance.feature
|   |   |   ├── payment_processing.feature
```

## Author

[Tiago Fonseca](https://github.com/tiagorsdfonseca) 

## License

License under the Apache License. See [LICENSE](LICENSE).