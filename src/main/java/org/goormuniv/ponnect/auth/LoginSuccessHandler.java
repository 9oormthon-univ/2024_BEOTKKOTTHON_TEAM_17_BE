package org.goormuniv.ponnect.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.goormuniv.ponnect.domain.Member;
import org.goormuniv.ponnect.dto.AuthenticationDto;
import org.goormuniv.ponnect.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = extractUsername(authentication);
        String jwtToken = jwtProvider.generateToken(email);


        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Authorization", jwtToken);
        Member member = memberRepository.findByEmail(email).orElse(Member.builder().build());
        ObjectMapper objectMapper = new ObjectMapper();
        AuthenticationDto authenticationDto = AuthenticationDto.builder()
                .userId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .profileImgURL(member.getProfileImgUrl())
                .phone(member.getPhone())
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(authenticationDto));
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
