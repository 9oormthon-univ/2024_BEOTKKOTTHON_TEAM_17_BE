package org.goormuniv.ponnect.service;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.SimpleMessage;
import org.goormuniv.ponnect.auth.JwtProvider;
import org.goormuniv.ponnect.auth.PrincipalDetails;
import org.goormuniv.ponnect.config.SecurityConfig;
import org.goormuniv.ponnect.domain.Card;
import org.goormuniv.ponnect.domain.Member;
import org.goormuniv.ponnect.dto.*;
import org.goormuniv.ponnect.exception.auth.*;
import org.goormuniv.ponnect.repository.CardRepository;
import org.goormuniv.ponnect.repository.MemberRepository;
import org.goormuniv.ponnect.util.AmazonStorageUtil;
import org.goormuniv.ponnect.util.ObjectToDtoUtil;
import org.goormuniv.ponnect.util.QrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.*;


@Service
@AllArgsConstructor
@Slf4j
public class MemberService {

    private final JavaMailSender javaMailSender;

    private final PasswordEncoder passwordEncoder;
    private final CardRepository cardRepository;

    private final MemberRepository memberRepository;

    private final AmazonStorageUtil amazonStorageUtil;

    private final QrCodeUtil qrCodeUtil;
    private final JwtProvider jwtProvider;


    //토큰 유효성 검사
    public ResponseEntity<?> validateJwt(HttpServletRequest httpServletRequest) throws ServletException, IOException {
        String accessToken = jwtProvider.extractAccessToken(httpServletRequest).orElseThrow(InvalidAccessTokenException::new);
        jwtProvider.validateToken(accessToken);
        String email = jwtProvider.extractUserEmail(accessToken);
        memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> register(Principal principal, RegisterDto registerDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (principal != null) throw new NoPermissionException();
        try {
            Member member = Member.builder()
                    .email(registerDto.getEmail())
                    .name(registerDto.getName())
                    .password(passwordEncoder.encode(registerDto.getPassword()))
                    .role("ROLE_USER")
                    .phone(registerDto.getPhone())
                    .build();
            memberRepository.save(member); //데이터베이스 저장
            Card card = Card.builder().build(); //초기 사용자의 Card를 생성
            cardRepository.save(card);

            member.setCard(card);

            ByteArrayOutputStream qrByte = qrCodeUtil.generateQr(member.getId());
            String qrUrl = amazonStorageUtil.uploadProfileQR(member.getId(), qrByte);
            member.setQrUrl(qrUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            String jwtToken = jwtProvider.generateToken(member.getEmail());
            httpHeaders.add("Authorization", jwtToken);

            AuthenticationDto authenticationDto = AuthenticationDto.builder()
                    .userId(member.getId())
                    .phone(member.getPhone())
                    .email(member.getEmail())
                    .name(member.getName())
                    .build();
            return new ResponseEntity<>(authenticationDto, httpHeaders, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Register Error ::{}", e.getMessage());
            throw new SignUpFailedException();
        }
    }

    @Deprecated
    public ResponseEntity<?> validUserInfo(UserInfoDto userInfoDto) {
        Boolean hasUser = memberRepository.findByEmailAndPhone(userInfoDto.getEmail(), userInfoDto.getPhone()).isPresent();
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasUser", hasUser);
        return ResponseEntity.ok(response);
    }


    @Transactional
    public ResponseEntity<?> reissue(UserInfoDto userInfoDto) {
        String newPw = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Member member = memberRepository.findByEmailAndPhone(userInfoDto.getEmail(), userInfoDto.getPhone()).orElseThrow(NotFoundMemberException::new);

        member.setPassword(passwordEncoder.encode(newPw));
        try {
            SMTPMsgDto smtpMsgDto = SMTPMsgDto.builder()
                    .address(member.getEmail())
                    .title(member.getName() + "님의 PONNECT 임시비밀번호 안내 이메일 입니다.")
                    .message("안녕하세요. PONNECT 임시 비밀번호 안내 관련 이메일 입니다. " + "[" + member.getName() + "]" + "님의 임시 비밀번호는 "
                            + newPw + " 입니다.").build();
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(smtpMsgDto.getAddress());
            simpleMailMessage.setSubject(smtpMsgDto.getTitle());
            simpleMailMessage.setText(smtpMsgDto.getMessage());
            javaMailSender.send(simpleMailMessage);
        } catch (Exception exception) {
            log.error("PW Reissue ::{} ", exception.getMessage());
            throw new STMPException();
        }
        return ResponseEntity.ok().build();
    }

}
