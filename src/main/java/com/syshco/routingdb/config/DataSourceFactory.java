package com.syshco.routingdb.config;

import com.syshco.routingdb.config.propperties.DataSourceProperties;
import com.syshco.routingdb.config.propperties.HikariCPDataSourceProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating data sources based on configuration properties.
 * This factory is responsible for creating data sources dynamically based on the provided properties.
 */
@Component
public class DataSourceFactory implements DisposableBean {

    @Getter
    private final DataSourceProperties dataSourceProperties; // Holds the configuration properties
    private final HikariCPDataSourceProperties hikariCPDataSourceProperties; // Holds the configuration properties

    private final Map<Object, Object> targetDataSources; // Stores the created data sources

    public DataSourceFactory(DataSourceProperties dataSourceProperties, HikariCPDataSourceProperties hikariCPDataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
        this.hikariCPDataSourceProperties = hikariCPDataSourceProperties;
        this.targetDataSources = createDataSources(); // Initializes data sources based on properties
    }

    /**
     * Retrieves the default data source based on the configured default database in DataSourceProperties.
     *
     * @return The default data source as a DataSource instance.
     */
    public DataSource getDefaultDatasource() {
        DataSourceProperties.VaultDataSource defaultVault = getDefaultVaultDataSource();
        return createDataSource(defaultVault);
    }

    private DataSourceProperties.VaultDataSource getDefaultVaultDataSource() {
        return dataSourceProperties.getEnvironment().stream()
                .filter(vaultDataSource -> vaultDataSource.getName().equalsIgnoreCase(dataSourceProperties.getDefaultDb()))
                .findFirst()
                .orElseGet(() -> dataSourceProperties.getEnvironment().get(0));
    }

    /**
     * Retrieves the source data sources configured based on the environment properties.
     *
     * @return A map containing the configured data sources, where keys are data source names and values are DataSource instances.
     */
    public Map<Object, Object> getSourceDataSource() {
        return targetDataSources; // Returns the created data sources
    }

    private Map<Object, Object> createDataSources() {
        DataSourceProperties.VaultDataSource defaultVault = getDefaultVaultDataSource();
        Map<Object, Object> map = new HashMap<>();
        map.put(defaultVault.getName(), createDataSource(defaultVault)); // Creates and adds default data source
        return map;
    }

    /**
     * Creates a new HikariDataSource instance based on the provided VaultDataSource configuration.
     *
     * @param vaultDataSource The configuration for the data source.
     * @return The created HikariDataSource instance.
     */
    public HikariDataSource createDataSource(DataSourceProperties.VaultDataSource vaultDataSource) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(vaultDataSource.getDriverClassName());
        config.setJdbcUrl(vaultDataSource.getUrl());
        config.setUsername(vaultDataSource.getUserName());
        config.setPassword(vaultDataSource.getPassword());
        config.setMaximumPoolSize(hikariCPDataSourceProperties.getMaximumPoolSize());
        config.setMinimumIdle(hikariCPDataSourceProperties.getMinimumIdle());
        config.setConnectionTimeout(hikariCPDataSourceProperties.getConnectionTimeout());
        config.setIdleTimeout(hikariCPDataSourceProperties.getIdleTimeout());
        config.setMaxLifetime(hikariCPDataSourceProperties.getMaxLifetime());
        config.setAutoCommit(hikariCPDataSourceProperties.isAutoCommit());
        config.setReadOnly(hikariCPDataSourceProperties.isReadOnly());
        config.setConnectionTestQuery(hikariCPDataSourceProperties.getConnectionTestQuery());
        return new HikariDataSource(config);
    }

    @Override
    public void destroy() {
        targetDataSources.values().stream()
                .filter(dataSource -> dataSource instanceof DisposableBean)
                .map(dataSource -> (DisposableBean) dataSource)
                .forEach(disposableBean -> {
                    try {
                        disposableBean.destroy();
                    } catch (Exception ignored) {
                        // Handle or ignore any exceptions during cleanup
                    }
                });
        targetDataSources.clear(); // Clears the map after cleanup
    }
}
