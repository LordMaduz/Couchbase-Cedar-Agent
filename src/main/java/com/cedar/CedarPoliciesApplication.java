package com.cedar;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "DBS Cedar Agent", version = "1.0", description = "DBS Cedar Agent"))
public class CedarPoliciesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CedarPoliciesApplication.class, args);
	}

}
