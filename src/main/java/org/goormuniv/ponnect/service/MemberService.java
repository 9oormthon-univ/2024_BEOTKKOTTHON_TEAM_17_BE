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
import org.goormuniv.ponnect.domain.Member;
import org.goormuniv.ponnect.dto.*;
import org.goormuniv.ponnect.repository.MemberRepository;
import org.goormuniv.ponnect.util.AmazonStorageUtil;
import org.goormuniv.ponnect.util.ObjectToDtoUtil;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;


@Service
@AllArgsConstructor
@Slf4j
public class MemberService {

    private final JavaMailSender javaMailSender;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private final AmazonStorageUtil amazonStorageUtil;
    private final JwtProvider jwtProvider;


    public ResponseEntity<?> validateJwt(HttpServletRequest httpServletRequest) throws ServletException, IOException {
        Optional<String> accessToken = jwtProvider.extractAccessToken(httpServletRequest);
        if (accessToken.isPresent()) {
            jwtProvider.validateToken(accessToken.get());
            String email = jwtProvider.extractUserEmail(accessToken.get());
            try {
                Member member = memberRepository.findByEmail(email).orElseThrow(RuntimeException::new);

                return new ResponseEntity<>(AuthenticationDto.builder()
                        .userId(member.getId())
                        .email(member.getEmail())
                        .phone(member.getPhone())
                        .name(member.getName())
                        .profileImgURL(member.getProfileImgUrl())
                        .build(), HttpStatus.OK);
            } catch (Exception exception) {
                log.info(exception.getMessage());
                return new ResponseEntity<>(ErrMsgDto.builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("유저 정보가 없습니다."), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(ErrMsgDto.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("토큰이 유효하지 않습니다.").build()
                    , HttpStatus.BAD_REQUEST);

        }

    }

    @Transactional
    public ResponseEntity<?> register(Principal principal, String signUp, MultipartFile profileImg, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (principal != null) { //SecurityContext에 없다면 예외처리
            return new ResponseEntity<>(ErrMsgDto.builder().message("이미 회원가입이 완료되었습니다.")
                    .statusCode(HttpStatus.BAD_REQUEST.value()), HttpStatus.NOT_ACCEPTABLE);
        }
        try {
            RegisterDto registerRequest = (RegisterDto) new ObjectToDtoUtil().jsonStrToObj(signUp, RegisterDto.class);
            String profileImgURL = null;

            if (profileImg != null) {
                profileImgURL = amazonStorageUtil.uploadProfileImg(registerRequest.getEmail(), profileImg);
            }

            Member member = Member.builder()
                    .email(registerRequest.getEmail())
                    .name(registerRequest.getName())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role("ROLE_USER")
                    .phone(registerRequest.getPhone())
                    .profileImgUrl(profileImgURL)
                    .build();


            memberRepository.save(member); //데이터베이스 저장

            HttpHeaders httpHeaders = new HttpHeaders();
            String jwtToken = jwtProvider.generateToken(member.getEmail());
            httpHeaders.add("Authorization", jwtToken);

            AuthenticationDto authenticationDto = AuthenticationDto.builder()
                    .userId(member.getId())
                    .phone(member.getPhone())
                    .email(member.getEmail())
                    .profileImgURL(member.getProfileImgUrl())
                    .name(member.getName())
                    .build();
            return new ResponseEntity<>(authenticationDto, httpHeaders, HttpStatus.CREATED);

        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseEntity<>(ErrMsgDto.builder().message("회원가입이 실패했습니다.")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> validUserInfo(UserInfoDto userInfoDto) {
        Boolean hasUser = memberRepository.existsMembersByEmailAndPhone(userInfoDto.getEmail(), userInfoDto.getPhone());
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasUser", hasUser);
        return ResponseEntity.ok(response);
    }


    @Transactional
    public ResponseEntity<?> reissue(PrincipalDetails principalDetails) {
        String newPw = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Member member;
        try {
            member = memberRepository.findByEmail(principalDetails.getUsername()).orElseThrow(RuntimeException::new);
            member.setPassword(passwordEncoder.encode(newPw));
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return new ResponseEntity<>(ErrMsgDto.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("유저 정보가 없습니다."), HttpStatus.BAD_REQUEST);
        }
        try {
            SMTPMsgDto smtpMsgDto = SMTPMsgDto.builder()
                    .address(principalDetails.getEmail())
                    .title(principalDetails.getName() + "님의 PONNECT 임시비밀번호 안내 이메일 입니다.")
                    .message("안녕하세요. PONNECT 임시비밀번호 안내 관련 이메일 입니다." + "[" + principalDetails.getName() + "]" + "님의 임시 비밀번호는 "
                            + newPw + " 입니다.").build();
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(smtpMsgDto.getAddress());
            simpleMailMessage.setSubject(smtpMsgDto.getTitle());
            simpleMailMessage.setText(smtpMsgDto.getMessage());
            javaMailSender.send(simpleMailMessage);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            return new ResponseEntity<>(ErrMsgDto.builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("메일 발송이 실패했습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok().build();
    }

}
