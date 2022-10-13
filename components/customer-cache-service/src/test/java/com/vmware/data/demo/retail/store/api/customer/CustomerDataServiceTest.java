package com.vmware.data.demo.retail.store.api.customer;

import com.vmware.data.demo.retail.store.domain.CustomerFavorites;
import com.vmware.data.demo.retail.store.domain.Promotion;
import com.vmware.data.services.gemfire.spring.security.SpringSecurityUserService;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerDataServiceTest
{
    private CustomerDataService subject;
    @Mock
    Region<String, Collection<CustomerFavorites>> customerFavoritesRegion;

    @Mock
    Region<String, String> customerLocationRegion;

    @Mock
    Region<String, Collection<Promotion>> beaconPromotionsRegion;

    @Mock
    private SpringSecurityUserService springSecurityUserService;

    private Collection<Promotion> expectedPromotions = Collections.singleton(new Promotion());

    private String expectedBeacon = "beaconId";

    @BeforeEach
    public void setup()
    {
        subject = new CustomerDataService(customerFavoritesRegion,
                customerLocationRegion,
                beaconPromotionsRegion,
                springSecurityUserService);

    }


    @Test
    public void testGetCustomerLocation()
    {

        String userName = "imani";

        when(customerLocationRegion.get("imani")).thenReturn(expectedBeacon);
        String beacon = subject.whereIsCustomer(userName);
        assertNotNull(beacon);


    }//------------------------------------------------

    @Test
    public void testByPromotionsByUser()
    {
        String expectedLocation = "imani";


        assertNull(this.subject.findPromotionsByUserName("ggreen"));

        when(customerLocationRegion.get(any())).thenReturn(expectedLocation);
        when(beaconPromotionsRegion.get(any())).thenReturn(expectedPromotions);
        assertNotNull(this.subject.findPromotionsByUserName("imani"));
    }//------------------------------------------------


    @Test
    public void testSaveCustomerAtBeaconId()
    {
        String name = "ggreen";
        String beaconId = "1212";

        this.subject.saveCustomerAtBeaconId(name, beaconId);

    }
}