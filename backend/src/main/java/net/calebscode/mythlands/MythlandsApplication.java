package net.calebscode.mythlands;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MythlandsApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(MythlandsApplication.class, args);
	}

}
