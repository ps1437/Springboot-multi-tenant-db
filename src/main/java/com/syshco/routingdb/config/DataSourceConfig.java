package com.syshco.routingdb.config;

/**
 * Configuration class for setting up the data source and JPA repositories.
 * This configuration class enables transaction management and JPA repositories scanning.
 */

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.syshco.routingdb")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "datasource.schemaPerRequest.enabled", havingValue = "true", matchIfMissing = true)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(DataSourceFactory dataSourceFactory) {
        // Create a custom data source for header-based routing
        HeaderBasedRoutingDataSource customDataSource = new HeaderBasedRoutingDataSource(dataSourceFactory);
        // Set the target data sources based on header information
        customDataSource.setTargetDataSources(dataSourceFactory.getSourceDataSource());
        // Set the default data source if no header information matches
        customDataSource.setDefaultTargetDataSource(dataSourceFactory.getDefaultDatasource());
        return customDataSource;
    }


}
