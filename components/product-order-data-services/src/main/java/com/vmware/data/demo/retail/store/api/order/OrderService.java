package com.vmware.data.demo.retail.store.api.order;

import com.vmware.data.demo.retail.store.domain.OrderDTO;

import java.util.Collection;

/**
 * OrderService
 *
 * @author Gregory Green
 */
public interface OrderService
{
    int processOrder(OrderDTO order);


    Collection<OrderDTO> processOrderCSV(String csv);


    OrderDTO processOrderCSVLine(String csv);

}
