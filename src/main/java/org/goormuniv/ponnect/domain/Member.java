package org.goormuniv.ponnect.domain;

import jakarta.persistence.*;
import lombok.*;
import org.goormuniv.ponnect.domain.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

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

    @Column
    private String qrUrl;

    @Column(nullable = false)
    private String role;

    @JoinColumn(name = "card_id")
    @OneToOne( cascade = { CascadeType.REMOVE}, orphanRemoval = true)
    @ToString.Exclude
    private Card card;

    @OneToMany(mappedBy = "following")
    @ToString.Exclude
    private List<Follow> followingList = new ArrayList<Follow>(); //자신이 팔로잉 한것만 알면 된다.

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Category> categoryList = new ArrayList<Category>(); //자신이 생성한 카테고리가 형성된다.




}
