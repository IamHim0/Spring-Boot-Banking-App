package com.cfkiatong.springbootbankingapp.config;

import com.cfkiatong.springbootbankingapp.security.CustomAuthenticationEntryPoint;
import com.cfkiatong.springbootbankingapp.security.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint customAuthEntryPoint;

    public SecurityConfiguration(UserDetailsService userDetailsService, JwtFilter jwtFilter, CustomAuthenticationEntryPoint customAuthEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.customAuthEntryPoint = customAuthEntryPoint;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //Disable CSRF token
        http.csrf(csrf -> csrf.disable());

        //Enable authentication
        http.authorizeHttpRequests(
                requests ->
                        requests
                                .requestMatchers("/api/v1/users", "/api/v1/auth/login")
                                .permitAll() //Waves authentication for login & register endpoints
                                .anyRequest().authenticated());

        //Postman log-in
        http.httpBasic(Customizer.withDefaults());

        //Custom AuthenticationEntryPoint
        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(customAuthEntryPoint));

        http.sessionManagement(
                        session
                                -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        //Web form log-in
//        http.formLogin(Customizer.withDefaults());

        return http.build();
    }


}
