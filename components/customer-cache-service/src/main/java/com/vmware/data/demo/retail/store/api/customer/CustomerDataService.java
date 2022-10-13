package com.vmware.data.demo.retail.store.api.customer;

import com.vmware.data.demo.retail.store.domain.CustomerFavorites;
import com.vmware.data.demo.retail.store.domain.CustomerIdentifier;
import com.vmware.data.demo.retail.store.domain.Promotion;
import com.vmware.data.services.gemfire.spring.security.SpringSecurityUserService;
import com.vmware.data.services.gemfire.spring.security.data.UserProfileDetails;
import org.apache.geode.cache.Region;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * CustomerDataService
 *
 * @author Gregory Green
 */
@Service
public class CustomerDataService implements CustomerService
{
    private final Region<String, Collection<CustomerFavorites>> customerFavoritesRegion;
    private final Region<String, String> customerLocationRegion;
    private final Region<String, Collection<Promotion>> beaconPromotionsRegion;
    private final SpringSecurityUserService springSecurityUserService;

    public CustomerDataService(Region<String, Collection<CustomerFavorites>> customerFavoritesRegion, Region<String,
            String> customerLocationRegion, Region<String, Collection<Promotion>> beaconPromotionsRegion,
                               SpringSecurityUserService springSecurityUserService)
    {
        this.customerFavoritesRegion = customerFavoritesRegion;
        this.customerLocationRegion = customerLocationRegion;
        this.beaconPromotionsRegion = beaconPromotionsRegion;
        this.springSecurityUserService = springSecurityUserService;
    }




    @Override
    public void saveCustomerAtBeaconId(String name, String beaconId)
    {
        this.customerLocationRegion.put(name, beaconId+"|0|0");
    }

    @Override
    public CustomerIdentifier findCustomerIdentifierByUsername(String userName)
    {

        UserProfileDetails upd = springSecurityUserService.findUserProfileDetailsByUserName(userName);

        if(upd == null)
            return null;


        return new CustomerIdentifier(userName, upd.getFirstName(), upd.getLastName(), upd.getEmail(),upd.getPhone());
    }

    @Override
    public Collection<CustomerFavorites> findFavorites(String userName)
    {
        if(userName == null )
            return null;

        return this.customerFavoritesRegion.get(userName);
    }
    public void clearCustomerLocation(String userName)
    {
        if(userName == null)
            return;

        this.customerLocationRegion.remove(userName);
    }

    @Override
    public Collection<Promotion> findPromotionsByUserName(String userName)
    {

        if(userName == null)
            return null;

        String beaconId = whereIsCustomer(userName);
        if(beaconId == null)
            return null;


        Collection<Promotion> promotions = whatArePromotions(beaconId);


        return promotions;
    }

    @Override
    public String whereIsCustomer(String userName)
    {
        if(userName == null)
            return null;

        return this.customerLocationRegion.get(userName);
    }
    Collection<Promotion> whatArePromotions(String beaconKey)
    {
        if(beaconKey == null)
            return null;

        return beaconPromotionsRegion.get(beaconKey);
    }//------------------------------------------------


    public boolean isAtCheckout(String userName)
    {

        return "5|0|0".equals(customerLocationRegion.get(userName));
    }
}
