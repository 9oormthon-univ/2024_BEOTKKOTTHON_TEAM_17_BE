package org.goormuniv.ponnect.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.goormuniv.ponnect.dto.CardToCategoryDto;
import org.goormuniv.ponnect.dto.CategoryCreateDto;
import org.goormuniv.ponnect.dto.CategoryRenameDto;
import org.goormuniv.ponnect.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary="카테고리 추가", description = "카테고리 추가")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/card/category") //카테고리 생성
    public ResponseEntity<?> createCategory(Principal principal, @RequestBody CategoryCreateDto categoryCreateDto){
        return categoryService.createCategory(principal, categoryCreateDto);
    }

    @Operation(summary="카테고리 목록 보기", description = "카테고리 목록 보기")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/category") //카테고리 내역 조회
    public ResponseEntity<?> getCategory(Principal principal){
        return categoryService.getCategory(principal);
    }

    @Operation(summary="카테고리 지정", description = "카테고리 지정")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/card/category/friend") //카테고리에 등록
    public ResponseEntity<?> postCategoryToCard(Principal principal, @RequestBody CardToCategoryDto cardToCategoryDto){
        return categoryService.postCategoryToCard(principal, cardToCategoryDto);
    }

    @Operation(summary="카테고리 내에서의 명함 내역 및 검색", description = "카테고리 내에서의 명함 내역 및 검색")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/category/{categoryId}")
    public ResponseEntity<?> getAllCards(Principal principal, @PathVariable(name = "categoryId") Long categoryId, @RequestParam(required = false, defaultValue = "", value = "search") String search ) {
        return categoryService.getAllCardOfCategory(categoryId, principal, search);
    }

    @Operation(summary="해당 카테고리에 없는 내역들 내역 및 검색", description = "해당 카테고리에 없는 내역들 내역 및 검색")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/category/not-belong/{categoryId}")
    public ResponseEntity<?> getNotBelongCards(Principal principal, @PathVariable(name = "categoryId") Long categoryId, @RequestParam(required = false, defaultValue = "", value = "search") String search ) {
        return categoryService.getNotBelongCards(categoryId, principal, search);
    }



    @Operation(summary="카테고리 수정", description = "카테고리 수정")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/card/category/{categoryId}")
    public ResponseEntity<CategoryRenameDto> renameCategoryName(Principal principal, @PathVariable Long categoryId, @RequestBody CategoryRenameDto categoryRenameDto){
        return categoryService.renameCategoryName(principal, categoryId, categoryRenameDto);
    }

    @Operation(summary="카테고리 삭제", description = "카테고리 삭제")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/card/category/{categoryId}")
    public ResponseEntity<?> removeCategory(Principal principal, @PathVariable Long categoryId){
        return categoryService.removeCategory(principal, categoryId);
    }

    @Operation(summary="카테고리 내에 존재하는 명함 삭제하기", description = "카테고리 내에 존재하는 명함 삭제하기")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/card/category/{categoryId}/remove/{cardId}")
    public ResponseEntity<?> removeCardOfCategory(Principal principal, @PathVariable Long categoryId, @PathVariable Long cardId){
        return categoryService.removeCardOfCategory(principal, categoryId, cardId);
    }


}
