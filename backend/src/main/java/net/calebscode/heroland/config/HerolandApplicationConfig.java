package net.calebscode.heroland.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
public class HerolandApplicationConfig {
	
	@Bean
	public Gson applicationDefaultGson() {
		return new GsonBuilder().create();
	}
	
	@Bean
	public ThreadPoolTaskExecutor herolandTaskExecutor() {
		var exec = new ThreadPoolTaskExecutor();
		return exec;
	}
	
}
