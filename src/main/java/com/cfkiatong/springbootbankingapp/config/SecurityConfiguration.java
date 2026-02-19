package com.cfkiatong.springbootbankingapp.config;

import com.cfkiatong.springbootbankingapp.security.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfiguration(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());

        return provider;
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
                                .requestMatchers("/api/v1/accounts", "/api/v1/users", "/api/v1/auth/login")
                                .permitAll() //Waves authentication for login & register endpoints
                                .anyRequest().authenticated());

        //Postman log-in
        http.httpBasic(Customizer.withDefaults());

        http.sessionManagement(
                        session
                                -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        //Web form log-in
//        http.formLogin(Customizer.withDefaults());

        return http.build();
    }

    //Default Users implementation
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.
//                withDefaultPasswordEncoder().
//                username("uname").
//                password("password")
//                .roles("USER")
//                .build();
//
//        UserDetails user1 = User.
//                withDefaultPasswordEncoder().
//                username("uname1").
//                password("password1")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, user1);
//    }
}
