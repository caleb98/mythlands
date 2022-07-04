package net.mythlands.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.mythlands.core.NameGenerator;

@Configuration
public class MythlandsApplicationConfig {
	
	@Bean
	public Gson applicationDefaultGson() {
		return new GsonBuilder()
				.serializeNulls()
				.create();
	}
	
	@Bean
	public NameGenerator bossNameGenerator() {
		var gen = new NameGenerator("102(0.5:(0.2:1)02)(0.3:[1: the Broken,1: the Deprived])");
		
		gen.addClass(
				'0', 
				"a", "e", "i", "o", "u", "ai", "ou", "ei", "eo"
		);
		gen.addClass(
				'1', 
				"p", "b", "t", "d", "f", "v", "s", "z", "m", "n", "ch", "k", "g"
		);
		gen.addClass(
				'2', 
				"ch", "th", "gh", "k", "ly", "ph", "ng", "d", "r", "nd", "x", "z", "hz", "f"
		);
		
		return gen;
	}
	
}
