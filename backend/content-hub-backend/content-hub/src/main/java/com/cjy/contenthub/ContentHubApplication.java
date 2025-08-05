package com.cjy.contenthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cjy.contenthub.*"})
public class ContentHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentHubApplication.class, args);
	}

}
