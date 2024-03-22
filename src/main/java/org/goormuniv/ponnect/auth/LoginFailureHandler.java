package org.goormuniv.ponnect.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.exception.ErrCode;
import org.goormuniv.ponnect.exception.ErrResponse;
import org.json.JSONException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ErrResponse errResponse = ErrResponse.builder()
                .code(ErrCode.LOGIN_FAILED.getCode())
                .message(ErrCode.LOGIN_FAILED.getMessage())
                .status(ErrCode.LOGIN_FAILED.getStatus())
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(errResponse));
    }
}
