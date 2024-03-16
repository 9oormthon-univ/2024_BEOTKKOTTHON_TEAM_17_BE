package org.goormuniv.ponnect.controller;


import lombok.RequiredArgsConstructor;
import org.goormuniv.ponnect.auth.PrincipalDetails;
import org.goormuniv.ponnect.repository.CardRepository;
import org.goormuniv.ponnect.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CardController {

    private final CardService cardService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/card")
    public ResponseEntity<?> getMyCard(Principal principal){
        return cardService.getMyCard(principal);
    }
}
