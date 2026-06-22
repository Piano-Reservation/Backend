package com.backend_piano.auth.service;

import com.backend_piano.auth.dto.LoginRequest;
import com.backend_piano.auth.exception.AuthErrorCode;
import com.backend_piano.global.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private final SecurityContextRepository securityContextRepository;

    public Void login(LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        UsernamePasswordAuthenticationToken token =
                UsernamePasswordAuthenticationToken.unauthenticated(request.studentNumber(), request.password());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(token);
        } catch (BadCredentialsException e) {
            throw new ApiException(AuthErrorCode.INVALID_CREDENTIALS);
        } catch (DisabledException e) {
            throw new ApiException(AuthErrorCode.ACCOUNT_DISABLED);
        } catch (AuthenticationException e) {
            throw new ApiException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        sessionAuthenticationStrategy.onAuthentication(authentication, servletRequest, servletResponse);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, servletRequest, servletResponse);
        return null;
    }
}
