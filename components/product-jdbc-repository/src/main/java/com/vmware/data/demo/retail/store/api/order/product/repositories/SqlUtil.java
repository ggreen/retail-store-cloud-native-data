package com.vmware.data.demo.retail.store.api.order.product.repositories;

public class SqlUtil
{
    public static String escape(String associations)
    {
        if (associations == null)
            return "''";

        return "'" + associations.replace("'", "''") + "'";
    }
}