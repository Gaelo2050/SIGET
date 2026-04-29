package com.siget.siget20;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.siget.siget20.Repository.AMBAR",
        entityManagerFactoryRef = "ambarEntityManagerFactory",
        transactionManagerRef = "ambarTransactionManager"
)
@EntityScan(basePackages = "com.siget.siget20.Repository.AMBAR.Entity")
public class AmbarDataSourceConfig {

    @Bean(name = "ambarDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.ambar")
    public DataSource ambarDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "ambarEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean ambarEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("ambarDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.siget.siget20.Repository.AMBAR.Entity")
                .persistenceUnit("ambarPU")
                .build();
    }

    @Bean(name = "ambarTransactionManager")
    public PlatformTransactionManager ambarTransactionManager(
            @Qualifier("ambarEntityManagerFactory")
            LocalContainerEntityManagerFactoryBean ambarEntityManagerFactory) {

        return new JpaTransactionManager(ambarEntityManagerFactory.getObject());
    }
}
