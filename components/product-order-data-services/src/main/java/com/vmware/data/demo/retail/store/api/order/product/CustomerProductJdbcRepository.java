package com.vmware.data.demo.retail.store.api.order.product;

import com.vmware.data.demo.retail.store.api.customer.CustomerRepository;
import com.vmware.data.demo.retail.store.domain.CustomerFavorites;
import com.vmware.data.demo.retail.store.domain.CustomerIdentifier;
import com.vmware.data.demo.retail.store.domain.Product;
import com.vmware.data.demo.retail.store.domain.ProductQuantity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Customer data access implement
 * @author gregory green
 */
@Repository
public class CustomerProductJdbcRepository implements CustomerProductRepository
{
    private final ProductRepository retailJdbcDAO;
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRepository customerRepository;

    public CustomerProductJdbcRepository(ProductJdbcRepository productJdbcRepository,
                                         JdbcTemplate jdbcTemplate, CustomerRepository customerRepository)
    {
        this.retailJdbcDAO = productJdbcRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.customerRepository = customerRepository;
    }

    @Override
    public Set<CustomerFavorites> findCustomerFavoritesByIdentifier(CustomerIdentifier customerIdentifier)
    {
        int customerId = customerRepository.findCustomerId(customerIdentifier);


        String sql = "SELECT customerid ,productid ,count\n" +
                "FROM (\n" +
                "  SELECT *\n" +
                "        , max(count) OVER (PARTITION BY customerid) AS _max_\n" +
                "        , row_number() OVER (PARTITION BY customerid, count ORDER BY random()) AS _rank_  -- include this line to randomly select one if ties unacceptable\n" +
                "  FROM (select customerid, productid, count(*)\n" +
                "    from (select o.customerid, i.productid, i.productname\n" +
                "    from orders o, order_items i\n" +
                "    where o.orderid = i.orderid and customerid = ?) as custOrders\n" +
                "    group by productid,customerid\n" +
                "    order by customerid ) aggregateQuery\n" +
                ") foo\n" +
                "WHERE count = _max_\n" +
                "AND _rank_ = 1";


        System.out.println("selectCustomerFavorites sql:" + sql);

        RowMapper<CustomerFavorites> rm = (rs, rowNum) ->
        {
            CustomerFavorites cp = new CustomerFavorites();
            cp.setCustomerId(customerId);

            Collection<ProductQuantity> productQuantities = new ArrayList<ProductQuantity>();


            ProductQuantity productQuantity = new ProductQuantity();
            Product product = new Product();
            product.setProductName(retailJdbcDAO.findProductById(rs.getInt("productid")).getProductName());
            productQuantity.setProduct(product);
            productQuantity.setQuantity(rs.getInt("count"));
            productQuantities.add(productQuantity);

            cp.setProductQuanties(productQuantities);
            return cp;
        };
        Integer[] args = {customerId};

        List<CustomerFavorites> list = jdbcTemplate.query(sql, rm,args);

        if (list == null || list.isEmpty())
            return null;

        return new HashSet<CustomerFavorites>(list);
    }
}