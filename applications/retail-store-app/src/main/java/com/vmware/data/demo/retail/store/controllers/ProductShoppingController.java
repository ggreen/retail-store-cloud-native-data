package com.vmware.data.demo.retail.store.controllers;

import com.vmware.data.demo.retail.store.domain.*;
import com.vmware.data.services.gemfire.client.RegionTemplate;
import com.vmware.data.services.gemfire.io.GemFireIO;
import com.vmware.data.services.gemfire.lucene.GemFireLuceneSearch;
import com.vmware.data.services.gemfire.spring.security.SpringSecurityUserService;
import com.vmware.data.services.gemfire.spring.security.data.UserProfileDetails;
import nyla.solutions.core.data.collections.QueueSupplier;
import nyla.solutions.core.util.Organizer;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
public class ProductShoppingController
{
	
	private String regionName = "/products";
	private String indexName = "productIndex";
	private String defaultField = "productName";

	private final SpringSecurityUserService springSecurityUserService;
	private final GemFireLuceneSearch search;
	private final Region<String, Collection<Product>> productRecommendationsRegion;
	private final Region<Integer,Set<ProductAssociate>> productAssociationsRegion;
	private final QueueSupplier<OrderDTO> messageChannel;
	private final RegionTemplate productRegionTemplate;

	public ProductShoppingController(RegionTemplate productRegionTemplate,
									 SpringSecurityUserService springSecurityUserService,
									 GemFireLuceneSearch search,
									 @Qualifier("productRecommendationsRegion")
								  Region<String, Collection<Product>> productRecommendationsRegion,
									 @Qualifier("productAssociationsRegion")
								  Region<Integer,
			Set<ProductAssociate>> productAssociationsRegion,
									 QueueSupplier<OrderDTO> messageChannel)
	{
		this.messageChannel = messageChannel;
		this.productRegionTemplate = productRegionTemplate;
		this.springSecurityUserService = springSecurityUserService;
		this.search = search;
		this.productRecommendationsRegion = productRecommendationsRegion;
		this.productAssociationsRegion = productAssociationsRegion;
	}

	@GetMapping("/searchProducts/{queryString}")
	public Collection<Product> searchProducts(@PathVariable String queryString)
	throws Exception
	{
		String[] args = new String[]{indexName, regionName, queryString, defaultField};

		Collection<Product> result = GemFireIO.exeWithResults(
				FunctionService.onRegion(productRegionTemplate.getRegion())
							   .setArguments(args).setArguments(args), "SimpleLuceneSearchFunction");

		return result;
	}
	
	@GetMapping("findProductRecommendations/{productId}")
	public Collection<Product> findProductRecommendations(@PathVariable String productId)
	{
		return this.productRecommendationsRegion.get(productId);
	}//------------------------------------------------

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/order")
	@PreAuthorize("hasRole('ROLE_WRITE')")
	public OrderReview orderProducts(Principal userPrincipal, @RequestBody Integer[] productIds)
	throws Exception
	{
		try
		{
			if(productIds == null || productIds.length == 0)
				throw new IllegalArgumentException("ProductIds required");
			
			List<Integer> productList = Arrays.asList(productIds);


			Collection<Product> products = productRegionTemplate.getAllValues(productList);
			OrderReview or = new OrderReview();
			or.setProducts(new HashSet<Product>(products));

			Collection<Set<ProductAssociate>> associations = productAssociationsRegion.getAll(productList).values();
			if(associations != null && !associations.isEmpty())
			{
				Collection<ProductAssociate> productAssociations = new HashSet<>(associations.size());
				Organizer.flatten(associations, productAssociations);
				
				if(!productAssociations.isEmpty())
					or.setProductAssociates(new HashSet<ProductAssociate>(productAssociations));
				
			}
			
			UserProfileDetails user =springSecurityUserService
										.findUserProfileDetailsByUserName(userPrincipal.getName());

			CustomerIdentifier customerIdentifier = new CustomerIdentifier(
					userPrincipal.getName(),
			user.getFirstName(), user.getLastName(),user.getEmail(), user.getPhone());
			
			OrderDTO order = new OrderDTO(customerIdentifier, productIds);

			this.messageChannel.add(order);
			
			return or;
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
}
