package com.lti.data.recasttableaumigrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class RecastTableauMigratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecastTableauMigratorApplication.class, args);
	}

}
