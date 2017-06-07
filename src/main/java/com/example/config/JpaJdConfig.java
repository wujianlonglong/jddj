package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by wujianlong on 2017/3/21.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "jddjEntityManagerFactory",
        transactionManagerRef = "jddjTransactionManager",
        basePackages = {"com.example.repository.jddj"}) //设置Repository所在位置
public class JpaJdConfig {

    @Autowired
    @Qualifier("jddjDataSource")
    private DataSource jddjDataSource;

   // @Primary
    @Bean(name = "jddjEntityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return jddjEntityManagerFactory(builder).getObject().createEntityManager();
    }

    //@Primary
    @Bean(name = "jddjEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean jddjEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(jddjDataSource)
                .properties(getVendorProperties(jddjDataSource))
                .packages("com.example.domain.jddj") //设置实体类所在位置
                .persistenceUnit("jddjPersistenceUnit")
                .build();
    }

    @Autowired
    private JpaProperties jpaProperties;

    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

   // @Primary
    @Bean(name = "jddjTransactionManager")
    public PlatformTransactionManager jddjTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(jddjEntityManagerFactory(builder).getObject());
    }

}
