package com.vmware.data.demo.retail.store.controllers;

import com.vmware.data.demo.retail.store.api.customer.CustomerService;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class PresentationController
{

	 private final CustomerService customerService;
	 
	private final Environment env;

	public PresentationController(CustomerService customerService, Environment env)
	{
		this.customerService = customerService;
		this.env = env;
	}

	/**
	 * Present the HomePage
	 * @param request the HTTP request
	 * @param model the MVC mode
	 * @return the template to present
	 */
	@PreAuthorize("hasRole('ROLE_WRITE')")
	@RequestMapping("/")
    public String homePage(HttpServletRequest request, Model model) 
    {
		model.addAttribute("userName", request.getUserPrincipal().getName());
		
		customerService.clearCustomerLocation(request.getRemoteUser());
		
	    return "main";
    }
	/**
	 * View the store
	 * @param user the user
	 * @param model the model
	 * @return the template to present
	 */
	@PreAuthorize("hasRole('ROLE_WRITE')")
	@RequestMapping("/store")
    public String storePage(Principal user,Model model) 
    {
		model.addAttribute("userName", user.getName());
		return "store";
    }
	
	@RequestMapping("/login")
    public String loginPage(Model model) 
    {
		return "login";
    }
	
	@RequestMapping("/register")
    public String registerPage(Model model) 
    {
		return "register";
    }
	@RequestMapping("/uregister")
    public String uregister(Model model) 
    {
		return "uregister";
    }
}
