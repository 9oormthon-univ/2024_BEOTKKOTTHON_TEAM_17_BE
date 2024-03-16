package org.goormuniv.ponnect.repository;

import org.goormuniv.ponnect.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository  extends JpaRepository<Card, Long> {
}
