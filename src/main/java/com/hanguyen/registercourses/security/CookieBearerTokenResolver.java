package com.hanguyen.registercourses.security;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;
@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {
    private static final String ACCESS_TOKEN_COOKIE_NAME = "ACCESS_TOKEN";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    @Override
    public String resolve(HttpServletRequest request) {
        String token = resolveFromCookie(request);
        if (token != null) {
            return token;
        }
        return resolveFromHeader(request);
    }
    private String resolveFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
    private String resolveFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization != null && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
