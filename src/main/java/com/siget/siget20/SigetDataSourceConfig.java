package com.siget.siget20;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.siget.siget20.Repository.SIGET",
        entityManagerFactoryRef = "sigetEntityManagerFactory",
        transactionManagerRef = "sigetTransactionManager"
)
@EntityScan(basePackages = "com.siget.siget20.Repository.SIGET.Entity")
public class SigetDataSourceConfig {

    @Primary
    @Bean(name = "sigetDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.siget")
    public DataSource sigetDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "sigetEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sigetEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("sigetDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.siget.siget20.Repository.SIGET.Entity")
                .persistenceUnit("sigetPU")
                .build();
    }

    @Primary
    @Bean(name = "sigetTransactionManager")
    public PlatformTransactionManager sigetTransactionManager(
            @Qualifier("sigetEntityManagerFactory")
            LocalContainerEntityManagerFactoryBean sigetEntityManagerFactory) {

        return new JpaTransactionManager(sigetEntityManagerFactory.getObject());
    }
}
