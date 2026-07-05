package com.telecom.scm.security.service;

import com.telecom.scm.security.model.AuthenticatedUser;

import io.jsonwebtoken.Claims;

public interface TokenService {

    String generateToken(AuthenticatedUser user);

    Claims parseClaims(String token);

    String getUsername(String token);

    String getIdentityType(String token);

    String getRole(String token);

    boolean isTokenValid(String token);
}
