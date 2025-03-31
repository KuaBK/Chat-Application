package com.example.cua_chat_app.utils;

import java.text.ParseException;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.experimental.NonFinal;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(new MACVerifier(signerKey.getBytes()))) {
                throw new IllegalArgumentException("Token signature is invalid");
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            return claims.getSubject();

        } catch (ParseException | JOSEException e) {
            throw new IllegalArgumentException("Token is invalid", e);
        }
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "UNKNOWN";
        }

        Object principal = authentication.getPrincipal();

        return switch (principal) {
            case UserDetails userDetails -> userDetails.getUsername();
            case Jwt jwt -> jwt.getClaimAsString("sub");
            default -> principal.toString();
        };
    }
}
