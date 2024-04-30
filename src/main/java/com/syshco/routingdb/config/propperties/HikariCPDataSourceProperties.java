package com.syshco.routingdb.config.propperties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties class for HikariCPDataSourceProperties.
 * This class is annotated with @Component and @ConfigurationProperties to enable reading properties from the application.yml file.
 * It contains properties related to HikariCP data source configuration.
 */
@Component
@ConfigurationProperties(prefix = "hikari")
@Getter
@Setter
public class HikariCPDataSourceProperties {
    private int maximumPoolSize = 10;
    private int minimumIdle = 5;
    private long connectionTimeout = 30000;
    private long idleTimeout = 600000;
    private long maxLifetime = 1800000;
    private boolean autoCommit = true;
    private boolean readOnly = false;
    private String connectionTestQuery = "SELECT 1";
    private boolean poolName = true;
    private boolean initializationFailTimeout = true;
    private long validationTimeout = 5000;
    private boolean isolateInternalQueries = false;
    private boolean allowPoolSuspension = true;
    private int transactionIsolation = 2;

}
