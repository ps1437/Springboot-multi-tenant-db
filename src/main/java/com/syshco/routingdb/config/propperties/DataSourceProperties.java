package com.syshco.routingdb.config.propperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuration properties class for DataSourceProperties.
 * This class is annotated with @Component and @ConfigurationProperties to enable reading properties from the application.yml file.
 * It contains properties related to data source configuration such as environment and default database.
 */
@Component
@ConfigurationProperties(prefix = "hikari.datasource")
@Getter
@Setter
public class DataSourceProperties {

    /**
     * List of VaultDataSource instances representing different environments and configurations.
     */
    private List<VaultDataSource> environment;

    /**
     * The name of the default database to use from the configured environments.
     */
    private String defaultDb;

    /**
     * Inner static class representing a data source configuration for a specific environment in the vault.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class VaultDataSource {
        private String name;
        private String dialect;
        private String driverClassName;
        private String userName;
        private String password;
        private String url;
    }
}
