package com.vmware.data.demo.retail.store.api.customer;

import com.vmware.data.demo.retail.store.domain.CustomerIdentifier;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * CustomerRepository
 *
 * @author Gregory Green
 */
public interface CustomerRepository
{
    int findCustomerId(CustomerIdentifier customerIdentifier)
    throws EmptyResultDataAccessException;

    int saveCustomerByIdentifier(CustomerIdentifier customerIdentifier);

    int updateCustomerFavorites();
}
