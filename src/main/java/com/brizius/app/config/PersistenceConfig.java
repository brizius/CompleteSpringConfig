package com.brizius.app.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.brizius.app")
@PropertySource("classpath:application.properties")
public class PersistenceConfig {

  private static final String[] PROPERTY_PACKAGES_TO_SCAN = {"com.brizius.myapp",};

  protected static final String PROPERTY_NAME_DATABASE_DRIVER = "jdbc.driverClassName";
  protected static final String PROPERTY_NAME_DATABASE_PASSWORD = "jdbc.password";
  protected static final String PROPERTY_NAME_DATABASE_URL = "jdbc.url";
  protected static final String PROPERTY_NAME_DATABASE_USERNAME = "jdbc.username";

  private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
  private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
  private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
  // private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY =
  // "hibernate.ejb.naming_strategy";
  private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";


  private static final String PROPERTY_NAME_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE =
      "hibernate.cache.use_second_level_cache";
  private static final String PROPERTY_NAME_HIBERNATE_CACHE_USE_QUERY_CACHE =
      "hibernate.cache.use_query_cache";
  private static final String PROPERTY_NAME_HIBERNATE_CACHE_PROVIDER_CLASS =
      "hibernate.cache.provider_class";
  private static final String PROPERTY_NAME_HIBERNATE_CACHE_REGION_FACTORY_CLASS =
      "hibernate.cache.region.factory_class";
  // private static final String
  // PROPERTY_NAME_HIBERNATE_CACHE_PROVIDER_CONFIGURATION="hibernate.cache.provider_configuration";
  private static final String PROPERTY_NAME_HIBERNATE_GENERATE_STATISTICS =
      "hibernate.generate_statistics";


  private static final String PROPERTY_NAME_JAVAX_PERSISTENCE_SHAREDCACHE_MODE =
      "javax.persistence.sharedCache.mode";
  protected static final String PROPERTY_NAME_MEMCACHED_ADDRESS = "memcached.address";

  @Autowired
  private Environment env;

  @Bean
  public DataSource dataSource() {
    HikariDataSource dataSource = new HikariDataSource();

    dataSource.setMaximumPoolSize(100);
    dataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
    dataSource.addDataSourceProperty("url", env.getRequiredProperty("jdbc.url"));
    dataSource.addDataSourceProperty("user", env.getRequiredProperty("jdbc.username"));
    dataSource.addDataSourceProperty("password", env.getRequiredProperty("jdbc.password"));
    return dataSource;
  }

  @Bean
  public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
    JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(emf);
    return txManager;
  }

  @Bean
  // public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean()
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean entityManagerFactory =
        new LocalContainerEntityManagerFactoryBean();
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    entityManagerFactory.setDataSource(dataSource());
    entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
    entityManagerFactory.setPackagesToScan(PROPERTY_PACKAGES_TO_SCAN);

    // entityManagerFactory.setPersistenceUnitName("persistenceUnit");

    Properties jpaProperties = new Properties();
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_HBM2DDL_AUTO));
    // jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
    // env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));

    // Cache
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_CACHE_PROVIDER_CLASS,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CACHE_PROVIDER_CLASS));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_CACHE_USE_QUERY_CACHE,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CACHE_USE_QUERY_CACHE));
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_CACHE_REGION_FACTORY_CLASS,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CACHE_REGION_FACTORY_CLASS));

    jpaProperties.put(PROPERTY_NAME_HIBERNATE_GENERATE_STATISTICS,
        env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_GENERATE_STATISTICS));
    jpaProperties.put(PROPERTY_NAME_MEMCACHED_ADDRESS,
        env.getRequiredProperty(PROPERTY_NAME_MEMCACHED_ADDRESS));

    jpaProperties.put(PROPERTY_NAME_JAVAX_PERSISTENCE_SHAREDCACHE_MODE,
        env.getRequiredProperty(PROPERTY_NAME_JAVAX_PERSISTENCE_SHAREDCACHE_MODE));
    // jpaProperties.put(PROPERTY_NAME_HIBERNATE_CACHE_PROVIDER_CONFIGURATION,
    // env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_CACHE_PROVIDER_CONFIGURATION));

    entityManagerFactory.setJpaProperties(jpaProperties);
    entityManagerFactory.afterPropertiesSet();
    entityManagerFactory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());

    return entityManagerFactory;
  }
}
