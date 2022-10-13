package com.vmware.data.demo.retail.store.analytics.streams;

import com.vmware.data.demo.retail.store.api.customer.CustomerJdbcRepository;
import com.vmware.data.demo.retail.store.api.order.OrderJdbcRepository;
import com.vmware.data.demo.retail.store.api.order.product.ProductJdbcRepository;
import nyla.solutions.core.patterns.machineLearning.associations.AssociationProbabilities;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories
@ComponentScan(basePackageClasses = {
        ProductJdbcRepository.class,
        OrderJdbcRepository.class,
        ProductJdbcRepository.class,
        CustomerJdbcRepository.class})
@EntityScan
public class DatabaseConfig
{
//    @Bean
//    public ProductJdbcRepository productJdbcDao(JdbcTemplate jdbcTemplate)
//    {
//        return new ProductJdbcRepository(jdbcTemplate);
//    }
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    AssociationProbabilities analyzer(OrderJdbcRepository orderRepository)
    {
        double minimum = 0.75;
        long numTransactions = orderRepository.countOrders();
        double confidence = 1.6666666666666666E-4;
        return new AssociationProbabilities(minimum,numTransactions,confidence);
    }

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


    /**
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

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource ds)
    {
        return new JdbcTemplate(ds);
    }

//    @Bean
//    public RetailAnalyticsRepository dao(DataSource dataSource,
//                                         JdbcTemplate jdbcTemplate,
//                                         ProductJdbcRepository pivotMarketPostgreDAO, QuerierService querierService)
//    {
//        return new RetailAnalyticsRepository(jdbcTemplate, pivotMarketPostgreDAO, querierService);
//    }//------------------------------------------------
}
