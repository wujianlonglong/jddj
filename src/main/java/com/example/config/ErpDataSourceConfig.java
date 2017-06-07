package com.example.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Created by wujianlong on 2017/3/21.
 */

@Configuration
public class ErpDataSourceConfig {

    @Bean(name = "jddjDataSource")
    @ConfigurationProperties(prefix = "jddj.datasource")
   // @Primary
    public DataSource jddjDataSource() {
        return DataSourceBuilder.create().build();
    }



    @Bean(name = "sjHubDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    public DataSource sjHubDataSource() {
        return DataSourceBuilder.create().build();
    }

}
