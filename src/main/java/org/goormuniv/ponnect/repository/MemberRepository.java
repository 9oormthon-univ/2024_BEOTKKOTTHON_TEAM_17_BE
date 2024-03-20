package org.goormuniv.ponnect.repository;

import org.goormuniv.ponnect.domain.Follow;
import org.goormuniv.ponnect.domain.Member;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);



    Optional<Member> findByEmailAndPhone(String email, String phone);

    List<Member> findAll(Specification<Member> spec);


}
