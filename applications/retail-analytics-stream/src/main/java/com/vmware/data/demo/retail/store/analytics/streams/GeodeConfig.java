package com.vmware.data.demo.retail.store.analytics.streams;

import com.vmware.data.demo.retail.store.domain.CustomerFavorites;
import com.vmware.data.demo.retail.store.domain.Product;
import com.vmware.data.demo.retail.store.domain.ProductAssociate;
import com.vmware.data.demo.retail.store.domain.Promotion;
import com.vmware.data.services.gemfire.client.GemFireClient;
import com.vmware.data.services.gemfire.io.QuerierService;
import org.apache.geode.cache.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class GeodeConfig
{

    @Bean("beaconProductsRegion")
    public Region<String, Set<Product>> beaconProductsRegion()
    {
        return GemFireClient.connect().getRegion("beaconProducts");
    }

    @Bean("customerFavoritesRegion")
    public Region<String, Set<CustomerFavorites>> customerFavoritesRegion()
    {
        return GemFireClient.connect().getRegion("customerFavorites");
    }//------------------------------------------------

    @Bean("beaconPromotionsRegion")
    public Region<String,  Set<Promotion>> beaconPromotionsRegion()
    {
        return GemFireClient.connect().getRegion("beaconPromotions");
    }//------------------------------------------------
    @Bean("customerLocationRegion")
    Region<String,String> customerLocationRegion()
    {
        return GemFireClient.connect().getRegion("customerLocation");
    }

    //productsRegion
    @Bean("productsRegion")
    public Region<Integer,Product> productsRegion()
    {
        return GemFireClient.connect().getRegion("products");
    }

    @Bean("customerPromotionsRegion")
    public Region<String, Set<Promotion>> customerPromotionsRegion()
    {
        return GemFireClient.connect().getRegion("customerPromotions");
    }

    //Region<Integer, Set<ProductAssociate>> productAssociationsRegion;
    @Bean("productAssociationsRegion")
    public Region<Integer, Set<ProductAssociate>> productAssociationsRegion()
    {
        return GemFireClient.connect().getRegion("productAssociations");
    }

    @Bean
    QuerierService querierService()
    {
        return GemFireClient.connect().getQuerierService();
    }
}
