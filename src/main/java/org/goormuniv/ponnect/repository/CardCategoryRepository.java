package org.goormuniv.ponnect.repository;

import org.goormuniv.ponnect.domain.CardCategory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardCategoryRepository extends JpaRepository<CardCategory, Long> {

    List<CardCategory> findAll(Specification<CardCategory> spec);
}
