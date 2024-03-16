package org.goormuniv.ponnect.repository;

import org.goormuniv.ponnect.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);



    Optional<Member> findByEmailAndPhone(String email, String phone);
}
