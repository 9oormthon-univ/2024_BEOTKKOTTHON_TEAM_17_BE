package org.goormuniv.ponnect.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.goormuniv.ponnect.auth.PrincipalDetails;
import org.goormuniv.ponnect.dto.CardCreateDto;
import org.goormuniv.ponnect.dto.ColorDto;
import org.goormuniv.ponnect.repository.CardRepository;
import org.goormuniv.ponnect.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CardController {

    private final CardService cardService;

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/card")
    public ResponseEntity<?> createMyCard(@RequestBody CardCreateDto cardCreateDto, Principal principal) {
        return cardService.createCard(cardCreateDto, principal);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card")
    public ResponseEntity<?> getMyCard(Principal principal){
        return cardService.getMyCard(principal);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/card/save/{userId}")
    public ResponseEntity<?> saveCard(@PathVariable(name="userId") Long userId, Principal principal) {
        return cardService.saveCard(userId, principal);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card/list")
    public ResponseEntity<?> getAllCards(Principal principal,@RequestParam(required = false, defaultValue = "", value = "search") String search ) {
        return cardService.getAllCard(principal, search);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/card/{userId}")
    public ResponseEntity<?> deleteCard(@PathVariable(name="userId") Long userId, Principal principal) {
        return cardService.deleteCard(userId, principal);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/card/color")
    public ResponseEntity<?> changeColor(@RequestBody ColorDto colorDto, Principal principal) {
        return cardService.changeColor(colorDto, principal);
    }
}
