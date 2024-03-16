package org.goormuniv.ponnect.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.goormuniv.ponnect.auth.PrincipalDetails;
import org.goormuniv.ponnect.domain.Member;
import org.goormuniv.ponnect.dto.CardDto;
import org.goormuniv.ponnect.dto.ErrMsgDto;
import org.goormuniv.ponnect.repository.CardRepository;
import org.goormuniv.ponnect.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository cardRepository;

    private final MemberRepository memberRepository;


    public ResponseEntity<?> getMyCard(Principal principal) {
        CardDto cardDto;
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();
            cardDto = CardDto.builder()
                    .cardId(member.getCard().getId())
                    .userId(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .qrUrl(member.getQrUrl())
                    .organisation(member.getCard().getOrganisation())
                    .link(member.getCard().getLink())
                    .content(member.getCard().getInstagram())
                    .youtube(member.getCard().getYoutube())
                    .facebook(member.getCard().getFacebook())
                    .x(member.getCard().getX())
                    .tiktok(member.getCard().getTiktok())
                    .naver(member.getCard().getNaver())
                    .linkedIn(member.getCard().getLinkedIn())
                    .notefolio(member.getCard().getNotefolio())
                    .behance(member.getCard().getBehance())
                    .github(member.getCard().getGithub())
                    .kakao(member.getCard().getKakao())
                    .bgColor(member.getCard().getBgColor())
                    .textColor(member.getCard().getTextColor())
                    .build();

        }catch(Exception e){
            log.info("회원이 없음");
            return new ResponseEntity<>( ErrMsgDto.builder()
                    .message("회원이 존재하지 않습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(),HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(cardDto);
    }
}
