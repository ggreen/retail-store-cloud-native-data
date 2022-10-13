package com.vmware.data.demo.retail.store.api.order.product;

import com.vmware.data.demo.retail.store.domain.Beacon;
import com.vmware.data.demo.retail.store.domain.Product;
import com.vmware.data.demo.retail.store.domain.ProductAssociate;
import com.vmware.data.demo.retail.store.domain.Promotion;

import java.util.Set;

/**
 * ProductRepository
 *
 * @author Gregory Green
 */
public interface ProductRepository
{
    Set<Product> findProductsByBeacon(Beacon beacon);

    Set<Promotion> findPromotionsByProduct(Product product);

    Product findProductById(int productId);

    java.util.List<Integer> findProductIds();

    Set<ProductAssociate> findProductAssociates(Product product);
}
