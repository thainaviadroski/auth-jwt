package net.thaina.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(auth -> auth.requestMatchers("home").permitAll())
                .authorizeRequests(auth -> auth.requestMatchers("login").permitAll())
                .authorizeRequests(auth -> auth.anyRequest().authenticated())
                .cors(withDefaults());
        http.addFilterBefore(new SecurityFilters(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
