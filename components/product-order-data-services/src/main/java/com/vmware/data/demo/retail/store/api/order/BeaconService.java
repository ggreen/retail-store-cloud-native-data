package com.vmware.data.demo.retail.store.api.order;

import com.vmware.data.demo.retail.store.domain.BeaconRequest;

/**
 * BeaconService
 *
 * @author Gregory Green
 */
public interface BeaconService
{
    void processBeaconRequest(BeaconRequest br);

}
