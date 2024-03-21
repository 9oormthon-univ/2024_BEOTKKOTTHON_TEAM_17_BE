package org.goormuniv.ponnect.auth;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider implements LogoutHandler {

    private final RedisService redisService;


    @Value("${jwt.secret}")
    private String secret_key;

    private final String HEADER_KEY = "Authorization";

    private static final String BEARER = "Bearer ";

    private final long ACCCESS_TOKEN_VALIDITY_TIME =  604800 * 1000L; //엑세스 토큰 유효기간 1주


    @PostConstruct
    protected void init() {
        log.info("secret_key Base64 인코딩시작");
        log.info("Original Secret_Key : " + secret_key);
        this.secret_key = Base64.getEncoder().encodeToString(secret_key.getBytes(StandardCharsets.UTF_8));
        log.info("Encoded Base64 Secret_Key : " + secret_key);
        log.info("secretKey 초기화 완료");
    }


    public String extractUserEmail(String jwt) {
        return Jwts.parser().setSigningKey(secret_key).parseClaimsJws(jwt).getBody().getSubject();
    }
    public Optional<String> extractAccessToken(HttpServletRequest request) throws IOException, ServletException {
        return Optional.ofNullable(request.getHeader(HEADER_KEY)).filter(accessToken -> accessToken.startsWith(BEARER)).map(accessToken -> accessToken.replace(BEARER, ""));
    }


    public Long getExpireTime(String token) {
        Date expirationDate =  Jwts.parser().setSigningKey(secret_key).parseClaimsJws(token).getBody().getExpiration();
        long now = new Date().getTime();
        return ((expirationDate.getTime() - now) % 1000) + 1;
    }


    public String generateToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCCESS_TOKEN_VALIDITY_TIME))
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact(); //토큰생성
    }

    public Authentication getAuthentication(UserDetails userDetails) {
        log.info("토큰 인증 정보 조회 시작");
        log.info("UserDetails UserName : {}",
                userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());
    }


    public boolean validateToken(String token) { //토큰 발리데이션
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret_key).parseClaimsJws(token);
            log.info("토큰 유효 체크 완료");
            return !claims.getBody().getExpiration().before(new Date());
        } catch (SignatureException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        } catch (Exception e) {
            log.info("JWT 토큰이 쪼매 이상합니다.");
        }
        return false;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 현재 인증된 사용자의 JWT 토큰 가져오기
        String token = null;
        try {
            token = extractAccessToken(request).orElseThrow();
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
        String userEmail = extractUserEmail(token);
        Long expiration = getExpireTime(token);
        redisService.setDataWithExpiration(token, "BLACKLIST_ACCESSTOKEN_" + userEmail, expiration);
        // 토큰 블랙리스트에 추가
    }
}
