package com.vmware.data.demo.retail.store.analytics.streams;

import com.vmware.data.demo.retail.store.analytics.streams.consumers.BeaconRequestConsumer;
import com.vmware.data.demo.retail.store.analytics.streams.consumers.OrderConsumer;
import com.vmware.data.demo.retail.store.api.customer.CustomerJdbcRepository;
import com.vmware.data.demo.retail.store.api.order.BeaconService;
import com.vmware.data.demo.retail.store.api.order.OrderService;
import com.vmware.data.demo.retail.store.api.order.product.ProductCacheLoader;
import com.vmware.data.demo.retail.store.domain.BeaconRequest;
import com.vmware.data.demo.retail.store.domain.OrderDTO;
import nyla.solutions.core.patterns.workthread.ExecutorBoss;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.function.Consumer;

@Configuration
@EnableTransactionManagement
@EnableScheduling
@ComponentScan(basePackageClasses = {CustomerJdbcRepository.class, BeaconService.class,OrderService.class})
public class AppConf
{

	/**
	 * Working thread executor pattern
	 * @param env the spring configurations
	 * @return the executor boss
	 */
	@Bean
	public ExecutorBoss boss(Environment env)
	{
		return new ExecutorBoss(env.getProperty("bossThreads",Integer.class,10));
	}//------------------------------------------------


    @Bean
    public Consumer<OrderDTO> orders(OrderService orderService) {
        return new OrderConsumer(orderService);
    }

    @Bean
    public Consumer<BeaconRequest> beaconRequests(BeaconService beaconService)
    {
        return new BeaconRequestConsumer(beaconService);
    }

    @Bean
    public CommandLineRunner runner(ProductCacheLoader productCacheLoader)
	{
		CommandLineRunner runner = (args) -> {
			productCacheLoader.loadProductsCache();
		};

		return runner;
	}
}
