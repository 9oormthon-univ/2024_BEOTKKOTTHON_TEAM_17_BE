package org.goormuniv.ponnect.service;


import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.domain.*;
import org.goormuniv.ponnect.dto.*;
import org.goormuniv.ponnect.exception.auth.NoPermissionException;
import org.goormuniv.ponnect.exception.auth.NotFoundMemberException;
import org.goormuniv.ponnect.exception.card.NoExistCardException;
import org.goormuniv.ponnect.exception.card.NotContentException;
import org.goormuniv.ponnect.exception.category.*;
import org.goormuniv.ponnect.exception.server.InternalServerException;
import org.goormuniv.ponnect.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CardRepository cardRepository;

    private final CategoryRepository categoryRepository;
    private final CardCategoryRepository cardCategoryRepository;
    private final MemberRepository memberRepository;
    private final MediaRepository mediaRepository;


    //카테고리 생성
    @Transactional
    public ResponseEntity<?> createCategory(Principal principal, CategoryCreateDto categoryCreateDto) {
        Map<String, Object> response = new HashMap<>();
        if (categoryCreateDto.getCategoryName() == null) throw new NoExistCategoryException();

        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
        Category category = Category.builder().categoryName(categoryCreateDto.getCategoryName()).build();
        category.setMember(member);
        categoryRepository.save(category);


        response.put("categoryId", category.getId());

        return ResponseEntity.ok().body(response);
    }

    //GET /api/card/category 목록 보기
    public ResponseEntity<?> getCategory(Principal principal) {
        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
        List<CategoryDto> categoryList = categoryRepository.findAllByMemberId(member.getId()).stream().map(category -> {
            return CategoryDto.builder()
                    .categoryId(category.getId())
                    .categoryName(category.getCategoryName())
                    .build();
        }).toList();
        return ResponseEntity.ok(categoryList);
    }


    @Transactional
    public ResponseEntity<?> postCategoryToCard(Principal principal, CardToCategoryDto cardToCategoryDto) {
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
            Category category = categoryRepository.findCategoryByIdAndMemberId(cardToCategoryDto.getCategoryId(), member.getId()).orElseThrow(NoExistCategoryException::new);
            List<CardIdDto> cardIdDtoList = cardToCategoryDto.getCardIdList();
            if (cardIdDtoList.isEmpty()) throw new NotContentException();
            for (CardIdDto cardIdDto : cardIdDtoList) {
                if (cardRepository.findCardById(cardIdDto.getCardId()).isPresent()) {
                    CardCategory cardCategory = CardCategory.builder()
                            .category(category)
                            .member(cardRepository.findCardById(cardIdDto.getCardId()).get().getMember())
                            .build();
                    cardCategoryRepository.save(cardCategory);
                }
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.info(e.getMessage());
            throw new NoCreateCardToCategoryException();
        }
    }

    public ResponseEntity<?> getAllCardOfCategory(Long categoryId, Principal principal, String keyword) {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
            categoryRepository.findCategoryByIdAndMemberId(categoryId, member.getId()).orElseThrow(NoPermissionException::new);
            Specification<CardCategory> specification = search(keyword, categoryId);
            List<CardDto> cardDtos = cardCategoryRepository.findAll(specification).stream()
                    .map(cardCategory -> {
                        Member memberEntity = cardCategory.getMember();
                        return CardDto.builder()
                                .userId(memberEntity.getId())
                                .name(memberEntity.getName())
                                .phone(memberEntity.getPhone())
                                .email(memberEntity.getEmail())
                                .qrUrl(memberEntity.getQrUrl())
                                .cardId(memberEntity.getCard().getId())
                                .instagram(memberEntity.getCard().getInstagram())
                                .status(memberEntity.getCard().getStatus())
                                .organization(memberEntity.getCard().getOrganization())
                                .link(memberEntity.getCard().getLink())
                                .content(memberEntity.getCard().getContent())
                                .youtube(memberEntity.getCard().getYoutube())
                                .facebook(memberEntity.getCard().getFacebook())
                                .x(memberEntity.getCard().getX())
                                .tiktok(memberEntity.getCard().getTiktok())
                                .naver(memberEntity.getCard().getNaver())
                                .linkedIn(memberEntity.getCard().getLinkedIn())
                                .notefolio(memberEntity.getCard().getNotefolio())
                                .behance(memberEntity.getCard().getBehance())
                                .github(memberEntity.getCard().getGithub())
                                .kakao(memberEntity.getCard().getKakao())
                                .bgColor(memberEntity.getCard().getBgColor())
                                .textColor(memberEntity.getCard().getTextColor())
                                .stickerDtoList(mediaRepository.findAllByCardId(memberEntity.getCard().getId()).stream()
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

        private Specification<CardCategory> search (String kw, Long categoryId){
            return (Root<CardCategory> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
                criteriaQuery.distinct(true);
                Predicate followIdPredicate = criteriaBuilder.equal(root.get("category").get("id"), categoryId);
                Join<CardCategory, Member> CardCategoryJoin = root.join("member", JoinType.INNER);
                Join<Member, Card> cardJoin = CardCategoryJoin.join("card", JoinType.INNER);
//                Predicate cardPredicate = criteriaBuilder.equal(cardJoin.get("id"), CardCategoryJoin.get("id"));


                Predicate searchPredicate = criteriaBuilder.or(criteriaBuilder.like(CardCategoryJoin.get("name"), "%" + kw + "%"), // 제목
                        criteriaBuilder.like(CardCategoryJoin.get("email"), "%" + kw + "%"),      // 내용
                        criteriaBuilder.like(CardCategoryJoin.get("phone"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("content"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("organization"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("instagram"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("youtube"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("facebook"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("status"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("x"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("tiktok"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("naver"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("linkedIn"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("notefolio"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("behance"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("github"), "%" + kw + "%"),
                        criteriaBuilder.like(cardJoin.get("kakao"), "%" + kw + "%")
                );

                return criteriaBuilder.and(followIdPredicate,  searchPredicate);
            };
        }

        public ResponseEntity<?> getNotBelongCards (Long categoryId, Principal principal, String search){
            //try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);


            Specification<Member> specification = notBelongSearch(search, categoryId, member.getId()); //여기서 에러가 나는것 같다.
            List<CardDto> list = memberRepository.findAll(specification).stream()
                    .map(memberEntity -> {
                        return CardDto.builder()
                                .userId(memberEntity.getId())
                                .name(memberEntity.getName())
                                .phone(memberEntity.getPhone())
                                .email(memberEntity.getEmail())
                                .qrUrl(memberEntity.getQrUrl())
                                .cardId(memberEntity.getCard().getId())
                                .instagram(memberEntity.getCard().getInstagram())
                                .organization(memberEntity.getCard().getOrganization())
                                .status(memberEntity.getCard().getStatus())
                                .link(memberEntity.getCard().getLink())
                                .content(memberEntity.getCard().getContent())
                                .youtube(memberEntity.getCard().getYoutube())
                                .facebook(memberEntity.getCard().getFacebook())
                                .x(memberEntity.getCard().getX())
                                .tiktok(memberEntity.getCard().getTiktok())
                                .naver(memberEntity.getCard().getNaver())
                                .linkedIn(memberEntity.getCard().getLinkedIn())
                                .notefolio(memberEntity.getCard().getNotefolio())
                                .behance(memberEntity.getCard().getBehance())
                                .github(memberEntity.getCard().getGithub())
                                .kakao(memberEntity.getCard().getKakao())
                                .bgColor(memberEntity.getCard().getBgColor())
                                .textColor(memberEntity.getCard().getTextColor())
                                .stickerDtoList(mediaRepository.findAllByCardId(memberEntity.getCard().getId()).stream()
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

            return ResponseEntity.ok(list);
        }

        private Specification<Member> notBelongSearch (String kw, Long categoryId, Long memberId){
            return (Root<Member> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {


                Subquery<Long> followedSubquery = criteriaQuery.subquery(Long.class);
                Root<Follow> followedSubRoot = followedSubquery.from(Follow.class);
                followedSubquery.select(followedSubRoot.get("followed").get("id")); // Selecting followed memberId
                followedSubquery.where(criteriaBuilder.equal(followedSubRoot.get("following").get("id"), memberId)); // Filtering by memberId


                Subquery<Long> categoryMembersSubquery = criteriaQuery.subquery(Long.class);
                Root<CardCategory> categoryMembersSubRoot = categoryMembersSubquery.from(CardCategory.class);
                categoryMembersSubquery.select(categoryMembersSubRoot.get("member").get("id")); // Selecting memberId from CardCategory
                categoryMembersSubquery.where(criteriaBuilder.equal(categoryMembersSubRoot.get("category").get("id"), categoryId)); // Filtering by categoryId

                Predicate searchPredicate = criteriaBuilder.or(criteriaBuilder.like(root.get("name"), "%" + kw + "%"), // 제목
                        criteriaBuilder.like(root.get("email"), "%" + kw + "%"),      // 내용
                        criteriaBuilder.like(root.get("phone"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("content"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("organization"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("instagram"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("youtube"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("status"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("facebook"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("x"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("tiktok"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("naver"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("linkedIn"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("notefolio"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("behance"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("github"), "%" + kw + "%"),
                        criteriaBuilder.like(root.get("card").get("kakao"), "%" + kw + "%")
                );


                return criteriaBuilder.and(
                        root.get("id").in(followedSubquery),
                        criteriaBuilder.not(root.get("id").in(categoryMembersSubquery)
                        ), searchPredicate
                );
            };
        }


        @Transactional
        public ResponseEntity<CategoryRenameDto> renameCategoryName (Principal principal, Long categoryId, CategoryRenameDto
        categoryRenameDto){
        try{
                Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
                Category category = categoryRepository.findCategoryByIdAndMemberId(categoryId, member.getId()).orElseThrow(NoExistCategoryException::new);
                category.setCategoryName(categoryRenameDto.getCategoryName());
                return ResponseEntity.ok(categoryRenameDto);
            } catch (Exception e) {
                log.info(e.getMessage());
                throw new RenameCategoryException();
            }
        }

        @Transactional
        public ResponseEntity<?> removeCategory (Principal principal, Long categoryId){
            try {
                Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
                Category category = categoryRepository.findCategoryByIdAndMemberId(categoryId, member.getId()).orElseThrow(NoExistCategoryException::new);
                List<CardCategory> cardCategories = cardCategoryRepository.findByCategoryId(categoryId);
                cardCategoryRepository.deleteAll(cardCategories);
                categoryRepository.delete(category);

                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.info(e.getMessage());
                throw new RemoveCategoryException();
            }
        }


        @Transactional
        public ResponseEntity<?> removeCardOfCategory (Principal principal, Long categoryId, Long cardId){
            try {
                Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(NotFoundMemberException::new);
                categoryRepository.findCategoryByIdAndMemberId(categoryId, member.getId()).orElseThrow(NoExistCategoryException::new);
                Card card = cardRepository.findCardById(cardId).orElseThrow(NoExistCardException::new);
                CardCategory cardCategory = cardCategoryRepository.findCardCategoryByCategoryIdAndMemberId(categoryId, card.getMember().getId()).orElseThrow(NoExistCardOfCategoryException::new);

                cardCategoryRepository.delete(cardCategory);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.info(e.getMessage());
                throw new RemoveCardOfCategoryException();
            }
        }
    }


