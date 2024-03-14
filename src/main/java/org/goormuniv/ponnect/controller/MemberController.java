package org.goormuniv.ponnect.controller;



import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.auth.PrincipalDetails;
import org.goormuniv.ponnect.dto.ErrMsgDto;

import org.goormuniv.ponnect.dto.UserInfoDto;
import org.goormuniv.ponnect.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/auth/sign-up")
    public ResponseEntity<?> register (Principal principal,
                                       @RequestPart(value = "signUp") String signUp,
                                       @RequestPart(required = false, value = "profileImg") MultipartFile profileImg,
                                       HttpServletRequest httpServletRequest,
                                       HttpServletResponse httpServletResponse
    ) {
        try{
            return memberService.register(principal, signUp, profileImg, httpServletRequest, httpServletResponse);
        }
        catch (Exception e){
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrMsgDto.builder()
                    .message("가입이 정상적으로 처리되지 않았습니다.")
                    .statusCode(HttpStatus.BAD_REQUEST.value()).build());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/auth/validation-jwt")
    public ResponseEntity<?> validateJwt(HttpServletRequest httpServletRequest) throws ServletException, IOException {
        return memberService.validateJwt(httpServletRequest);
    }


    @Deprecated
    @GetMapping("/auth/check-info")
    public ResponseEntity<?> validUserInfo(@RequestBody UserInfoDto userInfoDto){
        return memberService.validUserInfo(userInfoDto);
    }


    @PostMapping("/auth/reissuance-pw")
    public ResponseEntity<?> reissue(@RequestBody UserInfoDto userInfoDto){
        return memberService.reissue(userInfoDto);
    }



}
