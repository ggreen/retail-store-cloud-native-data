package com.vmware.data.demo.retail.store.api.customer;

import com.vmware.data.demo.retail.store.domain.CustomerIdentifier;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerJdbcRepositoryTest
{
    @Mock
    private JdbcTemplate jdbcTemplate;

    private CustomerJdbcRepository subject;

    @BeforeEach
    public void setUp()
    {
        subject = new CustomerJdbcRepository(jdbcTemplate);
    }

    @Test
    void findCustomerId()
    {

        int expected = 23;
        CustomerIdentifier customerIdentifier = JavaBeanGeneratorCreator.of(CustomerIdentifier.class).create();


        when(jdbcTemplate.queryForObject(anyString(), Mockito.any(Class.class),any())).thenReturn(expected);

        var actual = subject.findCustomerId(customerIdentifier);


        assertEquals(expected,actual);
    }
}