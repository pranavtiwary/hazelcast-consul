package com.pranav.web;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

/**
 * Application : Entry point in app
 * 
 * @author pranav.tiwary@vuclip.com
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan({ "com.pranav.web" })
@EnableTransactionManagement
@EnableJpaRepositories
@EnableWebMvc
public class Application {

	public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	
	@Bean
    ObjectMapper customizeJacksonConfiguration() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GuavaModule());
        return om;
    }
}
