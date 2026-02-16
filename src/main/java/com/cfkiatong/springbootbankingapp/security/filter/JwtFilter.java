package com.cfkiatong.springbootbankingapp.security.filter;
import com.cfkiatong.springbootbankingapp.repository.AccountRepository;
import com.cfkiatong.springbootbankingapp.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
@Component
public class JwtFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    AccountRepository accountRepository;

    JwtFilter(JwtService jwtService, AccountRepository accountRepository) {
        this.jwtService = jwtService;
        this.accountRepository = accountRepository; }

    @Override
    public void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request,response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.extractAllClaims(token);

            if(claims.getExpiration().before(new Date())){
                filterChain.doFilter(request, response);
                return;
            }

            String id = claims.getSubject();

            List<String> roles = claims.get("roles", List.class);

            List<SimpleGrantedAuthority> authorities = roles
                    .stream()
                            .map(SimpleGrantedAuthority::new)
                                    .toList();

            UserDetails user = new User(id, "", authorities);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities());

            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e){
            filterChain.doFilter(request, response);
        }

        filterChain.doFilter(request, response);
    }

//    @Override protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain) throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//        String token = null;
//        String id;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            id = jwtService.extractId(token);
//        } else {
//            id = null;
//        }
//
//        if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            Account account = accountRepository.findById(UUID.fromString(id))
//                    .orElseThrow(() -> new AccountNotFoundException(UUID.fromString(id)));
//
//            UserPrincipal userPrincipal = new UserPrincipal(account);
//
//            if (jwtService.validateToken(token, userPrincipal)) {
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(
//                                userPrincipal,
//                                null,
//                                userPrincipal.getAuthorities());
//
//                authToken.setDetails(
//                        new WebAuthenticationDetailsSource()
//                                .buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }

}