package com.vmware.data.demo.retail.store.api.order.product;

import com.vmware.data.demo.retail.store.domain.Beacon;
import com.vmware.data.demo.retail.store.domain.Product;
import com.vmware.data.demo.retail.store.domain.ProductAssociate;
import com.vmware.data.demo.retail.store.domain.Promotion;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Organizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductJdbcRepositoryTest
{
    private  Product product;


    @Mock
    private JdbcTemplate jdbcTemplate;

    private ProductJdbcRepository subject;

    @BeforeEach
    void setUp()
    {
        product = JavaBeanGeneratorCreator
                .of(Product.class).create();
        subject = new ProductJdbcRepository(jdbcTemplate);
    }

//    @Test
//    void selectProductsByBeacon()
//    {
//        Beacon beacon = JavaBeanGeneratorCreator
//                .of(Beacon.class).create();
//
//        subject.findProductsByBeacon(beacon);
//    }

    @Test
    void selectPromotionsByProduct()
    {
        subject.findPromotionsByProduct(product);
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), any());
    }

    @Test
    public void findPromotionsByProduct_WhenPromotionsExists_ThenReturnPromotions()
    {
        Promotion expected = new JavaBeanGeneratorCreator<Promotion>(Promotion.class)
                .create();

        expected.setMarketingMessage("Bread");

        List<Promotion> list = Organizer.toList(expected);

        when(jdbcTemplate.query(anyString(),any(RowMapper.class),any()))
                .thenReturn(list);

        Product product = null;

        Collection<Promotion> promotions = subject.findPromotionsByProduct(product);

        assertNull(promotions);
        int wonderBreadId = 58;

        product = new Product();
        product.setProductId(wonderBreadId);

        promotions = subject.findPromotionsByProduct(product);

        assertNotNull(promotions);
        assertTrue(!promotions.isEmpty());

        assertTrue(promotions.stream().allMatch(p -> p.getMarketingMessage().contains("Bread")));
    }

    @Test
    void findProductById()
    {
        subject.findProductById(product.getProductId());
        verify(jdbcTemplate).query(anyString(), any(ResultSetExtractor.class));
    }

    @Test
    void selectProductIds()
    {
        List<Integer> expected = Arrays.asList(1);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(expected);
        List<Integer> actual = subject.findProductIds();

        assertEquals(expected,actual);
    }

    @Test
    void selectProductAssociates()
    {
        ProductAssociate productAssociation = JavaBeanGeneratorCreator.of(ProductAssociate.class).create();
        List<ProductAssociate> expected = Collections.singletonList(productAssociation);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(expected);
        Set<ProductAssociate> actual = subject.findProductAssociates(product);
        verify(jdbcTemplate).query(anyString(),any(RowMapper.class));
    }

    @Test
    public void selectProductsByBeacon()
    {
        Product expected =  new JavaBeanGeneratorCreator<Product>(Product.class)
                .randomizeAll().create();

        List<Product> list = Organizer.toList(expected);

        when(jdbcTemplate.query(anyString(),any(Object[].class),any(RowMapper.class)))
                .thenReturn(list);

        Beacon beacon = new Beacon();
        int major = -1;
        int minor = -1;
        String uuid = "2";
        beacon.setMajor(major);
        beacon.setMinor(minor);
        beacon.setUuid(uuid);

        Collection<Product> products = subject.selectProductsByBeacon(beacon);
        assertNotNull(products);
        assertTrue(!products.isEmpty());
    }
}