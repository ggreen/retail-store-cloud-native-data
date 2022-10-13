package com.vmware.data.demo.retail.store.api.order;

import com.vmware.data.demo.retail.store.api.customer.CustomerRepository;
import com.vmware.data.demo.retail.store.api.order.product.CustomerProductRepository;
import com.vmware.data.demo.retail.store.api.order.product.ProductRepository;
import com.vmware.data.demo.retail.store.domain.*;
import nyla.solutions.core.util.Debugger;
import org.apache.geode.cache.Region;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class OrderDataService implements OrderService, BeaconService
{

	private final OrderJdbcRepository orderDao;
	private final Region<String,Set<CustomerFavorites>> customerFavoritesRegion;
	private final Region<Integer, Set<ProductAssociate>> productAssociationsRegion;
	private final CustomerRepository customerRepository;
	private final CustomerProductRepository customerProductRepository;
	private final ProductRepository productRepository;
	private final Region<String,Set<Product>> beaconProductsRegion;

	private final Region<String,Set<Promotion>> customerPromotionsRegion;


	private final Region<String,Set<Promotion>> beaconPromotionsRegion;

	public OrderDataService(OrderJdbcRepository orderDao, Region<String, Set<CustomerFavorites>> customerFavoritesRegion, Region<Integer,
			Set<ProductAssociate>> productAssociationsRegion, CustomerRepository customerRepository,
							CustomerProductRepository customerProductRepository, ProductRepository productRepository,
							Region<String, Set<Product>> beaconProductsRegion,
							Region<String, Set<Promotion>> customerPromotionsRegion,
							Region<String, Set<Promotion>> beaconPromotionsRegion)
	{
		this.orderDao = orderDao;
		this.customerFavoritesRegion = customerFavoritesRegion;
		this.productAssociationsRegion = productAssociationsRegion;
		this.customerRepository = customerRepository;
		this.customerProductRepository = customerProductRepository;
		this.productRepository = productRepository;
		this.beaconProductsRegion = beaconProductsRegion;
		this.customerPromotionsRegion = customerPromotionsRegion;
		this.beaconPromotionsRegion = beaconPromotionsRegion;
	}


	@Override
	public int processOrder(OrderDTO order)
	{
		Debugger.println(this,"process Order %s",order);
		
		//insert into order_times
		var products = orderDao.saveOrder(order);
		
		constructProductAssociations(products);
		
		if(products == null || products.isEmpty())
			return 0;
		
		
		//calculate
		
		this.customerRepository.updateCustomerFavorites();
		
		//populate region
		this.cacheCustomerFavorites(order.getCustomerIdentifier());
		
		return products.size();
	}

	@Override
	public Collection<OrderDTO> processOrderCSV(String csv)
	{
		
		System.out.println("processOrderCSV:"+csv);
		
		if(csv == null || csv.length() == 0)
			return null;
		
		var lines = csv.split("\n");
		
		if(lines == null || lines.length == 0)
			return null;
		
		
		ArrayList<OrderDTO> orders = new ArrayList<>(lines.length);
		
		for (String line : lines)
		{
			line = line.trim();
			if(line.length() ==0)
				continue; //skip empty lines
			
			orders.add(this.processOrderCSVLine(line));
		}
		
		orders.trimToSize();
		
		return orders;
		
	}//------------------------------------------------
	
	/* (non-Javadoc)
	 * @see io.pivotal.market.api.PivotalMartFacadeService#processOrderCSV(java.lang.String)
	 */
	@Override
	public OrderDTO processOrderCSVLine(String csv)
	{
		Debugger.println(this,"processing csv line:"+csv);
		
		if(csv  == null || csv.length() == 0 || csv.trim().length() == 0)
		{
			Debugger.println(this,"CSV is null. Returning null");
			return null;
		}
		
		try
		{
			var builder = new RetailOrderCsvBuilder();
			builder.buildOrderLine(csv);
			
			var productIds = builder.getProductIds();
			
			if(productIds == null || productIds.length ==0 )
			{
				throw new IllegalArgumentException("Product ids are null or empty:"+csv);
			}
			
			var orderDTO = builder.getOrderDTO();
			
			this.processOrder(orderDTO);
			
			return orderDTO;
		}
		catch (RuntimeException e)
		{
			Debugger.printError(e);
			
			throw e;
		}
		
	}

	void constructProductAssociations(Collection<Product> products)
	{

		if (products != null) {
			for (Product product : products) {
				Set<ProductAssociate> productAssociation = productRepository.findProductAssociates(product);

				if (productAssociation == null || productAssociation.isEmpty())
					continue;

				this.productAssociationsRegion.put(product.getProductId(), productAssociation);
			}
		}
	}

	void cacheCustomerFavorites(CustomerIdentifier customerIdentifier)
	{
		Set<CustomerFavorites> customerFavorites = customerProductRepository.findCustomerFavoritesByIdentifier(customerIdentifier);
		if(customerFavorites == null || customerFavorites.isEmpty())
			return;

		this.customerFavoritesRegion.put(customerIdentifier.getKey(), customerFavorites);
	}

	@Override
	public void processBeaconRequest(BeaconRequest br)
	{
		try
		{

			System.out.println("processBeaconRequest:"+br);

			Beacon beacon = new Beacon();
			beacon.setUuid(br.getUuid());
			beacon.setMajor(br.getMajor());
			beacon.setMinor(br.getMinor());

			cacheCustomerFavorites(br.getCustomerId());

			Set<Product> products = productRepository.findProductsByBeacon(beacon);

			if (products == null || products.isEmpty())
				return;

			beaconProductsRegion.put(beacon.getUuid(), products);

			Set<Promotion> promotions = new HashSet<>();
			for (Product product : products) {
				System.out.println(" Looking for promotion for product:" + product);
				Set<Promotion> set = productRepository.findPromotionsByProduct(product);
				if (set == null || set.isEmpty())
					continue;

				System.out.println("found promotions:" + set);

				promotions.addAll(set);

			}
			if (promotions.isEmpty())
				return;

			//Add associations
			this.cacheProductAssociations(products);

			//add promotions
			customerPromotionsRegion.put(br.getCustomerId().getKey(), promotions);
			this.beaconPromotionsRegion.put(beacon.getKey(), promotions);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
	protected void cacheProductAssociations(Collection<Product> products)
	{
		if (products == null)
			return;

		Set<ProductAssociate> productAssociation = null;
		for (Product product : products) {
			productAssociation = productRepository.findProductAssociates(product);

			if (productAssociation == null || productAssociation.isEmpty())
				continue;

			this.productAssociationsRegion.put(product.getProductId(), productAssociation);
		}
	}
}
