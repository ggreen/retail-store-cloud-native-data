package com.vmware.data.demo.retail.store.api.order;

import com.vmware.data.demo.retail.store.api.customer.CustomerRepository;
import com.vmware.data.demo.retail.store.api.order.product.CustomerProductRepository;
import com.vmware.data.demo.retail.store.api.order.product.ProductRepository;
import com.vmware.data.demo.retail.store.domain.*;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.util.Organizer;
import nyla.solutions.core.util.Text;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDataServiceTest
{
    private OrderDataService subject;

    @Mock
    private OrderJdbcRepository dao;

    @Mock
    private Region<String, Set<CustomerFavorites>> customerFavoritesRegion;

    @Mock
    private Region<Integer, Set<ProductAssociate>> productAssociationsRegion;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerProductRepository customerProductRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private Region<String, Set<Product>> beaconProductsRegion;

    @Mock
    private Region<String, Set<Promotion>> customerPromotionsRegion;

    @Mock
    private Region<String, Set<Promotion>> beaconPromotionsRegion;

    @BeforeEach
    public void setup()
    {
        subject = new OrderDataService(dao, customerFavoritesRegion,  productAssociationsRegion, customerRepository,
                customerProductRepository, productRepository, beaconProductsRegion, customerPromotionsRegion,
                beaconPromotionsRegion);


        OrderDTO order = new OrderDTO();
        order.setCustomerIdentifier(new CustomerIdentifier());
        order.getCustomerIdentifier().setFirstName("nyla");
        order.getCustomerIdentifier().setLastName("nyla");

        Integer[] productIds = {1};

        order.setProductIds(productIds);
    }
    @Test
    public void processOrderCSV_When_SingleLine()
    {
        var csv ="\"0\",\"Nyla\",\"Nyla\",Email,\"77-777\",\"1,2\"";

        var orders = subject.processOrderCSV(csv);

        assertTrue(orders !=null && !orders.isEmpty());

        OrderDTO order = orders.iterator().next();

        Integer [] expected = {1,2};

        assertEquals(Arrays.asList(expected), Arrays.asList(order.getProductIds()));

    }//------------------------------------------------
    @Test
    public void processOrderCSV_When_multiple_lines()
    throws Exception
    {
        var csv = IO.readFile("src/test/resources/test.csv");

        assertTrue(csv != null && csv.trim().length() > 0,"csv:"+csv);

        var orders = subject.processOrderCSV(csv);

        assertTrue(orders !=null && !orders.isEmpty());

        assertEquals(2, orders.size());

        OrderDTO order = orders.iterator().next();

        Integer [] expected = {1,2,3};

        assertEquals(Arrays.asList(expected), Arrays.asList(order.getProductIds()));

    }


    @Test
    public void processOrderCSV() throws Exception
    {
        OrderDTO orderDTO = new OrderDTO();
        //userId,firstName,lastName,email,phone,productIds

        for (int i = 0; i < 3; i++)
        {
            orderDTO.setCustomerIdentifier(new CustomerIdentifier());
            orderDTO.getCustomerIdentifier().setFirstName("firstName"+i);
            orderDTO.getCustomerIdentifier().setLastName("lastName"+i);
            orderDTO.getCustomerIdentifier().setEmail("email"+i);
            orderDTO.getCustomerIdentifier().setKey("key"+i);

            orderDTO.setProductIds(Organizer.toIntegers(Arrays.asList(i).toArray()));

            String csv = CsvWriter.toCSV(orderDTO.getCustomerIdentifier().getKey(),
                    orderDTO.getCustomerIdentifier().getFirstName(),
                    orderDTO.getCustomerIdentifier().getLastName(),
                    orderDTO.getCustomerIdentifier().getEmail(),
                    orderDTO.getCustomerIdentifier().getMobileNumber(),
                    Text.mergeArray(",",orderDTO.getProductIds()));

            OrderDTO out = subject.processOrderCSV(csv).iterator().next();

            assertArrayEquals(out.getProductIds(),orderDTO.getProductIds());
        }
    }

    @Test
    void process()
    {
        BeaconRequest br = JavaBeanGeneratorCreator.of(BeaconRequest.class).create();

        Set<CustomerFavorites> customerFavorites = mock(Set.class);
        when(customerProductRepository.findCustomerFavoritesByIdentifier(any())).thenReturn(customerFavorites);
        subject.processBeaconRequest(br);

        verify(customerProductRepository).findCustomerFavoritesByIdentifier(any());
        verify(customerFavoritesRegion).put(anyString(),any());
        verify(productRepository).findProductsByBeacon(any());
    }

    @Test
    void cacheCustomerFavorites_WhenEmpty_ThenDoNota()
    {
        CustomerIdentifier doesNotExist = JavaBeanGeneratorCreator.of(CustomerIdentifier.class).create();

        subject.cacheCustomerFavorites(doesNotExist);

        verify(customerFavoritesRegion,never()).put(anyString(),any());
    }

}