package com.vmware.data.demo.retail.store;

import com.vmware.data.demo.retail.store.api.customer.CustomerDataService;
import com.vmware.data.demo.retail.store.domain.CustomerFavorites;
import com.vmware.data.demo.retail.store.domain.Product;
import com.vmware.data.demo.retail.store.domain.ProductAssociate;
import com.vmware.data.demo.retail.store.domain.Promotion;
import com.vmware.data.demo.retail.store.listeners.CustomerPromotionStompConsumer;
import com.vmware.data.services.gemfire.client.GemFireClient;
import com.vmware.data.services.gemfire.client.RegionTemplate;
import com.vmware.data.services.gemfire.client.listeners.CacheListenerBridge;
import com.vmware.data.services.gemfire.lucene.GemFireLuceneSearch;
import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.pdx.PdxInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;

/**
 * @author Gregory Greem
 */
@Configuration
@ComponentScan(basePackageClasses = CustomerDataService.class)
public class GemFireConfig
{
	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${gemFire.locators}")
	private String locators;

	@Bean(name = "productAssociationsRegion")
	 public  Region<Integer, Set<ProductAssociate>> productAssociationsRegion(@Autowired GemFireClient gemFireClient)
	 {
	         return gemFireClient.getRegion("productAssociations");
	 }

	 @Bean(name = "productsRegion")
	 public Region<String,PdxInstance> productsRegion(@Autowired GemFireClient  gemFireClient)
	 {
	         return gemFireClient.getRegion("products");
	 }

	@Bean
	public RegionTemplate productsRegionTemplate(@Qualifier("productsRegion") Region<String,PdxInstance> productsRegions)
	{
		return new RegionTemplate(productsRegions);
	}//------------------------------------------------
	@Bean
	GemFireClient getGemFire()
	{
		return GemFireClient.builder().locators(locators).clientName(applicationName).build();
	}//------------------------------------------------
	@Bean(name = "gemfireCache")
    public ClientCache getGemfireClientCache(GemFireClient gemFireClient) throws Exception {

		 return gemFireClient.getClientCache();
    }//------------------------------------------------
	@Bean(name = "alerts")
	public Region<String,PdxInstance> getAlerts(GemFireClient gemFireClient)
	{
		return gemFireClient.getRegion("alerts");
	}//------------------------------------------------
	@Bean(name = "productRecommendationsRegion")
	public Region<String, Collection<Product>> productRecommendationsRegion(GemFireClient gemFireClient)
	{
		return gemFireClient.getRegion("productRecommendations");
	}//------------------------------------------------
	@Bean(name = "beaconPromotionsRegion")
	public Region<String,Collection<Promotion>> beaconPromotionsRegion(GemFireClient 		   gemFireClient)
	{
		return gemFireClient.getRegion("beaconPromotions");
	}

	@Bean(name = "customerLocationRegion")
	public Region<String,String> customerLocationRegion(GemFireClient gemFireClient)
	{
		return gemFireClient.getRegion("customerLocation");
	}

	@Bean(name = "customerPromotionsRegion")
	public Region<String,Collection<Promotion>> customerPromotions(GemFireClient 	   gemFireClient, CustomerPromotionStompConsumer consumer)
	{
		Region<String,Collection<Promotion>> region = gemFireClient.getRegion("customerPromotions");

		region.registerInterestForAllKeys();

		CacheListener<String,Collection<Promotion>> consumerPromotionListener = CacheListenerBridge.forAfterPut(consumer);

		region.getAttributesMutator().addCacheListener(consumerPromotionListener);

		return region;
	}

	@Bean(name = "customerFavoritesRegion")
	Region<String,Collection<CustomerFavorites>> getCustomerFavoritesRegion(GemFireClient 			gemFireClient)
	{
		return gemFireClient.getRegion("customerFavorites");
	}

	@Bean(name = "liveAlertsQueue")
	public Queue<Collection<Promotion>> getAlertQueue(GemFireClient gemFireClient)
	{
		return gemFireClient.registerCq("liveAlerts", "select * from /customerPromotions");
	}

	@Bean
	public GemFireLuceneSearch geodeLuceneSearch(GemFireClient gemfireClient)
	{
		return new GemFireLuceneSearch(gemfireClient.getClientCache());
	}
}
