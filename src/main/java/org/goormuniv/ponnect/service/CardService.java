package org.goormuniv.ponnect.service;

import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.domain.*;
import org.goormuniv.ponnect.dto.*;
import org.goormuniv.ponnect.exception.auth.NotFoundMemberException;
import org.goormuniv.ponnect.exception.card.NoExistCardException;
import org.goormuniv.ponnect.exception.follow.AlreadyFollowException;
import org.goormuniv.ponnect.exception.follow.NoExistFollowMemberException;
import org.goormuniv.ponnect.exception.follow.SelfFollowException;
import org.goormuniv.ponnect.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    public ResponseEntity<CardDto> createCard(CardCreateDto cardCreateDto, Principal principal) {


            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
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
                    .behance(cardCreateDto.getBehance())
                    .notefolio(cardCreateDto.getNotefolio())
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

        return ResponseEntity.ok().body(cardDto);
    }

    public ResponseEntity<CardDto> getMyCard(Principal principal) {
        CardDto cardDto;
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
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
        return ResponseEntity.ok(cardDto);
    }

    @Transactional
    public  ResponseEntity<?> saveCard(Long userId, Principal principal) {
        Map<String, Object> response = new HashMap<>();
            Member following = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
            Member followed = memberRepository.findById(userId).orElseThrow(NotFoundMemberException::new);
            if(following.getId().equals(followed.getId())) throw new SelfFollowException();
            if (followRepository.existsByFollowingIdAndFollowedId(following.getId(), followed.getId())) throw new AlreadyFollowException();


            Follow follow = Follow.builder()
                    .following(following)
                    .followed(followed)
                    .build();

            followRepository.save(follow);

            response.put("followId", follow.getId());
            return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getAllCard (Principal principal, String keyword) {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
            Specification<Follow> specification = search(keyword, member.getId()); //여기서 에러가 나는것 같다.
            List<CardDto> cardDtos = followRepository.findAll(specification).stream()
                    .map(follow -> {
                        log.info(follow.toString());
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
    }

    private Specification<Follow> search(String kw, Long memberId) {
        return (Root<Follow> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            Predicate followIdPredicate =  criteriaBuilder.equal(root.get("following").get("id"), memberId);
            Join<Follow, Member> followedJoin = root.join("followed", JoinType.INNER);
            Join<Member, Card> cardJoin = followedJoin.join("card", JoinType.INNER);
//            Predicate cardPredicate =  criteriaBuilder.equal(cardJoin.get("id"), followedJoin.get("id"));


            Predicate searchPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get("memo"), "%" + kw + "%"),
                    criteriaBuilder.like(followedJoin.get("name"), "%" + kw + "%"), // 제목
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
            );

            return criteriaBuilder.and(followIdPredicate, searchPredicate);
        };
    }

    public ResponseEntity<?> deleteCard (Long userId, Principal principal) { //삭제 메서드 수행 시, 카테고리에 존재하는 영역 모두 삭제해야함
            Member following = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);

            Follow follow = followRepository.findByFollowingIdAndFollowedId(following.getId(), userId);


            if (follow == null) throw new NoExistFollowMemberException();

            List<Category> category = categoryRepository.findAllByMemberId(following.getId());
            for(Category categoryEntity : category){
                Optional<CardCategory> cardCategory = cardCategoryRepository.findCardCategoryByCategoryIdAndMemberId(categoryEntity.getId(), userId);
                cardCategory.ifPresent(cardCategoryRepository::delete);
            }

            followRepository.delete(follow); //팔로우 삭제하기 전에 명함함에 존재하는 해당 명함을 우선 삭제해준다.

            return ResponseEntity.ok().body("delete complete");
    }

    public ResponseEntity<?> changeColor (ColorDto colorDto, Principal principal) {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);

            Card card = cardRepository.findByMemberId(member.getId()).orElseThrow(NoExistCardException::new);

            // 배경색, 글자색 변경
            Card updateCard = Card.builder()
                    .id(card.getId())
                    .organization(card.getOrganization())
                    .link(card.getLink())
                    .content(card.getContent())
                    .instagram(card.getInstagram())
                    .youtube(card.getYoutube())
                    .facebook(card.getFacebook())
                    .kakao(card.getKakao())
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
            log.info(colorDto.toString());
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

    }

    public ResponseEntity<?> changeMemo (ChangeMemoDto changeMemoDto, Principal principal) {
        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
        Follow follow = followRepository.findByFollowingIdAndFollowedId(member.getId(), changeMemoDto.getUserId());

        if (follow == null) throw new NoExistFollowMemberException();

        follow.setMemo(changeMemoDto.getMemo());

        followRepository.save(follow);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getMemo (Long userId, Principal principal) {
        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
        Follow follow = followRepository.findByFollowingIdAndFollowedId(member.getId(), userId);

        if (follow == null) throw new NoExistFollowMemberException();

        GetMemoDto getMemoDto = GetMemoDto.builder().memo(follow.getMemo()).build();
        return ResponseEntity.ok().body(getMemoDto);


    }

}
