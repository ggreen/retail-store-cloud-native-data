package com.vmware.data.demo.retail.store.api.order;

import com.vmware.data.demo.retail.store.domain.CustomerIdentifier;
import com.vmware.data.demo.retail.store.domain.OrderDTO;
import nyla.solutions.core.io.csv.CsvReader;
import nyla.solutions.core.util.Debugger;
import nyla.solutions.core.util.Text;

import java.util.List;

/**
 * The Market CSV builder
 * @author Gregory Green
 *
 */
public class RetailOrderCsvBuilder
{
	private CustomerIdentifier customerIdentifier;
	private Integer[] productIds;
	private OrderDTO orderDTO;
	
	/**
	 * 
	 * @param csvLine the CSV format userId,firstName,lastName,email,phone,productIds";
	 */
	public void buildOrderLine(String csvLine)
	{
		
		if(csvLine == null|| csvLine.length() == 0)
			return;
		
		
		List<String> cells = CsvReader.parse(csvLine);
		
		int length  = cells.size();
		
		int i=0;
		String key = length > i ? cells.get(i) : null;
		i++;
		
		String firstName = length > i ? cells.get(i) : null;
		i++;
		
		String lastName  = length > i ? cells.get(i) : null;
		i++;
		
		String email = length > i ? cells.get(i) : null;
		i++;
		
		String mobileNumber = length > i ? cells.get(i) : null;
		i++;

		String text = length > i ? cells.get(i) : null;
		
		if(text == null || text.length() ==0)
		{
			Debugger.printInfo(this,"No product ids provided:"+csvLine);
			throw new IllegalArgumentException("No product ids provided:"+csvLine);
		}
	
		
		productIds = Text.splitRE(text, ",",Integer.class);
		
		if(productIds == null || productIds.length ==0 )
		{
			throw new IllegalArgumentException("Product ids are null or empty:"+csvLine);
		}
		
		customerIdentifier = new CustomerIdentifier(key, firstName, lastName, email,mobileNumber);
		orderDTO = new OrderDTO(customerIdentifier, productIds);
		
	}
	
	/**
	 * @return the customerIdentifier
	 */
	public CustomerIdentifier getCustomerIdentifier()
	{
		return customerIdentifier;
	}
	/**
	 * @return the productIds
	 */
	public Integer[] getProductIds()
	{
		return productIds;
	}
	/**
	 * @return the orderDTO
	 */
	public OrderDTO getOrderDTO()
	{
		return orderDTO;
	}

	String cleanCsv(String csv)
	{
		if(csv == null)
			return "";
		
		//csv = Text.replaceFirstRegExpWith(csv,".*UFT-8", "")
                return "";
		
	}


	

}
