package org.goormuniv.ponnect.domain;

import jakarta.persistence.*;
import lombok.*;
import org.goormuniv.ponnect.domain.base.BaseEntity;

@Entity
@Getter
@Builder
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;


}
