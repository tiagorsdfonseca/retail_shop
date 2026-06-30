# retail_shop

Repository Name: retail-shop-project


# Retail Shop Template

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://events.development.outlook.com)
[![Spring Boot 4](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=flat&logo=spring&logoColor=white)](https://shields.io/)

This repository contains the microservices code for the **Retail Shop** project. The application replicates a e-distributed e-commerce architecture focused on catalog inventory, checkout processing, and payment operations using modern Spring native capabilities.

## Usage



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


## Project layout

The code for this application is divided in two main sections: `catalog` and `order`. The first one focuses on the operations which are related with inventory management and promotional price management. On the other hand, the `order` section deals with the payment mechanisms and checkout validation. The code is presented in the `service` package of each section. All of the tests are in the `tests` folder. There also Behavioral-Driven Development features implemented in Cucumber present on the `resources/features` folder.

## Catalog Architecture

```src/
├── main/
|   ├── java.com.example.catalog/
|   |   ├── CatalogApplication.java
|   |   ├── controller/
|   |   ├── model/
|   |   |   ├── Product.java
|   |   ├── repository/
|   |   |   ├── ProductRepository.java
|   ├── resources/
|   |   ├── application.properties
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
```

## Order Architecture

```src/
├── main/
|   ├── java.com.example.order/
|   |   ├── OrderApplication.java
|   |   ├── client/
|   |   |   ├── CatalogClient.java
|   |   |   ├── CatalogResponse.java
|   |   |   ├── PaymentClient.java
|   |   ├── controller/
|   |   ├── dto/
|   |   |   ├── OrderRequest.java
|   |   ├── event/
|   |   |   ├── OrderCreatedEvent.java
|   |   ├── model/
|   |   ├── repository/
|   |   ├── service/
|   |   |   ├── OrderService.java
|   ├── resources/
|   |   ├── application.properties
├── test/
|   ├── java.com.example.order
|   |   ├── OrderApplicationTests.java
|   |   ├── bdd/
|   |   |   ├── config/
|   |   |   |   ├── ClientConfig.java
|   |   |   |   ├── MockClientConfig.java
|   |   |   |   ├── TestClientConfig.java
|   |   |   ├── steps/
|   |   |   |   ├── CatalogMockSteps.java
|   |   |   |   ├── OrderProcessingSteps.java
|   |   |   |   ├── PaymentMockSteps.java
|   |   |   ├── CucumberSpringConfiguration.java
|   |   |   ├── CucumberTestRunner.java
|   ├── resources/
|   |   ├── features/
|   |   |   ├── checkout_validation.feature
|   |   |   ├── order_history_and_compliance.feature
|   |   |   ├── payment_processing.feature
```

## Author

[Tiago Fonseca]

## License
