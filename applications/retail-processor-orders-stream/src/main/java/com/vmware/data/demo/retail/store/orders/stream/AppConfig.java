package com.vmware.data.demo.retail.store.orders.stream;

import com.vmware.data.demo.retail.store.api.order.OrderDataService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan(basePackageClasses = {OrderDataService.class})
public class AppConfig
{
//    @Bean
//	public OrderDataService orderMgmt(OrderJdbcRepository orderDao,
//									  Region<String, Set<CustomerFavorites>> customerFavoritesRegion,
//									  Region<Integer, Set<ProductAssociate>> productAssociationsRegion, CustomerJdbcRepository customerRepository)
//	{
//		return new OrderDataService(orderDao,
//				customerFavoritesRegion,
//				productAssociationsRegion,
//                customerRepository, customerProductRepository);
//	}
}
