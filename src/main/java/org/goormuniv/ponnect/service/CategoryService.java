package org.goormuniv.ponnect.service;


import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.domain.Card;
import org.goormuniv.ponnect.domain.CardCategory;
import org.goormuniv.ponnect.domain.Category;
import org.goormuniv.ponnect.domain.Member;
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
        if(categoryCreateDto.getCategoryName() == null){
            return ResponseEntity.badRequest().body(ErrMsgDto.builder().message("카테고리가 없습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build());
        }
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();
            Category category = Category.builder().categoryName(categoryCreateDto.getCategoryName()).build();
            category.setMember(member);
            categoryRepository.save(category);


            response.put("categoryId", category.getId());

        } catch (Exception e){
            log.info("회원이 없음");
            return new ResponseEntity<>( ErrMsgDto.builder()
                    .message("회원이 존재하지 않습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(),HttpStatus.BAD_REQUEST);

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
        } catch (Exception e){
            log.info("회원이 없음");
            return new ResponseEntity<>( ErrMsgDto.builder()
                    .message("회원이 존재하지 않습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(),HttpStatus.BAD_REQUEST);
        }
    }


    @Transactional
    public ResponseEntity<?> postCategoryToCard(Principal principal, CardToCategoryDto cardToCategoryDto) {
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();
            Category category = categoryRepository.findCategoryByIdAndMemberId(cardToCategoryDto.getCategoryId(), member.getId()).orElseThrow();
            List<CardIdDto> cardIdDtoList = cardToCategoryDto.getCardIdList();
            if(cardIdDtoList.isEmpty()){
                return ResponseEntity.badRequest().body(ErrMsgDto.builder().message("추가할 항목이 없습니다.").statusCode(HttpStatus.NO_CONTENT.value()).build());
            }
            for(CardIdDto cardIdDto : cardIdDtoList){
                if(cardRepository.findCardById(cardIdDto.getCardId()).isPresent()) {
                    CardCategory cardCategory = CardCategory.builder()
                            .category(category)
                            .card(cardRepository.findCardById(cardIdDto.getCardId()).get())
                            .build();
                    cardCategoryRepository.save(cardCategory);
                }
            }

            return ResponseEntity.ok().build();

        } catch (Exception e){
            return new ResponseEntity<>( ErrMsgDto.builder()
                    .message("추가에 실패하였습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(),HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getAllCardOfCategory(Long categoryId, Principal principal, String keyword) {
        try {
            Member member = memberRepository.findByEmail(principal.getName()).orElseThrow();

            log.info(member.getEmail());
            Specification<CardCategory> specification = search(keyword, member.getId()); //여기서 에러가 나는것 같다.

            List<CardDto> cardDtos = cardCategoryRepository.findAll(specification).stream()
                    .map(cardEntity -> {
                        Card card = cardEntity.getCard();
                        return CardDto.builder()
                                .userId(card.getMember().getId())
                                .name(card.getMember().getName())
                                .phone(card.getMember().getPhone())
                                .email(card.getMember().getEmail())
                                .qrUrl(card.getMember().getQrUrl())
                                .instagram(card.getInstagram())
                                .organization(card.getOrganization())
                                .link(card.getLink())
                                .content(card.getInstagram())
                                .youtube(card.getYoutube())
                                .facebook(card.getFacebook())
                                .x(card.getX())
                                .tiktok(card.getTiktok())
                                .naver(card.getNaver())
                                .linkedIn(card.getLinkedIn())
                                .notefolio(card.getNotefolio())
                                .behance(card.getBehance())
                                .github(card.getGithub())
                                .kakao(card.getKakao())
                                .bgColor(card.getBgColor())
                                .textColor(card.getTextColor())
                                .build();
                    })
                    .toList();

            return ResponseEntity.ok(cardDtos);
        }catch(Exception e){
            log.info(e.getMessage());
            return new ResponseEntity<>( ErrMsgDto.builder()
                    .message("회원이 존재하지 않습니다.").statusCode(HttpStatus.BAD_REQUEST.value()).build(),HttpStatus.BAD_REQUEST);
        }
    }

    private Specification<CardCategory> search(String kw, Long categoryId) {
        return (Root<CardCategory> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            Predicate followIdPredicate =  criteriaBuilder.equal(root.get("category").get("id"), categoryId);
            Join<CardCategory, Card> CardCategoryJoin = root.join("card", JoinType.INNER);
            Join< Member, Card> cardJoin = CardCategoryJoin.join("member", JoinType.INNER);
            Predicate cardPredicate =  criteriaBuilder.equal(cardJoin.get("id"), CardCategoryJoin.get("id"));


            Predicate searchPredicate = criteriaBuilder.or(criteriaBuilder.like(cardJoin.get("name"), "%" + kw + "%"), // 제목
                    criteriaBuilder.like(cardJoin.get("email"), "%" + kw + "%"),      // 내용
                    criteriaBuilder.like(cardJoin.get("phone"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("content"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("organization"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("instagram"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("youtube"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("facebook"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("x"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("tiktok"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("naver"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("linkedIn"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("notefolio"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("behance"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("github"), "%" + kw + "%"),
                    criteriaBuilder.like(CardCategoryJoin.get("kakao"), "%" + kw + "%")
            );  //script

            return criteriaBuilder.and(followIdPredicate, cardPredicate, searchPredicate);
        };
    }
}


    //GET /api/card/category/:categoryId?search={keyword}

