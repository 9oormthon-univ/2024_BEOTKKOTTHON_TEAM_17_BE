package org.goormuniv.ponnect.service;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.domain.Card;
import org.goormuniv.ponnect.domain.Follow;
import org.goormuniv.ponnect.domain.Member;
import org.goormuniv.ponnect.dto.CardCreateDto;
import org.goormuniv.ponnect.dto.CardDto;
import org.goormuniv.ponnect.dto.CardListDto;
import org.goormuniv.ponnect.dto.ErrMsgDto;
import org.goormuniv.ponnect.repository.CardRepository;
import org.goormuniv.ponnect.repository.FollowRepository;
import org.goormuniv.ponnect.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Optional;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    @Transactional
    public ResponseEntity<?> createCard(CardCreateDto cardCreateDto, Principal principal) {

        Map<String, Object> response = new HashMap<>();

        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();
            Card card = member.getCard();
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
                    .member(member)
                    .build();

            cardRepository.save(card);

            response.put("cardId", card.getId());

        } catch (Exception e){
            log.info("회원이 없음");
            return new ResponseEntity<>( ErrMsgDto.builder()
                    .message("회원이 존재하지 않습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(),HttpStatus.BAD_REQUEST);

        }
        return ResponseEntity.ok().body(response);
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

    @Transactional
    public  ResponseEntity<?> saveCard(Long userId, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            Member following = memberRepository.findByEmail(principal.getName()).orElseThrow(() -> new NotFoundException("로그인한 회원을 찾을 수 없습니다."));
            Member followed = memberRepository.findById(userId).orElseThrow(() -> new NotFoundException("해당 회원을 찾을 수 없습니다."));

            Follow follow = Follow.builder()
                    .following(following)
                    .followed(followed)
                    .build();

            followRepository.save(follow);

            response.put("followId", follow.getId());
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            log.info("회원을 찾을 수 없음", e);
            return ResponseEntity.badRequest().body(ErrMsgDto.builder()
                    .message(e.getMessage())
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build());
        } catch(Exception e){
            log.error("알 수 없는 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrMsgDto.builder()
                            .message("서버 오류가 발생했습니다.")
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
}
