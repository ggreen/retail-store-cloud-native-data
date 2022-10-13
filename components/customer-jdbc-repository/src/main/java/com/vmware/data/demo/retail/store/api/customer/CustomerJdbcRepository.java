package com.vmware.data.demo.retail.store.api.customer;

import com.vmware.data.demo.retail.store.api.JdbcUtil;
import com.vmware.data.demo.retail.store.domain.Customer;
import com.vmware.data.demo.retail.store.domain.CustomerIdentifier;
import nyla.solutions.core.util.Text;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerJdbcRepository implements CustomerRepository
{
    private final JdbcTemplate jdbcTemplate;

    public CustomerJdbcRepository(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findCustomerId(CustomerIdentifier customerIdentifier)
	throws EmptyResultDataAccessException

    {
        String firstName = Text.initCaps(customerIdentifier.getFirstName());
        String lastName = Text.initCaps(customerIdentifier.getLastName());

        String sql = "select customerid from pivotalmarkets.customers c where (c.firstname = ? and c.lastname = ?) limit 1 ";
        // Object[] args, Class<T> requiredTypex
        Object[] args = {firstName, lastName};
        Integer customerId = jdbcTemplate.queryForObject(sql, Integer.class,args);
        if(customerId == null)
            return -1;

        return customerId;
    }
    @Override
    public int saveCustomerByIdentifier(CustomerIdentifier customerIdentifier)
    {


        try {
            int customerId = findCustomerId(customerIdentifier);
            if (customerId > 0)
                return -1; //already exists;
        }
        catch (EmptyResultDataAccessException e) {
        }
        Customer customer = new Customer();
        customer.setFirstName(Text.initCaps(customerIdentifier.getFirstName()));
        customer.setLastName(Text.initCaps(customerIdentifier.getLastName()));
        customer.setMobileNumber(customerIdentifier.getMobileNumber());
        int customerId = JdbcUtil.nextSeqVal(this.jdbcTemplate,"customer_seq");
        customer.setCustomerId(customerId);

        String sql = "INSERT INTO \"pivotalmarkets\".\"customers\" (customerid,firstname,lastname,mobilenumber) VALUES (?,?,?,?)";

        jdbcTemplate.update(sql, customer.getCustomerId(), customer.getFirstName(), customer.getLastName(), customer.getMobileNumber());

        return customerId;
    }//------------------------------------------------
    @Override
    public int updateCustomerFavorites()
    {
        //TODO: move to stored procedures
//		String sql = "\n" +
//		"set search_path to pivotalmarkets;\n" +
//		"\n" +
//		"drop table if exists customer_favorites;\n" +
//		"drop table if exists cp1;\n" +
//		"create temp table cp1 as\n" +
//		"    select o.customerid, i.productid, i.productname\n" +
//		"    from orders o, order_items i\n" +
//		"    where o.orderid = i.orderid;\n" +
//		"\n" +
//		"\n" +
//		"drop table if exists cp2;\n" +
//		"create temp table cp2 as\n" +
//		"    select customerid, productid, count(*)\n" +
//		"    from cp1\n" +
//		"    group by productid,customerid\n" +
//		"    order by customerid ;\n" +
//		"\n" +
//		"CREATE TABLE customer_favorites as\n" +
//		"SELECT customerid ,productid ,count\n" +
//		"FROM (\n" +
//		"  SELECT *\n" +
//		"        , max(count) OVER (PARTITION BY customerid) AS _max_\n" +
//		"        , row_number() OVER (PARTITION BY customerid, count ORDER BY random()) AS _rank_  -- include this line to randomly select one if ties unacceptable\n" +
//		"  FROM cp2\n" +
//		") foo\n" +
//		"WHERE count = _max_\n" +
//		"AND _rank_ = 1;\n" +
//		"\n" +
//		"ALTER TABLE customer_favorites ADD COLUMN productname TEXT;\n" +
//		"UPDATE customer_favorites c SET productname = (SELECT productname FROM product p WHERE p.productid = c.productid limit 1);\n";
//
//		return this.jdbcTemplate.update(sql);

        return 0;

    }
}