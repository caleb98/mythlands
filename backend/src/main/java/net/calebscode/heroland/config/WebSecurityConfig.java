package net.calebscode.heroland.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import net.calebscode.heroland.service.HerolandUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic().and()
			.csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.and()
			.authorizeRequests()
				.antMatchers("/character/**").authenticated()
				.antMatchers("/**").permitAll()
				.and()
			.formLogin()
				.loginPage("/").permitAll()
				.successHandler(authenticationSuccessHandler())
				.failureHandler(authenticationFailureHandler())
				.and()
			.logout()
				.logoutSuccessHandler(logoutSuccessHandler());
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
	
	@Bean
	@Override
	protected UserDetailsService userDetailsService() {
		return new HerolandUserDetailsService();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return (request, response, authentication) -> {
			response.setContentType("text/html;charset=UTF-8");
			response.addHeader("login-error", "false");
		};
	}
	
	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return (request, response, exception) -> {
			response.addHeader("login-error", "true");
		};
	}
	
	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return (request, response, authentication) -> {
			response.setStatus(HttpServletResponse.SC_OK);
		};
	}
	
}
