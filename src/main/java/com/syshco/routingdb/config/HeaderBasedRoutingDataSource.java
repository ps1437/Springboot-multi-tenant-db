package com.syshco.routingdb.config;

import com.syshco.routingdb.config.propperties.DataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A custom routing data source that switches data sources based on a header value.
 * Extends AbstractRoutingDataSource for dynamic routing based on request headers.
 */
@Slf4j
public class HeaderBasedRoutingDataSource extends AbstractRoutingDataSource {

    /**
     * The name of the header used to determine the data source key.
     */
    public static final String DATABASE_NAME = "database";

    private final DataSourceFactory dataSourceFactory;

    HeaderBasedRoutingDataSource(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    /**
     * Determines the current data source lookup key based on the request header.
     *
     * @return The data source lookup key based on the header value, or null if no header is present.
     */
    @Override
    protected Object determineCurrentLookupKey() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            return attr.getRequest().getHeader(DATABASE_NAME);
        } else {
            return null;
        }
    }

    /**
     * Determines the target data source based on the lookup key.
     *
     * @return The target data source to be used.
     * @throws IllegalStateException if the target data source cannot be determined.
     */
    @Override
    protected DataSource determineTargetDataSource() {
        Assert.notNull(this.getResolvedDataSources(), "DataSource router not initialized");
        Object lookupKey = this.determineCurrentLookupKey();

        DataSource dataSource = getResolvedDataSources().get(lookupKey);

        if (dataSource == null) {
            dataSource = createDataSourceWithConfiguration(lookupKey);
        }

        if (dataSource == null && lookupKey == null) {
            dataSource = this.getResolvedDefaultDataSource();
        }

        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        } else {
            return dataSource;
        }
    }

    /**
     * Creates a data source based on the provided configuration.
     *
     * @param lookupKey The lookup key to determine the data source configuration.
     * @return The created data source.
     */
    private DataSource createDataSourceWithConfiguration(Object lookupKey) {
        return Optional.ofNullable(lookupKey)
                .flatMap(key -> {
                    synchronized (this) {
                        // Retrieve the map of resolved data sources
                        Map<Object, DataSource> resolvedDataSources = getResolvedDataSources();

                        // Check if a data source already exists for the given key - Multi Thread scenario
                        if (resolvedDataSources.containsKey(key)) {
                            // If it exists, return the existing data source
                            log.info("Data Source is available for {} returning the same", key);
                            return Optional.of(resolvedDataSources.get(key));
                        }

                        // If the data source doesn't exist, proceed with creating and updating it
                        DataSourceProperties dataSourceProperties = dataSourceFactory.getDataSourceProperties();

                        return dataSourceProperties.getEnvironment().stream()
                                .filter(vaultDataSource -> vaultDataSource.getName().equalsIgnoreCase(key.toString()))
                                .findFirst()
                                .map(dataSourceConfiguration -> updateTargetSetSource(key, dataSourceConfiguration));
                    }
                })
                .orElse(null); // Return null if the lookup key is null or if no data source is found for the key
    }


    private HikariDataSource updateTargetSetSource(Object key, DataSourceProperties.VaultDataSource dataSourceConfiguration) {
        log.info("Database configuration is available for {}", key);
        log.info("Updating datasource current Count: {}", getResolvedDataSources().size());

        Map<Object, Object> sourceMap = new ConcurrentHashMap<>(getResolvedDataSources());

        HikariDataSource dataSource = dataSourceFactory.createDataSource(dataSourceConfiguration);
        sourceMap.put(String.valueOf(key), dataSource);

        setTargetDataSources(Collections.unmodifiableMap(sourceMap));

        afterPropertiesSet();
        log.info("Updated datasource successfully Count: {}", getResolvedDataSources().size());
        return dataSource;
    }

}
