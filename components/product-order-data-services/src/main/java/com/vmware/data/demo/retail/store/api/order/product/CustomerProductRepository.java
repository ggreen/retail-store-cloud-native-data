package com.vmware.data.demo.retail.store.api.order.product;

import com.vmware.data.demo.retail.store.domain.CustomerFavorites;
import com.vmware.data.demo.retail.store.domain.CustomerIdentifier;

import java.util.Set;

/**
 * CustomerProducTRepository
 *
 * @author Gregory Green
 */
public interface CustomerProductRepository
{
    Set<CustomerFavorites> findCustomerFavoritesByIdentifier(CustomerIdentifier customerIdentifier);
}
