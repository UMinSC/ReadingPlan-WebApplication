package ca.sheridancollege.liu198.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@EnableWebSecurity
@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityfilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
		MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);
		return http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(mvc.pattern("/admin/**")).hasRole("ADMIN")
				.requestMatchers(mvc.pattern("/client/**")).hasRole("CLIENT")
				.requestMatchers(mvc.pattern("/")).permitAll()
				.requestMatchers(mvc.pattern("/js/**")).permitAll()
				.requestMatchers(mvc.pattern("/css/**")).permitAll()
				.requestMatchers(mvc.pattern("/images/**")).permitAll()
				.requestMatchers(mvc.pattern("/**")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST,"/register")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/register")).permitAll()
				.requestMatchers(mvc.pattern("/permission-denied")).permitAll()
				.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()// for h2 database
				.requestMatchers(mvc.pattern("/**")).denyAll())
				.csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).disable()) // for h2 database
				.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
				.formLogin(form -> form.loginPage("/login").permitAll())
				.exceptionHandling(exception -> exception.accessDeniedPage("/permission-denied"))
				.logout(logout -> logout.permitAll()).build();
	}


    @Bean
    public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

}
