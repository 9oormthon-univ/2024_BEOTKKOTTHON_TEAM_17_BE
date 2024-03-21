package org.goormuniv.ponnect.service;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.domain.*;
import org.goormuniv.ponnect.dto.*;
import org.goormuniv.ponnect.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.security.Principal;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final MediaRepository mediaRepository;
    private final CardCategoryRepository cardCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ResponseEntity<?> createCard(CardCreateDto cardCreateDto, Principal principal) {

        Map<String, Object> response = new HashMap<>();

        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();
            Card card = member.getCard();
            card = Card.builder()
                    .id(card.getId())
                    .organization(cardCreateDto.getOrganization())
                    .link(cardCreateDto.getLink())
                    .status(cardCreateDto.getStatus())
                    .content(cardCreateDto.getContent())
                    .instagram(cardCreateDto.getInstagram())
                    .youtube(cardCreateDto.getYoutube())
                    .facebook(cardCreateDto.getFacebook())
                    .x(cardCreateDto.getX())
                    .tiktok(cardCreateDto.getTiktok())
                    .naver(cardCreateDto.getNaver())
                    .linkedIn(cardCreateDto.getLinkedIn())
                    .behance(cardCreateDto.getNotefolio())
                    .github(cardCreateDto.getGithub())
                    .kakao(cardCreateDto.getKakao())
                    .bgColor(card.getBgColor())
                    .textColor(card.getTextColor())
                    .member(member)
                    .build();

            cardRepository.save(card);

            if (cardCreateDto.getName() != null)
                member.setName(cardCreateDto.getName());
            if (cardCreateDto.getPhone() != null)
                member.setPhone(cardCreateDto.getPhone());
            if (cardCreateDto.getEmail() != null)
                member.setEmail(cardCreateDto.getEmail());

            memberRepository.save(member);

            CardDto cardDto = CardDto.builder()
                    .cardId(member.getCard().getId())
                    .userId(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .phone(member.getPhone())
                    .qrUrl(member.getQrUrl())
                    .status(member.getCard().getStatus())
                    .organization(member.getCard().getOrganization())
                    .link(member.getCard().getLink())
                    .instagram(member.getCard().getInstagram())
                    .content(member.getCard().getContent())
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
                    .stickerDtoList(mediaRepository.findAllByCardId(member.getCard().getId()).stream()
                            .map(media -> StickerDto.builder()
                                    .type(media.getType())
                                    .posX(media.getPosX())
                                    .posY(media.getPosY())
                                    .zindex(media.getZIndex())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
        response.put("userInfo", cardDto);
        } catch (Exception e){
            log.error(e.getMessage());
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
                    .status(member.getCard().getStatus())
                    .organization(member.getCard().getOrganization())
                    .link(member.getCard().getLink())
                    .instagram(member.getCard().getInstagram())
                    .content(member.getCard().getContent())
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
                    .stickerDtoList(mediaRepository.findAllByCardId(member.getCard().getId()).stream()
                            .map(media -> StickerDto.builder()
                                    .type(media.getType())
                                    .posX(media.getPosX())
                                    .posY(media.getPosY())
                                    .zindex(media.getZIndex())
                                    .build())
                            .collect(Collectors.toList()))
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
            if(following.getId().equals(followed.getId())){ //자기자신을 팔로우 한다고 한다면 400 반환
                return ResponseEntity.badRequest().body(ErrMsgDto.builder()
                        .message("자신의 명함을 저장할 수 업습니다.")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
            }

            if (followRepository.existsByFollowingIdAndFollowedId(following.getId(), followed.getId()))
                throw new NullPointerException("follow가 이미 존재합니다.");


            Follow follow = Follow.builder()
                    .following(following)
                    .followed(followed)
                    .build();

            followRepository.save(follow);

            response.put("followId", follow.getId());
            return ResponseEntity.ok(response);
        } catch (NullPointerException e) {
            log.info("follow가 이미 존재합니다.", e);
            return ResponseEntity.badRequest().body(ErrMsgDto.builder()
                    .message("오류가 발생했습니다.")
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build());
        }
        catch (NotFoundException e) {
            log.info("회원을 찾을 수 없음", e);
            return ResponseEntity.badRequest().body(ErrMsgDto.builder()
                    .message("오류가 발생했습니다.")
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

    public ResponseEntity<?> getAllCard (Principal principal, String keyword) {
        log.info("search : " + keyword);
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();

            log.info(member.getEmail());
            Specification<Follow> specification = search(keyword, member.getId()); //여기서 에러가 나는것 같다.

            List<CardDto> cardDtos = followRepository.findAll(specification).stream()
                    .map(follow -> {
                        Member followed = follow.getFollowed();
                        return CardDto.builder()
                                .userId(followed.getId())
                                .name(followed.getName())
                                .phone(followed.getPhone())
                                .email(followed.getEmail())
                                .qrUrl(followed.getQrUrl())
                                .cardId(followed.getCard().getId())
                                .organization(followed.getCard().getOrganization())
                                .instagram(followed.getCard().getInstagram())
                                .status(followed.getCard().getStatus())
                                .link(followed.getCard().getLink())
                                .content(followed.getCard().getContent())
                                .youtube(followed.getCard().getYoutube())
                                .facebook(followed.getCard().getFacebook())
                                .x(followed.getCard().getX())
                                .tiktok(followed.getCard().getTiktok())
                                .naver(followed.getCard().getNaver())
                                .linkedIn(followed.getCard().getLinkedIn())
                                .notefolio(followed.getCard().getNotefolio())
                                .behance(followed.getCard().getBehance())
                                .github(followed.getCard().getGithub())
                                .kakao(followed.getCard().getKakao())
                                .bgColor(followed.getCard().getBgColor())
                                .textColor(followed.getCard().getTextColor())
                                .stickerDtoList(mediaRepository.findAllByCardId(followed.getCard().getId()).stream()
                                        .map(media -> StickerDto.builder()
                                                .type(media.getType())
                                                .posX(media.getPosX())
                                                .posY(media.getPosY())
                                                .zindex(media.getZIndex())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build();
                    })
                    .toList();

            return ResponseEntity.ok(cardDtos);
        }catch(Exception e){
            log.info("회원이 없음");
            return new ResponseEntity<>( ErrMsgDto.builder()
                    .message("회원이 존재하지 않습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(),HttpStatus.BAD_REQUEST);
        }
    }

    private Specification<Follow> search(String kw, Long memberId) {
        return (Root<Follow> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            Predicate followIdPredicate =  criteriaBuilder.equal(root.get("following").get("id"), memberId);
            Join<Follow, Member> followedJoin = root.join("followed", JoinType.INNER);
            Join<Member, Card> cardJoin = followedJoin.join("card", JoinType.INNER);
            Predicate cardPredicate =  criteriaBuilder.equal(cardJoin.get("id"), followedJoin.get("id"));


            Predicate searchPredicate = criteriaBuilder.or(criteriaBuilder.like(followedJoin.get("name"), "%" + kw + "%"), // 제목
                    criteriaBuilder.like(followedJoin.get("email"), "%" + kw + "%"),      // 내용
                    criteriaBuilder.like(followedJoin.get("phone"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("organization"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("content"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("instagram"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("youtube"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("status"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("facebook"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("x"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("tiktok"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("naver"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("linkedIn"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("notefolio"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("behance"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("github"), "%" + kw + "%"),
                    criteriaBuilder.like(cardJoin.get("kakao"), "%" + kw + "%")
            );  //script

            return criteriaBuilder.and(followIdPredicate, cardPredicate, searchPredicate);
        };
    }

    public ResponseEntity<?> deleteCard (Long userId, Principal principal) { //삭제 메서드 수행 시, 카테고리에 존재하는 영역 모두 삭제해야함
        try {
            Member following = memberRepository.findByEmail(principal.getName()).orElseThrow();

            Follow follow = followRepository.findByFollowingIdAndFollowedId(following.getId(), userId);


            if (follow == null) throw new NullPointerException("해당 팔로우가 존재하지 않습니다.");

            List<Category> category = categoryRepository.findAllByMemberId(following.getId());
            for(Category categoryEntity : category){
                Optional<CardCategory> cardCategory = cardCategoryRepository.findCardCategoryByCategoryIdAndMemberId(categoryEntity.getId(), userId);
                cardCategory.ifPresent(cardCategoryRepository::delete);
            }

            followRepository.delete(follow); //팔로우 삭제하기 전에 명함함에 존재하는 해당 명함을 우선 삭제해준다.

            return ResponseEntity.ok().body("delete complete");
        } catch(NullPointerException e){
            log.info("해당 팔로우가 존재하지 않음", e);
            return new ResponseEntity<>( ErrMsgDto.builder()
                    .message("오류가 발생했습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(),HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> changeColor (ColorDto colorDto, Principal principal) {
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(() -> new NoSuchElementException("회원 정보를 찾을 수 없습니다."));

            Card card = cardRepository.findByMemberId(member.getId()).orElseThrow(() -> new NoSuchElementException("카드 정보를 찾을 수 없습니다."));

            // 배경색, 글자색 변경
            Card updateCard = Card.builder()
                    .id(card.getId())
                    .organization(card.getOrganization())
                    .link(card.getLink())
                    .content(card.getContent())
                    .instagram(card.getInstagram())
                    .youtube(card.getYoutube())
                    .facebook(card.getFacebook())
                    .status(card.getStatus())
                    .x(card.getX())
                    .tiktok(card.getTiktok())
                    .naver(card.getNaver())
                    .linkedIn(card.getLinkedIn())
                    .notefolio(card.getNotefolio())
                    .behance(card.getBehance())
                    .github(card.getGithub())
                    .bgColor(colorDto.getBgColor())
                    .textColor(colorDto.getTextColor())
                    .member(member)
                    .build();

            cardRepository.save(updateCard);

            // 스티커 저장
            List<Media> mediaList = mediaRepository.findAllByCardId(card.getId());
            if (mediaList != null)
                mediaRepository.deleteAll(mediaList);

            List<Media> medias = colorDto.getStickerList().stream()
                    .map(stickerDto -> Media.builder()
                            .type(stickerDto.getType())
                            .posX(stickerDto.getPosX())
                            .posY(stickerDto.getPosY())
                            .zIndex(stickerDto.getZindex())
                            .card(card)
                            .build())
                    .collect(Collectors.toList());

            mediaRepository.saveAll(medias);

            return ResponseEntity.ok().body("save complete");

        } catch(NoSuchElementException e){
            log.info("회원 정보가 없음"+ e.getMessage());
            return new ResponseEntity<>( ErrMsgDto.builder()
                    .message("오류가 발생했습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(),HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            log.info("오류 발생");
            return new ResponseEntity<>( ErrMsgDto.builder()
                    .message("오류가 발생했습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(),HttpStatus.BAD_REQUEST);
        }
    }

}
