package com.vmware.data.demo.retail.store.analytics.streams.consumers;

import com.vmware.data.demo.retail.store.api.order.OrderService;
import com.vmware.data.demo.retail.store.domain.OrderDTO;

import java.util.function.Consumer;

public class OrderConsumer implements Consumer<OrderDTO>
{
    private final OrderService service;
    public OrderConsumer(OrderService service)
    {
        this.service = service;
    }

    @Override
    public void accept(OrderDTO orderDTO)
    {
        service.processOrder(orderDTO);
    }
}
