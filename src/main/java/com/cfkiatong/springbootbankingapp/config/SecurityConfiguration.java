package com.cfkiatong.springbootbankingapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    UserDetailsService userDetailsService;

    public SecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //Disable CSRF token
        http.csrf(csrf -> csrf.disable());

        //Enable authentication
        http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated());

        //Postman log-in
        http.httpBasic(Customizer.withDefaults());

        http.sessionManagement(
                session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

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
