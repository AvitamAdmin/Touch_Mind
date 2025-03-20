package com.touchMind.web.controllers.token;

import com.touchMind.core.dto.JwtRequest;
import com.touchMind.core.dto.JwtResponse;
import com.touchMind.utils.JWTUtility;
import com.touchMind.web.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenGenerationController extends BaseController {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JWTUtility jwtUtility;

    @PostMapping("/api/authenticate")
    @ResponseBody
    public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) {
        try {
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
            final String token = jwtUtility.generateToken(userDetails);
            return new JwtResponse(token);
        } catch (Exception e) {
            return new JwtResponse("Error");
        }
    }
}
