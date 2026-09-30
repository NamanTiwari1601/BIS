package com.proto.BIS.common.Config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FlywayConfig {

    @Bean
    public static BeanFactoryPostProcessor flywayMigrationProcessor(Environment env) {
        return (ConfigurableListableBeanFactory beanFactory) -> {
            Flyway flyway = Flyway.configure()
                    .dataSource(
                            env.getProperty("spring.datasource.url"),
                            env.getProperty("spring.datasource.username"),
                            env.getProperty("spring.datasource.password")
                    )
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .baselineVersion("1")
                    .load();
            flyway.migrate();
        };
    }
}