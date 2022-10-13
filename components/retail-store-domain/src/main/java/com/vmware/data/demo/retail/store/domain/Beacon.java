package com.vmware.data.demo.retail.store.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Beacon {

	private String uuid;
	private int major;
	private int minor;
	private String category;
	private boolean entrance;
	private boolean checkout;

    public String getKey()
    {
    	return new StringBuilder().append(uuid).append("|")
				.append(major).append("|")
				.append(minor).toString();
    }
}
