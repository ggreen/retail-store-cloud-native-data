package com.vmware.data.demo.retail.store.api.order;

import com.vmware.data.demo.retail.store.api.customer.CustomerRepository;
import com.vmware.data.demo.retail.store.api.order.product.ProductJdbcRepository;
import com.vmware.data.services.gemfire.io.QuerierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderJdbcRepositoryTest
{
    @Mock
    private JdbcTemplate jdbcTemplate;

    private ProductJdbcRepository productDao;
    private QuerierService querierService;

    private CustomerRepository customerRepository;

    private OrderJdbcRepository subject;

    @BeforeEach
    public void setUp()
    {
        subject = new OrderJdbcRepository(jdbcTemplate, productDao, querierService,
                customerRepository);
    }

    @Test
    void queryOrderCount()
    {
        long expected = 3;
        when(jdbcTemplate.queryForObject(anyString(),any(Class.class))).thenReturn(expected);
        assertEquals(expected,subject.countOrders());
    }
}