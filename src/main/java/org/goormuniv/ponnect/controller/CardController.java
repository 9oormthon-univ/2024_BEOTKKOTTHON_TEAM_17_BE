package org.goormuniv.ponnect.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.goormuniv.ponnect.auth.PrincipalDetails;
import org.goormuniv.ponnect.dto.CardCreateDto;
import org.goormuniv.ponnect.dto.CardDto;
import org.goormuniv.ponnect.dto.ColorDto;
import org.goormuniv.ponnect.dto.StickerDto;
import org.goormuniv.ponnect.repository.CardRepository;
import org.goormuniv.ponnect.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "명함", description = "명함 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CardController {

    private final CardService cardService;
    List<CardDto> cardList = new ArrayList<>();

    @Operation(summary="명함 제작 및 수정", description = "명함 제작 및 명함 수정 요청")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CardDto.class))
                    })
    })
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/card")
    public ResponseEntity<CardDto> createMyCard(@RequestBody CardCreateDto cardCreateDto, Principal principal) {
        return cardService.createCard(cardCreateDto, principal);
    }

    @Operation(summary="내 명함 보기", description = "내 명함 보기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CardDto.class))
                    })
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card")
    public ResponseEntity<CardDto> getMyCard(Principal principal){
        return cardService.getMyCard(principal);
    }

    @Operation(summary="친구 명함 저장", description = "친구 명함 저장")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/card/save/{userId}")
    public ResponseEntity<?> saveCard(@PathVariable(name="userId") Long userId, Principal principal) {
        return cardService.saveCard(userId, principal);
    }

    @Operation(summary="명함 리스트 및 키워드 검색", description = "명함 리스트 및 키워드 검색")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CardDto.class)))
                    })
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/list")
    public ResponseEntity<?> getAllCards(Principal principal,@RequestParam(required = false, defaultValue = "", value = "search") String search ) {
        return cardService.getAllCard(principal, search);
    }

    @Operation(summary="친구 명함 삭제", description = "친구 명함 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "delete complete")})
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/card/{userId}")
    public ResponseEntity<?> deleteCard(@PathVariable(name="userId") Long userId, Principal principal) {
        return cardService.deleteCard(userId, principal);
    }

    @Operation(summary="텍스트 색상 및 백그라운드 색상 바꾸기", description = "텍스트 색상 및 백그라운드 색상 바꾸기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "save complete")})
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/card/color")
    public ResponseEntity<?> changeColor(@RequestBody ColorDto colorDto, Principal principal) {
        return cardService.changeColor(colorDto, principal);
    }

}
