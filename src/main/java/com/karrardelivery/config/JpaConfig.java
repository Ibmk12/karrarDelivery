//package com.karrardelivery.Karrar.Delivery.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.orm.jpa.JpaVendorAdapter;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//
//import javax.sql.DataSource;
//
//
//public class JpaConfig {
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
//            DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setDataSource(dataSource);
//        factory.setJpaVendorAdapter(jpaVendorAdapter);
//        factory.setPackagesToScan("com.karrardelivery.Karrar.Delivery"); // Adjust package to match your entities
//        return factory;
//    }
//
//}
