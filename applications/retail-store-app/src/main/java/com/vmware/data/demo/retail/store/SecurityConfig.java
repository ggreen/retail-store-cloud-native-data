package com.vmware.data.demo.retail.store;

import com.vmware.data.services.gemfire.client.GemFireClient;
import com.vmware.data.services.gemfire.spring.security.GemFireUserDetailsService;
import com.vmware.data.services.gemfire.spring.security.SpringSecurityUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig
{
    /**
     *
     * @param gemFireClient the geode client
     * @return the user details service
     */
    @Bean
    public SpringSecurityUserService userDetailsService(GemFireClient gemFireClient)
    {
        return new GemFireUserDetailsService(gemFireClient.getRegion("users"));
    }//------------------------------------------------
}
