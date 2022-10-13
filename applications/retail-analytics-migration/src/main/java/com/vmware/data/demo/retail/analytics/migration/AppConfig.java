package com.vmware.data.demo.retail.analytics.migration;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.util.Organizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * AppConfig
 *
 * @author Gregory Green
 */

@Configuration
public class AppConfig
{

    @Value("${classpath:csv/orders.csv}")
    private Resource orderCsvResource;


    @Value("${classpath:csv/products.csv}")
    private Resource productsCsvResource;

    @Value("${classpath:csv/order_items.csv}")
    private Resource orderItemsCsvResource;

    private String driverClassName  = org.postgresql.Driver.class.getName();



    @AllArgsConstructor
    class AppConnectionCreator implements Creator<Connection>
    {
        private final DataSource dataSource;
        @SneakyThrows
        @Override
        public Connection create()
        {
            return dataSource.getConnection();
        }
    }

    @Bean
    Creator<Connection> createConnectionCreator(DataSource dataSource )
    {
        return new AppConnectionCreator(dataSource);
    }

    @SneakyThrows
    @Bean
    List<TableCsvLoader> listLoader(Creator<Connection> connectionCreator)
    {
        String orderItemSql = "INSERT INTO pivotalmarkets.order_items\n" +
                "(itemid, orderid, productid, quantity, productname)\n" +
                "VALUES(cast (?  AS INTEGER), cast (?  AS INTEGER) , cast (?  AS INTEGER), cast (?  AS float8), ?);";

        TableCsvLoader orderItems = new TableCsvLoader(connectionCreator,
                IO.toReader(orderItemsCsvResource.getInputStream()),
                orderItemSql
        );

        final String ordersSql = "INSERT INTO pivotalmarkets.orders\n" +
                "(orderid, customerid, storeid, orderdate)\n" +
                "VALUES(cast(? as integer), cast(? as integer), cast(? as integer), TO_DATE(?,'MM/DD/YYYY'));";

        TableCsvLoader orders = new TableCsvLoader(connectionCreator,
                IO.toReader(orderCsvResource.getInputStream()),
                ordersSql
        );


        final String productSql = "INSERT INTO pivotalmarkets.product\n" +
                "(productid, productname, categoryid,  \"cost\", price)\n" +
                "VALUES(cast(? as integer), ?, cast(? as integer), cast(? as float), cast(? as float) );\n";

        final TableCsvLoader products = new TableCsvLoader(connectionCreator,
                IO.toReader(productsCsvResource.getInputStream()),
                productSql);

        return Organizer.toList(products,orderItems, orders);
    }
}
