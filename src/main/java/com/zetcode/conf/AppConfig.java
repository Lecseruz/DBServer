package com.zetcode.conf;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public static DataSource primaryDataSource() {
        return DataSourceBuilder.create()
        .url("jdbc:postgresql://localhost:5432/technopark")
        .password("magomed20121998")
        .username("magomed")
        .driverClassName("org.postgresql.Driver")
        .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.mysqldatasource")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
}