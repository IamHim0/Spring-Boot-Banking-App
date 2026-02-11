//package com.cfkiatong.springbootbankingapp.security;
//
//import com.cfkiatong.springbootbankingapp.dto.AuthenticationRequest;
//import com.cfkiatong.springbootbankingapp.dto.AuthenticationResponse;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class AuthenticationController {
//
//    private final AuthenticationManager authenticationManager;
//    private final UserDetailsService userDetailsService;
//
//    public AuthenticationController(
//            AuthenticationManager authenticationManager,
//            UserDetailsService userDetailsService) {
//        this.authenticationManager = authenticationManager;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        authenticationRequest.getUsername(),
//                        authenticationRequest.getPassword()));
//
//        UserDetails user = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//
//        String token = "token";
//        AuthenticationResponse authResponse = new AuthenticationResponse();
//
//        authResponse.setJwtToken(token);
//
//        return ResponseEntity.ok(authResponse);
//    }
//
//}
