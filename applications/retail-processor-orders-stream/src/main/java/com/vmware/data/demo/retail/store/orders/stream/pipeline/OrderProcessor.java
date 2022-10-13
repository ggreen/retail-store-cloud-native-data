package com.vmware.data.demo.retail.store.orders.stream.pipeline;

import com.vmware.data.demo.retail.store.api.order.OrderService;
import com.vmware.data.demo.retail.store.domain.OrderDTO;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Function;

@Component
public class OrderProcessor implements Function<String,Collection<OrderDTO>>
{
    private final OrderService service;
    public OrderProcessor(OrderService service)
    {
        this.service = service;
    }

    public Collection<OrderDTO> apply(String csv) {

        System.out.println("Stream PROCESSING CSV:"+csv);

        try
        {
            System.out.println("ARGUMENTS:"+System.getProperty("sun.java.command"));
            System.out.println(" csv:"+csv);
            Collection<OrderDTO> order = service.processOrderCSV(csv);

            System.out.println("ORDER processed:"+order);

            return order;
        }
        catch(Exception e)
        {
            System.err.println("CANNOT process csv:"+csv);
            e.printStackTrace();
            throw e;
        }
    }
}
