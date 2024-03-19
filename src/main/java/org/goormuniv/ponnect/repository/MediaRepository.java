package org.goormuniv.ponnect.repository;

import org.goormuniv.ponnect.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findAllByCardId(Long CardId);
}
