package com.vmware.data.demo.retail.store.api.order;

import com.vmware.data.demo.retail.store.api.customer.CustomerRepository;
import com.vmware.data.demo.retail.store.api.order.product.CustomerProductRepository;
import com.vmware.data.demo.retail.store.api.order.product.ProductRepository;
import com.vmware.data.demo.retail.store.domain.*;
import nyla.solutions.core.util.Debugger;
import org.apache.geode.cache.Region;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
public class OrderProcessor
{
    private final OrderJdbcRepository dao;
    private final Region<Integer, Set<ProductAssociate>> productAssociationsRegion;
    private final Region<String,Set<CustomerFavorites>> customerFavoritesRegion;
    private final CustomerRepository customerRepository;
    private final CustomerProductRepository customerProductRepository;
    private final ProductRepository productRepository;

    public OrderProcessor(OrderJdbcRepository dao, Region<Integer, Set<ProductAssociate>> productAssociationsRegion,
                          Region<String, Set<CustomerFavorites>> customerFavoritesRegion, CustomerRepository customerRepository, CustomerProductRepository customerProductRepository, ProductRepository productRepository)
    {
        this.dao = dao;
        this.productAssociationsRegion = productAssociationsRegion;
        this.customerFavoritesRegion = customerFavoritesRegion;
        this.customerRepository = customerRepository;
        this.customerProductRepository = customerProductRepository;
        this.productRepository = productRepository;
    }

    public int processOrder(OrderDTO order)
    {
        Debugger.println(this,"process Order %s",order);

        //insert into order_times
        Collection<Product> products = dao.saveOrder(order);

        constructProductAssociations(products);

        if(products == null || products.isEmpty())
            return 0;


        //calculate

        this.customerRepository.updateCustomerFavorites();

        //populate region
        this.cacheCustomerFavorites(order.getCustomerIdentifier());

        return products.size();
    }


    private void constructProductAssociations(Collection<Product> products)
    {

        if(products != null)
        {
            for (Product product : products)
            {
                Set<ProductAssociate> productAssociation = productRepository.findProductAssociates(product);

                if(productAssociation == null || productAssociation.isEmpty())
                    continue;

                this.productAssociationsRegion.put(product.getProductId(),productAssociation);
            }
        }
    }

    private void cacheCustomerFavorites(CustomerIdentifier customerIdentifier)
    {
        Set<CustomerFavorites> cf = customerProductRepository.findCustomerFavoritesByIdentifier(customerIdentifier);
        this.customerFavoritesRegion.put(customerIdentifier.getKey(), cf);
    }

}
