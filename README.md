# Header-Based Routing for Multi-Tenancy with Spring Boot

## Overview

This project demonstrates a header-based routing mechanism for multi-tenancy using Spring Boot. It utilizes a custom routing data source to switch between different data sources based on a header value in incoming requests. This setup allows for dynamic routing of database connections, making it suitable for multi-tenant applications.

## Project Structure

The project is structured as follows:

- `com.syshco.routingdb.config`: Contains configuration classes for data sources, routing, and properties.
- `com.syshco.routingdb.config.properties`: Contains configuration properties classes.
- `com.syshco.routingdb`: Main package containing application configurations.

## Technologies Used

- Spring Boot: For creating the application and managing dependencies.
- HikariCP: For connection pooling and managing data sources efficiently.
- Lombok: For reducing boilerplate code with annotations like `@Getter` and `@Setter`.
- Slf4j: For logging messages in the application.

## Configuration

### `application.yml`

The `application.yml` file contains configuration properties for HikariCP data source and Vault data source. Update these properties according to your environment.

```yaml
# HikariCP data source configuration properties
hikari:
  maximumPoolSize: 10
  minimumIdle: 5
  connectionTimeout: 30000
  idleTimeout: 600000
  maxLifetime: 1800000
  autoCommit: true
  readOnly: false
  connectionTestQuery: "SELECT 1"
  poolName: true
  initializationFailTimeout: true
  validationTimeout: 5000
  isolateInternalQueries: false
  allowPoolSuspension: true
  transactionIsolation: 2

#  Datasource configuration properties
hikari.datasource:
  environment:
    - name: "uat"
      dialect: "org.hibernate.dialect.MySQLDialect"
      driverClassName: "com.mysql.cj.jdbc.Driver"
      url: "jdbc:mysql://localhost:3306/development_db"
      userName: "<>"
      password: "<>"
     
