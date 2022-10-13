package com.vmware.data.demo.retail.analytics.migration;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import nyla.solutions.core.patterns.creational.Creator;
import nyla.solutions.core.patterns.jdbc.office.CsvJdbcLoader;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * TableCsvLoader
 *
 * @author Gregory Green
 */
@AllArgsConstructor
public class TableCsvLoader
{
    private final Creator<Connection> creator;
    private final Reader reader;
    private final String sql;

    @SneakyThrows
    public void run()
    {
        load(reader);
    }

    Long load(Reader reader) throws SQLException, IOException
    {
        var loader = new CsvJdbcLoader();
        System.out.println("EXECUTING: "+sql);
        return loader.load(creator,reader,sql,true);
    }
}
