package com.cfkiatong.springbootbankingapp.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String key = "test-key";

}