package com.vmware.data.demo.retail.store.analytics.streams.consumers;

import com.vmware.data.demo.retail.store.api.order.BeaconService;
import com.vmware.data.demo.retail.store.domain.BeaconRequest;

import java.util.function.Consumer;

public class BeaconRequestConsumer implements Consumer<BeaconRequest>
{
    private final BeaconService service;

    public BeaconRequestConsumer(BeaconService service)
    {
        this.service = service;
    }

    @Override
    public void accept(BeaconRequest beaconRequest)
    {
        service.processBeaconRequest(beaconRequest);
    }
}
