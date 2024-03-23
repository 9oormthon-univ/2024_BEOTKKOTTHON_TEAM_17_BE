package org.goormuniv.ponnect.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.dto.ErrMsgDto;

import org.goormuniv.ponnect.dto.RegisterDto;
import org.goormuniv.ponnect.dto.UserInfoDto;
import org.goormuniv.ponnect.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "인증", description = "인증 관련 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary="회원가입", description = "회원가입")
    @PostMapping("/auth/sign-up")
    public ResponseEntity<?> register (Principal principal,
                                       @RequestBody RegisterDto registerDto,
                                       HttpServletRequest httpServletRequest,
                                       HttpServletResponse httpServletResponse
    ) {
            return memberService.register(principal, registerDto, httpServletRequest, httpServletResponse);
    }

    @Operation(summary="JWT 유효성 확인 및 기본 유저 정보 반환", description = "JWT 유효성 확인 및 기본 유저 정보 반환")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/auth/validation-jwt")
    public ResponseEntity<?> validateJwt(HttpServletRequest httpServletRequest) throws ServletException, IOException {
        return memberService.validateJwt(httpServletRequest);
    }


    @Operation(summary="삭제된 API", description = "삭제된 API")
    @Deprecated
    @GetMapping("/auth/check-info")
    public ResponseEntity<?> validUserInfo(@RequestBody UserInfoDto userInfoDto){
        return memberService.validUserInfo(userInfoDto);
    }


    @Operation(summary="비밀번호 재발급", description = "비밀번호 재발급")
    @PostMapping("/auth/reissuance-pw")
    public ResponseEntity<?> reissue(@RequestBody UserInfoDto userInfoDto){
        return memberService.reissue(userInfoDto);
    }



}
