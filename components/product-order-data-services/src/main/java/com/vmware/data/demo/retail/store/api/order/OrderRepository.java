package com.vmware.data.demo.retail.store.api.order;

import com.vmware.data.demo.retail.store.domain.OrderDTO;
import com.vmware.data.demo.retail.store.domain.Product;

import java.util.Collection;

/**
 * OrderRepository
 *
 * @author Gregory Green
 */
public interface OrderRepository
{
    Collection<Product> saveOrder(OrderDTO order);

    long countOrders();
}
