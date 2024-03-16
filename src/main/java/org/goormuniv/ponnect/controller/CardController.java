package org.goormuniv.ponnect.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.goormuniv.ponnect.auth.PrincipalDetails;
import org.goormuniv.ponnect.dto.CardCreateDto;
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
    @PostMapping("/card")
    public ResponseEntity<?> createMyCard(@RequestBody CardCreateDto cardCreateDto, Principal principal) throws ServletException, IOException {
        return cardService.createCard(cardCreateDto, principal);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card")
    public ResponseEntity<?> getMyCard(Principal principal){
        return cardService.getMyCard(principal);
    }
}
