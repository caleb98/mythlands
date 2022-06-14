package net.calebscode.mythlands.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/global", "/topic", "/local");
		registry.setApplicationDestinationPrefixes("/game");
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
			.addEndpoint("/connect");
	}
	
	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages
			.nullDestMatcher().permitAll()
			
			// Message permissions
			.simpMessageDestMatchers("/global/**").hasRole("ADMIN")
			.simpMessageDestMatchers("/game/**").hasRole("PLAYER")
			.simpMessageDestMatchers("/topic/**").hasRole("PLAYER")
			.simpMessageDestMatchers("/local/**").hasRole("PLAYER")
			
			// Subscribe permissions
			.simpSubscribeDestMatchers("/global/chat").hasRole("PLAYER")
			.simpSubscribeDestMatchers("/global/**").permitAll()
			.simpSubscribeDestMatchers("/local/**").hasRole("PLAYER")
			.simpSubscribeDestMatchers("/user/**").hasRole("PLAYER");
	}
	
}
