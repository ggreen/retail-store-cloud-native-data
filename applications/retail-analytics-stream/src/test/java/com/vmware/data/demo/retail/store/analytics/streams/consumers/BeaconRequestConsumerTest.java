package com.vmware.data.demo.retail.store.analytics.streams.consumers;

import com.vmware.data.demo.retail.store.api.order.BeaconService;
import com.vmware.data.demo.retail.store.domain.BeaconRequest;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BeaconRequestConsumerTest
{
    @Mock
    private BeaconService beaconService;
    @Test
    void accept()
    {

        BeaconRequestConsumer subject = new BeaconRequestConsumer(beaconService);
        BeaconRequest beaconRequest= JavaBeanGeneratorCreator.of(BeaconRequest.class)
                                                             .create();
        subject.accept(beaconRequest);

        verify(beaconService).processBeaconRequest(beaconRequest);
    }
}