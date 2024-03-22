package org.goormuniv.ponnect.auth;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.service.RedisService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final PrincipalServiceImpl principalDetailsServiceImp;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/api/auth/sign-in") || request.getRequestURI().startsWith("/console") || request.getRequestURI().equals("/api/auth/reissurance")) {
            filterChain.doFilter(request, response); // "/api/auth/sign-in" 요청이 들어오면, 다음 필터 호출
            return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
        }



        // 예외 처리 : 들어온 토큰 값이 올바르지 않은 경우 다른 체인으로 넘어감.
        final Optional<String> accessToken = jwtProvider.extractAccessToken(request);

        if (accessToken.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null
                && jwtProvider.validateToken(accessToken.get()) && !redisService.hasKey(accessToken.get())) {
            String email = jwtProvider.extractUserEmail(accessToken.get());
                UserDetails userDetails = principalDetailsServiceImp.loadUserByUsername(email);
                Authentication authentication = jwtProvider.getAuthentication(userDetails); //Authentication 객체 생성

                //SecurityContext에 Authentication를 담는다.
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);


        }

        filterChain.doFilter(request, response);
    }
}
