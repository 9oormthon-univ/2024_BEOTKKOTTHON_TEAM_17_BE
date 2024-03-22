package org.goormuniv.ponnect.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.exception.ErrCode;
import org.goormuniv.ponnect.exception.ErrResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    //권한이 없는 유저가 접근 할 시 처리하는 핸들러

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ErrResponse errResponse = ErrResponse.builder()
                .code(ErrCode.INVALID_ACCESS_TOKEN.getCode())
                .message(ErrCode.INVALID_ACCESS_TOKEN.getMessage())
                .status(ErrCode.INTERNAL_SERVER_ERROR.getStatus())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errResponse));
    }

}