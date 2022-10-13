package com.vmware.data.demo.retail.store.api.order;

import com.vmware.data.demo.retail.store.api.customer.CustomerRepository;
import com.vmware.data.demo.retail.store.api.order.product.ProductJdbcRepository;
import com.vmware.data.demo.retail.store.domain.OrderDTO;
import com.vmware.data.demo.retail.store.domain.Product;
import com.vmware.data.services.gemfire.io.QuerierService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

@Repository
public class OrderJdbcRepository implements OrderRepository
{
	private final JdbcTemplate jdbcTemplate;
	
	private final ProductJdbcRepository productDao;
	
	private final QuerierService querierService;
	private final CustomerRepository customerFavoriteDao;

	public OrderJdbcRepository(JdbcTemplate jdbcTemplate,
							   ProductJdbcRepository productDao,
							   QuerierService querierService,
							   CustomerRepository customerFavoriteDao)
	{
		this.jdbcTemplate = jdbcTemplate;
		this.productDao = productDao;
		this.querierService = querierService;
		this.customerFavoriteDao = customerFavoriteDao;
	}



	@Override
	public Collection<Product> saveOrder(OrderDTO order)
	{
		Integer[] productIds = order.getProductIds();
		
		if (productIds == null || productIds.length == 0)
			throw new IllegalArgumentException("producdIds is required");
		
		
		String insertOrderSQL = "INSERT INTO \"pivotalmarkets\".\"orders\" (orderid,customerid,storeid,orderdate) VALUES (?,?,?,?)";
		//Get customerId
		int customerId;
		
		try
		{
			customerId = customerFavoriteDao.findCustomerId(order.getCustomerIdentifier());
		}
		catch(EmptyResultDataAccessException e)
		{
			customerId = customerFavoriteDao.saveCustomerByIdentifier(order.getCustomerIdentifier());
			
		}
		
		//Get orderId
		int orderId = nextSeqVal("pivotalmarkets.order_seq");
		int storeId = 4;
		
		this.jdbcTemplate.update(insertOrderSQL,orderId,customerId,storeId,Calendar.getInstance().getTime());
		
		String insertItemSql="INSERT INTO pivotalmarkets.order_items(itemid, "+
										"orderid,  " + 	
										"productid, " + 
										"quantity, " + 
										"productname)" + 
		" values(nextval('pivotalmarkets.item_seq'),?,?,?,?)";
		
		ArrayList<Product> products = new ArrayList<>(productIds.length );
		Product product;
		for (Integer productId : productIds)
		{
			product = productDao.findProductById(productId);
			products.add(product);

			this.jdbcTemplate.update(insertItemSql, orderId,productId,1,product.getProductName());
		}
		
		return products;
	}

	public int nextSeqVal(String sequenceName)
	{
		return this.jdbcTemplate.queryForObject("select nextval('"+sequenceName+"')", int.class);
	}


	@Override
	public long countOrders()
	{
		final String countSql = "select count(*) from pivotalmarkets.orders";
		return this.jdbcTemplate.queryForObject(countSql,Long.class);
	}
}
