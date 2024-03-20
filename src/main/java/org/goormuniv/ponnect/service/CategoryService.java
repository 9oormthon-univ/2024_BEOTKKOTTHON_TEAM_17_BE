package org.goormuniv.ponnect.service;


import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.domain.*;
import org.goormuniv.ponnect.dto.*;
import org.goormuniv.ponnect.repository.CardCategoryRepository;
import org.goormuniv.ponnect.repository.CardRepository;
import org.goormuniv.ponnect.repository.CategoryRepository;
import org.goormuniv.ponnect.repository.MemberRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CardRepository cardRepository;

    private final CategoryRepository categoryRepository;
    private final CardCategoryRepository cardCategoryRepository;
    private final MemberRepository memberRepository;


    //카테고리 생성
    @Transactional
    public ResponseEntity<?> createCategory(Principal principal, CategoryCreateDto categoryCreateDto) {
        Map<String, Object> response = new HashMap<>();
        if (categoryCreateDto.getCategoryName() == null) {
            return ResponseEntity.badRequest().body(ErrMsgDto.builder().message("카테고리가 없습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build());
        }
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();
            Category category = Category.builder().categoryName(categoryCreateDto.getCategoryName()).build();
            category.setMember(member);
            categoryRepository.save(category);


            response.put("categoryId", category.getId());

        } catch (Exception e) {
            log.info("회원이 없음");
            return new ResponseEntity<>(ErrMsgDto.builder()
                    .message("회원이 존재하지 않습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.BAD_REQUEST);

        }
        return ResponseEntity.ok().body(response);
    }

    //GET /api/card/category 목록 보기
    public ResponseEntity<?> getCategory(Principal principal) {
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();
            List<CategoryDto> categoryList = categoryRepository.findAllByMemberId(member.getId()).stream().map(category -> {
                return CategoryDto.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getCategoryName())
                        .build();
            }).toList();
            return ResponseEntity.ok(categoryList);
        } catch (Exception e) {
            log.info("회원이 없음");
            return new ResponseEntity<>(ErrMsgDto.builder()
                    .message("회원이 존재하지 않습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.BAD_REQUEST);
        }
    }


    @Transactional
    public ResponseEntity<?> postCategoryToCard(Principal principal, CardToCategoryDto cardToCategoryDto) {
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();
            Category category = categoryRepository.findCategoryByIdAndMemberId(cardToCategoryDto.getCategoryId(), member.getId()).orElseThrow();
            List<CardIdDto> cardIdDtoList = cardToCategoryDto.getCardIdList();
            if (cardIdDtoList.isEmpty()) {
                return ResponseEntity.badRequest().body(ErrMsgDto.builder().message("추가할 항목이 없습니다.").statusCode(HttpStatus.NO_CONTENT.value()).build());
            }
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
            return new ResponseEntity<>(ErrMsgDto.builder()
                    .message("추가에 실패하였습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getAllCardOfCategory(Long categoryId, Principal principal, String keyword) {
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();

            log.info(member.getEmail());
            Specification<CardCategory> specification = search(keyword, categoryId); //여기서 에러가 나는것 같다.

            List<CardDto> cardDtos = cardCategoryRepository.findAll(specification).stream()
                    .map(cardCategory -> {
                        Member memberEntity = cardCategory.getMember();
                        return CardDto.builder()
                                .userId(memberEntity.getId())
                                .name(memberEntity.getName())
                                .phone(memberEntity.getPhone())
                                .email(memberEntity.getEmail())
                                .qrUrl(memberEntity.getQrUrl())
                                .instagram(memberEntity.getCard().getInstagram())
                                .status(memberEntity.getCard().getStatus())
                                .organization(memberEntity.getCard().getOrganization())
                                .link(memberEntity.getCard().getLink())
                                .content(memberEntity.getCard().getInstagram())
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
                                .build();
                    })
                    .toList();

            return ResponseEntity.ok(cardDtos);
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(ErrMsgDto.builder()
                    .message("회원이 존재하지 않습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    private Specification<CardCategory> search(String kw, Long categoryId) {
        return (Root<CardCategory> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            Predicate followIdPredicate = criteriaBuilder.equal(root.get("category").get("id"), categoryId);
            Join<CardCategory, Member> CardCategoryJoin = root.join("member", JoinType.INNER);
            Join<Member, Card> cardJoin = CardCategoryJoin.join("card", JoinType.INNER);
            Predicate cardPredicate = criteriaBuilder.equal(cardJoin.get("id"), CardCategoryJoin.get("id"));


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

            return criteriaBuilder.and(followIdPredicate, cardPredicate, searchPredicate);
        };
    }

    public ResponseEntity<?> getNotBelongCards(Long categoryId, Principal principal, String search) {
        //try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();


            Specification<Member> specification = notBelongSearch(search, categoryId, member.getId()); //여기서 에러가 나는것 같다.
            List<CardDto> list = memberRepository.findAll(specification).stream()
                .map(memberEntity -> {
                    return CardDto.builder()
                            .userId(memberEntity.getId())
                            .name(memberEntity.getName())
                            .phone(memberEntity.getPhone())
                            .email(memberEntity.getEmail())
                            .qrUrl(memberEntity.getQrUrl())
                            .instagram(memberEntity.getCard().getInstagram())
                            .organization(memberEntity.getCard().getOrganization())
                            .status(memberEntity.getCard().getStatus())
                            .link(memberEntity.getCard().getLink())
                            .content(memberEntity.getCard().getInstagram())
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
                            .build();
                })
                .toList();

        return ResponseEntity.ok(list);
    }

    private Specification<Member> notBelongSearch(String kw, Long categoryId, Long memberId) {
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
    public ResponseEntity<?> renameCategoryName(Principal principal, Long categoryId, CategoryRenameDto categoryRenameDto) {
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();
            Category category = categoryRepository.findCategoryByIdAndMemberId(categoryId, member.getId()).orElseThrow();
            category.setCategoryName(categoryRenameDto.getCategoryName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(ErrMsgDto.builder()
                    .message("카테고리 이름을 수정할 수 없습니다.")
                    .statusCode(HttpStatus.BAD_REQUEST.value()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<?> removeCategory(Principal principal, Long categoryId) {
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();
            categoryRepository.findCategoryByIdAndMemberId(categoryId, member.getId()).orElseThrow();
            List<CardCategory> cardCategories = cardCategoryRepository.findByCategoryId(categoryId);
            cardCategoryRepository.deleteAll(cardCategories);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(ErrMsgDto.builder()
                    .message("카테고리를 삭제할 수 없습니다.")
                    .statusCode(HttpStatus.BAD_REQUEST.value()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}


