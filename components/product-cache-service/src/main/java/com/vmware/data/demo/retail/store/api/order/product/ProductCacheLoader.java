package com.vmware.data.demo.retail.store.api.order.product;

import com.vmware.data.demo.retail.store.domain.Product;
import nyla.solutions.core.patterns.workthread.ExecutorBoss;
import nyla.solutions.core.util.Organizer;
import org.apache.geode.cache.Region;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProductCacheLoader implements ProductCacheService
{
    private final ProductJdbcRepository dao;
    private final Region<Integer,Product> productsRegion;
    private final ExecutorBoss boss = new ExecutorBoss(1);

    public ProductCacheLoader(ProductJdbcRepository dao, Region<Integer, Product> productsRegion)
    {
        this.dao = dao;
        this.productsRegion = productsRegion;
    }


    /**
     * Load all products from database into cache
     */
    @Override
    public void loadProductsCache()
    {
        var ids = this.dao.findProductIds();

        var batchSize = 100;
        var pages= Organizer.toPages(ids, batchSize);

                try
                {
                    Map<Integer, Product> batch = new HashMap<>(batchSize);

                    for (int productId : ids)
                    {
                        batch.put(Integer.valueOf(productId),this.dao.findProductById(productId));


                        if(batch.size() > batchSize)
                        {
                            this.productsRegion.putAll(batch);
                            batch.clear();
                        }
                    }//end for

                    if(!batch.isEmpty())
                    {
                        this.productsRegion.putAll(batch);
                        batch.clear();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

        }

}
