package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        entityManagerFactoryRef = "sjHubEntityManagerFactory",
        transactionManagerRef = "sjHubTransactionManager",
        basePackages = {"com.example.repository.sjhub"}) //设置Repository所在位置
public class JpaSjhubConfig {

    @Autowired
    @Qualifier("sjHubDataSource")
    private DataSource sjHubDataSource;


    @Primary
    @Bean(name = "sjHubEntityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return sjHubEntityManagerFactory(builder).getObject().createEntityManager();
    }

    @Primary
    @Bean(name = "sjHubEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sjHubEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(sjHubDataSource)
                .properties(getVendorProperties(sjHubDataSource))
                .packages("com.example.domain.sjhub") //设置实体类所在位置
                .persistenceUnit("sjHubPersistenceUnit")
                .build();
    }

    @Autowired
    private JpaProperties jpaProperties;

    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }


    @Primary
    @Bean(name = "sjHubTransactionManager")
    public PlatformTransactionManager sjHubTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(sjHubEntityManagerFactory(builder).getObject());
    }

}
