package org.goormuniv.ponnect.repository;

import org.goormuniv.ponnect.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findCardById(Long id);
    Optional<Card> findByMemberId(Long memberId);
}
