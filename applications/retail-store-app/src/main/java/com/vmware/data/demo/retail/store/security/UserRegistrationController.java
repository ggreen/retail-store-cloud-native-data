package com.vmware.data.demo.retail.store.security;

import com.vmware.data.services.gemfire.spring.security.SpringSecurityUserService;
import com.vmware.data.services.gemfire.spring.security.data.RegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserRegistrationController
{
	@Autowired
	SpringSecurityUserService userService;
	
	String[] roles  = {"ROLE_WRITE","ROLE_READ"};
	
	@PostMapping("registerUser")
	public String registerUser(@RequestBody RegistrationDTO dto)
	{
		userService.registerUser(dto.toUserDetails(roles));
	
		return "true";
	}

}
