package ar.edu.itba.paw.persistence;

import org.hsqldb.jdbc.JDBCDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@EnableTransactionManagement
@Configuration
@ComponentScan({"ar.edu.itba.paw.persistence",})
public class TestConfig {

    @Value("classpath:db/init.sql")
    private Resource initSql;

    @Value("classpath:db/schema.sql")
    private Resource schemaSql;

    @Bean
    public DataSource dataSource() {
        final SingleConnectionDataSource ds = new SingleConnectionDataSource();

        ds.setDriverClassName(JDBCDriver.class.getCanonicalName());
        ds.setUrl("jdbc:hsqldb:mem:paw");
        ds.setUsername("ha");
        ds.setPassword("");
        ds.setSuppressClose(true);

        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManager(){
        final LocalContainerEntityManagerFactoryBean factoryBean = new
                LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("ar.edu.itba.paw.model");
        factoryBean.setDataSource(dataSource());

        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        factoryBean.setJpaProperties(properties);
        return factoryBean;
    }

    @Bean
    public DataSourceInitializer dsi() {
        final DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(dataSource());
        dsi.setDatabasePopulator(dataSourcePopulator());
        return dsi;
    }

    private DatabasePopulator dataSourcePopulator() {
        final ResourceDatabasePopulator dbp = new ResourceDatabasePopulator();
        dbp.addScript(initSql);
        dbp.addScript(schemaSql);
        return dbp;
    }

}
