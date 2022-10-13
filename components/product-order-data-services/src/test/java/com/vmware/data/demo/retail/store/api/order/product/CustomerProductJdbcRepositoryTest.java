package com.vmware.data.demo.retail.store.api.order.product;

import com.vmware.data.demo.retail.store.api.customer.CustomerRepository;
import com.vmware.data.demo.retail.store.domain.CustomerFavorites;
import com.vmware.data.demo.retail.store.domain.CustomerIdentifier;
import com.vmware.data.demo.retail.store.domain.ProductQuantity;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Organizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerProductJdbcRepositoryTest
{
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ProductJdbcRepository productJdbcRepository;


    @Mock
    private CustomerRepository customerRepository;


    private CustomerProductJdbcRepository subject;

    @BeforeEach
    public void setUp()
    {
        subject = new CustomerProductJdbcRepository(productJdbcRepository,jdbcTemplate,customerRepository);
    }

    @Test
    public void selectCustomerFavorites()
    {
        Integer expectedCustomerId = 3;
        CustomerFavorites expectedCustomer = new JavaBeanGeneratorCreator<CustomerFavorites>(CustomerFavorites.class)
                .randomizeAll().create();
        ProductQuantity expectedPQ = new JavaBeanGeneratorCreator<ProductQuantity>(ProductQuantity.class)
                .randomizeAll().generateNestedAll().create();

        Collection<ProductQuantity> expectedProductQuantity = Organizer.toList(expectedPQ);

        expectedCustomer.setProductQuanties(expectedProductQuantity);

        List<CustomerFavorites> expectedCustomers = Organizer.toList(expectedCustomer);


        //int customerId = this.jdbcTemplate.queryForObject(sql,Integer.class,firstName,lastName);
//        when(jdbcTemplate.queryForObject(anyString(),any(Class.class),anyString(),anyString())).thenReturn(expectedCustomerId);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class),anyInt())).thenReturn(expectedCustomers);



        CustomerIdentifier customer = new CustomerIdentifier();
        customer.setFirstName("Joe");
        customer.setLastName("Smith");


        CustomerFavorites cp = subject.findCustomerFavoritesByIdentifier(customer).iterator().next();
        assertNotNull(cp);

        assertTrue(cp.getProductQuanties() != null && !cp.getProductQuanties().isEmpty());

        assertTrue(cp
                .getProductQuanties()
                .stream()
                .allMatch(p -> p.getProduct() != null && p.getProduct().getProductName() != null && p.getProduct().getProductName().length() > 0));

    }
}