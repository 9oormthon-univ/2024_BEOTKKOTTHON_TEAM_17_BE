package org.goormuniv.ponnect.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.auth.JwtProvider;
import org.goormuniv.ponnect.domain.Card;
import org.goormuniv.ponnect.domain.Member;
import org.goormuniv.ponnect.dto.CardCreateDto;
import org.goormuniv.ponnect.dto.ErrMsgDto;
import org.goormuniv.ponnect.repository.CardRepository;
import org.goormuniv.ponnect.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public ResponseEntity<?> createCard(CardCreateDto cardCreateDto, HttpServletRequest httpServletRequest) throws ServletException, IOException {

        Optional<String> accessToken = jwtProvider.extractAccessToken(httpServletRequest);
        if (accessToken.isPresent()) {
            String email = jwtProvider.extractUserEmail(accessToken.get());

            Optional<Member> member = memberRepository.findByEmail(email);
            if (member.isPresent()) {
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

                return ResponseEntity.ok().build();
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
}
