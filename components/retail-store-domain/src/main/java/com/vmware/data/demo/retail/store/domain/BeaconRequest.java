package com.vmware.data.demo.retail.store.domain;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class BeaconRequest {
	private CustomerIdentifier customerId;
	private String deviceId;
	private String uuid;
	private int major;
	private int minor;
	private int signalPower;
	
}