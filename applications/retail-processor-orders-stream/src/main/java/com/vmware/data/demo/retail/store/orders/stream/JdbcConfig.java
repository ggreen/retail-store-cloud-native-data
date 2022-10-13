package com.vmware.data.demo.retail.store.orders.stream;

import com.vmware.data.demo.retail.store.api.order.OrderJdbcRepository;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackageClasses = {OrderJdbcRepository.class})
public class JdbcConfig
{

//    @Bean
//    public OrderJdbcRepository orderJdbcDAO(JdbcTemplate jdbcTemplate, ProductJdbcRepository productDao,
//                                            QuerierService querierService, CustomerJdbcRepository customerFavoriteDao)
//    {
//
//        return new OrderJdbcRepository(jdbcTemplate, productDao, querierService,
//                customerFavoriteDao);
//
//    }//------------------------------------------------


//    @Bean
//    public jakarta.persistence.EntityManagerFactory entityManagerFactory(DataSource dataSource) throws PropertyVetoException
//    {
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setGenerateDdl(true);
//
//
//
//        Properties props = new Properties();
//        props.setProperty("hibernate.dialect", org.hibernate.dialect.PostgreSQL9Dialect.class.getName());
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setJpaVendorAdapter(vendorAdapter);
//        factory.setPackagesToScan("io.pivotal.gemfire.domain");
//        factory.setDataSource(dataSource);
//        factory.setJpaProperties(props);
//        factory.afterPropertiesSet();
//
//        return factory.getObject();
//    }//------------------------------------------------
    /**
     * String url = "jdbc:postgresql://localhost/test";
     Properties props = new Properties();
     props.setProperty("user","fred");
     props.setProperty("password","");
     props.setProperty("ssl","true");
     Connection conn = DriverManager.getConnection(url, props);
     String url = "jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true";
     Connection conn = DriverManager.getConnection(url);
     jdbc:postgresql://host:port/database
     * @param env the spring env context
     * @return the data source
     */
    @Bean
    public DataSource dataSource(Environment env)
    {
        return dataSource(
                env.getRequiredProperty("jdbcUrl"),
                env.getRequiredProperty("jdbcUsername"),
                env.getProperty("jdbcPassword")
        );
    }//------------------------------------------------

    public DataSource dataSource(String jdbcUrl, String userName,
                                 String password)
    {
        // Construct BasicDataSource
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("org.postgresql.Driver");

        bds.setUrl(jdbcUrl);
        bds.setUsername(userName);
        bds.setPassword(password);
        // bds.setDefaultSchema("pivotalmarkets");

        return bds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource ds)
    {
        return new JdbcTemplate(ds);
    }



}
