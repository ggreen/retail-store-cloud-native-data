package com.vmware.data.demo.retail.store.api.order;

import com.vmware.data.demo.retail.store.api.customer.CustomerRepository;
import com.vmware.data.demo.retail.store.api.order.product.CustomerProductRepository;
import com.vmware.data.demo.retail.store.api.order.product.ProductRepository;
import com.vmware.data.demo.retail.store.domain.CustomerFavorites;
import com.vmware.data.demo.retail.store.domain.OrderDTO;
import com.vmware.data.demo.retail.store.domain.Product;
import com.vmware.data.demo.retail.store.domain.ProductAssociate;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Organizer;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessorTest
{
    private OrderDTO dto;
    private OrderProcessor subject;


    @Mock
    private OrderJdbcRepository dao;

    @Mock
    private Region<Integer, Set<ProductAssociate>> productAssociationsRegion;

    @Mock
    private Region<String,Set<CustomerFavorites>> customerFavoritesRegion;

    private Set<ProductAssociate> productAssociation;
    private ProductAssociate productAssociate;

    private Product product;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerProductRepository customerProductRepository;

    @Mock
    private ProductRepository productRepository;


    @BeforeEach
    void setUp()
    {
        dto = JavaBeanGeneratorCreator
                .of(OrderDTO.class).create();

        product = JavaBeanGeneratorCreator.of(Product.class).create();
        productAssociate = JavaBeanGeneratorCreator.of(ProductAssociate.class).create();
        productAssociation = Organizer.toSet(productAssociate);

        subject = new OrderProcessor(dao,productAssociationsRegion,customerFavoritesRegion, customerRepository,
                customerProductRepository, productRepository);
    }



    @Nested
    class ProcessOrder
    {


        @Test
        void processOrder_insert()
        {

            subject.processOrder(dto);

            verify(dao).saveOrder(dto);
            verify(productRepository,never()).findProductAssociates(any());
            verify(customerProductRepository,never()).findCustomerFavoritesByIdentifier(any());
            verify(productAssociationsRegion,never()).put(any(),any());
        }

        @Test
        void processOrder_SelectProductAssociation()
        {
            when(dao.saveOrder(any())).thenReturn(Arrays.asList(product));

            subject.processOrder(dto);

            verify(dao).saveOrder(dto);
            verify(productRepository).findProductAssociates(any());
        }

        @Test
        void processOrder_selectCustomerFavorites()
        {
            when(dao.saveOrder(any())).thenReturn(Arrays.asList(product));
            when(productRepository.findProductAssociates(product)).thenReturn(productAssociation);

            subject.processOrder(dto);
            verify(productAssociationsRegion).put(anyInt(),any());
            verify(customerRepository).updateCustomerFavorites();
            verify(customerProductRepository).findCustomerFavoritesByIdentifier(any());
            verify(productAssociationsRegion).put(any(),any());
        }


    }
}