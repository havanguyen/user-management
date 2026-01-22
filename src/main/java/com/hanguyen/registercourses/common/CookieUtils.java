package com.hanguyen.registercourses.common;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
@Component
public class CookieUtils {
    public void setAccessCookie(HttpServletResponse response, String accessToken) {
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(15 * 60)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }
    public void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null)
            return;
        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
    public void clearCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
    public void setCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        setAccessCookie(response, accessToken);
        setRefreshCookie(response, refreshToken);
    }
}