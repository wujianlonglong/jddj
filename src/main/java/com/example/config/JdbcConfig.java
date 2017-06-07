package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by gaoqichao on 16-7-14.
 */
@Configuration
public class JdbcConfig {
    private static final Logger log = LoggerFactory.getLogger(JdbcConfig.class);
    
    @Bean(name = "dataSource")
    @Qualifier("dataSource")
   // @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        log.info("=============初始化三江中台数据库datasouce===========");
        return DataSourceBuilder.create().build();
    }
    
    /**
     * 默认的jdbctemplate
     *
     * @return
     */
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
    
    
    @Bean(name = "sjjkDataSource")
    @ConfigurationProperties(prefix = "sjjk.datasource")
    public DataSource sjjkDataSource() {
        log.info("=============初始化库存中间库datasouce===========");
        return DataSourceBuilder.create().build();
    }
    
    @Bean(name = "stockJdbcTemplate")
    public JdbcTemplate stockJdbcTemplate() {
        return new JdbcTemplate(sjjkDataSource());
    }

    @Bean(name = "jingDongDaoJiaDataSource")
    @ConfigurationProperties(prefix = "jddj.datasource")
    public DataSource jingDongDaoJiaDataSource() {
        log.info("=============初始化京东到家datasouce===========");
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jingDongDaoJiaJdbcTemplate")
    public JdbcTemplate jingDongDaoJiaJdbcTemplate() {
        return new JdbcTemplate(jingDongDaoJiaDataSource());
    }




    @Bean(name = "dwhDataSource")
    @ConfigurationProperties(prefix = "dwh.datasource")
    public DataSource dwhDataSource() {
        log.info("=============初始化库存中间库datasouce===========");
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "dwhJdbcTemplate")
    public JdbcTemplate dwhJdbcTemplate() {
        return new JdbcTemplate(dwhDataSource());
    }

}
