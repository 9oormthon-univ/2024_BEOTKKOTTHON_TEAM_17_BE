package org.goormuniv.ponnect.repository;

import org.goormuniv.ponnect.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByMemberId(Long memberId);

    Optional<Category> findCategoryByIdAndMemberId(Long categoryId, Long MemberId);
}
