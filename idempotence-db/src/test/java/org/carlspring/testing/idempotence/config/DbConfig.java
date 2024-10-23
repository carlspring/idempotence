package org.carlspring.testing.idempotence.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author carlspring
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.carlspring.testing.idempotence.repository")
@EntityScan(basePackages = "org.carlspring.testing.idempotence.entities")
public class DbConfig
{


    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                                .url("jdbc:postgresql://localhost:5432/db")
                                .username("postgres")
                                .password("postgres")
                                .driverClassName("org.postgresql.Driver")
                                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource)
    {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("org.carlspring.testing.idempotence.entities");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");

//        HashMap<String, Object> properties = new HashMap();
        properties.put("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/db");
        properties.put("jakarta.persistence.jdbc.user", "postgres");
        properties.put("jakarta.persistence.jdbc.password", "postgres");
        properties.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");

        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf)
    {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

}
