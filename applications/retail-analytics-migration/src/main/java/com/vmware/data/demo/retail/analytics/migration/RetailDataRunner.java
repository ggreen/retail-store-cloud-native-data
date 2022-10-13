package com.vmware.data.demo.retail.analytics.migration;

import nyla.solutions.core.io.IO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RetailDataRunner
 *
 * @author Gregory Green
 */
@Component
public class RetailDataRunner implements CommandLineRunner
{

    @Value("${spring.datasource.username}")
    private String dbUserName = "postgres";

    @Value("${classpath:schema.sql}")
    private Resource schemaSqlResource;

    @Value("${classpath:load_data.sql}")
    private Resource loadSqlResource;

    private final List<TableCsvLoader> loaders;
    private final JdbcTemplate jdbcTemplate;

    public RetailDataRunner(List<TableCsvLoader> loaders, JdbcTemplate jdbcTemplate)
    {
        this.loaders = loaders;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception
    {

        var sql = IO.readText(schemaSqlResource.getInputStream(),true);

        System.out.println("SQL:"+sql);

        jdbcTemplate.execute(sql);


        sql = IO.readText(loadSqlResource.getInputStream(),true);

        System.out.println("SQL:"+sql);
        jdbcTemplate.execute(sql);

        for (TableCsvLoader loader:loaders)
        {
            loader.run();
        }

        var searchSql = "ALTER ROLE "+dbUserName+" SET search_path TO pivotalmarkets;";
        System.out.println("\n\n *** CHANGE SEARCH PATH SQL:"+searchSql);
        jdbcTemplate.execute(searchSql);


        System.out.println("===================DONE=================");

    }
}
