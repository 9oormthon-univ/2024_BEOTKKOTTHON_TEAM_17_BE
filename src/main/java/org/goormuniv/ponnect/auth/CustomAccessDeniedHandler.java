package org.goormuniv.ponnect.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    //권한이 없는 유저가 접근 할 시 처리하는 핸들러

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        JSONObject responseJson = new JSONObject();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        try {
            responseJson.put("message", "엑세스 권한이 없습니다.");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        response.getWriter().write(objectMapper.writeValueAsString(responseJson));
    }

}