package org.goormuniv.ponnect.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JSONLoginFilter extends AbstractAuthenticationProcessingFilter {
    private static final String CONTENT_TYPE = "application/json";
    private static final String USERNAME_KEY = "principal";
    private static final String PASSWORD_KEY = "credential";
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/api/auth/sign-in", "POST"); //


    public JSONLoginFilter() {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
            throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
        }


        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        System.out.println(messageBody.toString());
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);

        String email = usernamePasswordMap.get(USERNAME_KEY);
        String password = usernamePasswordMap.get(PASSWORD_KEY);
        System.out.println(email + password);


        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);//principal 과 credentials 전달

        return this.getAuthenticationManager().authenticate(authenticationToken);
    }
}