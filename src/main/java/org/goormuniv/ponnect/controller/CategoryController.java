package org.goormuniv.ponnect.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goormuniv.ponnect.dto.CardToCategoryDto;
import org.goormuniv.ponnect.dto.CategoryCreateDto;
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/card/category") //카테고리 생성
    public ResponseEntity<?> createCategory(Principal principal, @RequestBody CategoryCreateDto categoryCreateDto){
        return categoryService.createCategory(principal, categoryCreateDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/category") //카테고리 내역 조회
    public ResponseEntity<?> getCategory(Principal principal){
        return categoryService.getCategory(principal);
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/card/category/friend")
    public ResponseEntity<?> postCategoryToCard(Principal principal, @RequestBody CardToCategoryDto cardToCategoryDto){
        return categoryService.postCategoryToCard(principal, cardToCategoryDto);
    }



    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/category/{categoryId}")
    public ResponseEntity<?> getAllCards(Principal principal, @PathVariable(name = "categoryId") Long categoryId, @RequestParam(required = false, defaultValue = "", value = "search") String search ) {
        return categoryService.getAllCardOfCategory(categoryId, principal, search);
    }


}