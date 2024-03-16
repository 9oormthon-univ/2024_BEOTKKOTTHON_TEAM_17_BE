package org.goormuniv.ponnect.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.auth.JwtProvider;
import org.goormuniv.ponnect.domain.Card;
import org.goormuniv.ponnect.domain.Member;
import org.goormuniv.ponnect.dto.CardCreateDto;
import org.goormuniv.ponnect.dto.CardDto;
import org.goormuniv.ponnect.dto.ErrMsgDto;
import org.goormuniv.ponnect.repository.CardRepository;
import org.goormuniv.ponnect.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Optional;
import java.security.Principal;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public ResponseEntity<?> createCard(CardCreateDto cardCreateDto, HttpServletRequest httpServletRequest) throws ServletException, IOException {

        Optional<String> accessToken = jwtProvider.extractAccessToken(httpServletRequest);
        if (accessToken.isPresent()) {
            String email = jwtProvider.extractUserEmail(accessToken.get());

            Optional<Member> member = memberRepository.findByEmail(email);
            if (member.isPresent()) {
                System.out.println("card "+ cardCreateDto.getKakaoTalk());
                Card card = member.get().getCard();
                card = Card.builder()
                        .id(card.getId())
                        .organisation(cardCreateDto.getOrganization())
                        .link(cardCreateDto.getLink())
                        .content(cardCreateDto.getContent())
                        .instagram(cardCreateDto.getInstagram())
                        .youtube(cardCreateDto.getYoutube())
                        .facebook(cardCreateDto.getFacebook())
                        .x(cardCreateDto.getX())
                        .tiktok(cardCreateDto.getTictok())
                        .naver(cardCreateDto.getNaverBlog())
                        .linkedIn(cardCreateDto.getLinkedIn())
                        .behance(cardCreateDto.getNotefolio())
                        .github(cardCreateDto.getGithub())
                        .kakao(cardCreateDto.getKakaoTalk())
                        .member(member.get())
                        .build();

                cardRepository.save(card);

                Map<String, Object> response = new HashMap<>();
                response.put("cardId", card.getId());

                return ResponseEntity.ok().body(response);
            } else {
                // 회원이 존재하지 않는 경0우
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ErrMsgDto.builder()
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .message("유저 정보가 없습니다.")
                                .build());
            }
        } else {
            // 토큰이 없는 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrMsgDto.builder()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .message("토큰이 유효하지 않습니다.")
                            .build());
        }
    }

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
