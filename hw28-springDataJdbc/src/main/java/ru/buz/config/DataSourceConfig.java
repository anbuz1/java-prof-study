package ru.buz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
@Configuration
public class DataSourceConfig {
    @Bean(name = "demoDbDataSource")
    @ConfigurationProperties("spring.datasource")
    public DataSource demoDbDataSource() {
        return DataSourceBuilder.create().build();
    }

}
