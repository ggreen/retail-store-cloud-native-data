package com.vmware.data.demo.retail.store.api.customer;

import com.vmware.data.demo.retail.store.domain.CustomerFavorites;
import com.vmware.data.demo.retail.store.domain.CustomerIdentifier;
import com.vmware.data.demo.retail.store.domain.Promotion;

import java.util.Collection;

/**
 * CustomerService
 *
 * @author Gregory Green
 */
public interface CustomerService
{
    boolean isAtCheckout(String userName);

    Collection<Promotion> findPromotionsByUserName(String userName);

    void saveCustomerAtBeaconId(String name, String beaconId);

    CustomerIdentifier findCustomerIdentifierByUsername(String name);

    Collection<CustomerFavorites> findFavorites(String name);

    String whereIsCustomer(String userName);

    void clearCustomerLocation(String remoteUser);
}
