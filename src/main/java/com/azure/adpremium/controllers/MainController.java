package com.azure.adpremium.controllers;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.azure.adpremium.security.AzureADAuthentication;

@RestController
public class MainController {

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public void authenticate(HttpServletRequest request)
			throws MalformedURLException, ServiceUnavailableException, InterruptedException, ExecutionException {
		System.out.println("authenticating");
		AzureADAuthentication.aadAuthentication(request);
	}
}
