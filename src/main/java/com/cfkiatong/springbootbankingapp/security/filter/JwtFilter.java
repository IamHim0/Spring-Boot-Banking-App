package com.cfkiatong.springbootbankingapp.security.filter;

import com.cfkiatong.springbootbankingapp.entity.Account;
import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.security.JwtService;
import com.cfkiatong.springbootbankingapp.security.UserDetailsServiceImplementation;
import com.cfkiatong.springbootbankingapp.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    ApplicationContext context;
    AccountRepository accountRepository;

    JwtFilter(JwtService jwtService, ApplicationContext context, AccountRepository accountRepository) {
        this.jwtService = jwtService;
        this.context = context;
        this.accountRepository = accountRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String id;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            id = jwtService.extractId(token);
        } else {
            id = null;
        }

        if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            Account account =
                    accountRepository.findById(UUID.fromString(id))
                            .orElseThrow(() -> new AccountNotFoundException(UUID.fromString(id)));

            UserPrincipal userPrincipal = new UserPrincipal(account);

            if (jwtService.validateToken(token, userPrincipal)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userPrincipal,
                                null,
                                userPrincipal.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
