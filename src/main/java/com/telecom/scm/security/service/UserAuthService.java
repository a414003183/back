package com.telecom.scm.security.service;

import java.util.Optional;

import com.telecom.scm.security.model.AuthenticatedUser;

public interface UserAuthService {

    Optional<AuthenticatedUser> findByUsername(String username);

    Optional<AuthenticatedUser> findByUsernameAndIdentity(String username, String identityType);

    AuthenticatedUser authenticate(String username, String rawPassword);
}
