package com.kaveh.transportationprovider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@Api(tags = "Health API")
@RequestMapping("/")
public class TransportationProviderApplication {

	@GetMapping("/")
	@ApiOperation(value = "Server health status")
	public String home() {
		return "App is running!";
	}

	public static void main(String[] args) {
		SpringApplication.run(TransportationProviderApplication.class, args);
	}

}
