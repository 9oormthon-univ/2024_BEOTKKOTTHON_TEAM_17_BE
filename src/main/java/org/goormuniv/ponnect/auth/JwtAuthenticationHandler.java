package org.goormuniv.ponnect.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.exception.ErrCode;
import org.goormuniv.ponnect.exception.ErrResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Deprecated
@Slf4j
@Component
public class JwtAuthenticationHandler extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            doFilter(request, response, filterChain);
        }catch (Exception exception){
            log.info("JwtAuthenticationHandler ::{}", exception.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ErrResponse errResponse = ErrResponse.builder()
                    .code(ErrCode.INVALID_ACCESS_TOKEN.getCode())
                    .message(ErrCode.INVALID_ACCESS_TOKEN.getMessage())
                    .status(ErrCode.INVALID_ACCESS_TOKEN.getStatus())
                    .build();

            response.getWriter().write(objectMapper.writeValueAsString(errResponse));
        }
    }

}
