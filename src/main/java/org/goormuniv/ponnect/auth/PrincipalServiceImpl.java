package org.goormuniv.ponnect.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.domain.Member;
import org.goormuniv.ponnect.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        Member member = Member.builder().build();
        try {
            member = memberRepository.findByEmail(username).orElseThrow();

        } catch (Exception exception) {
            log.info("사용자를 찾을 수 없음");
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return PrincipalDetails.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .role(member.getRole())
                .phone(member.getPhone())
                .profileUrl(member.getProfileImgUrl())
                .build();
    }
}
