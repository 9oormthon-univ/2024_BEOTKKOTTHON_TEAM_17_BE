package org.goormuniv.ponnect.domain;


import jakarta.persistence.*;
import lombok.*;
import org.goormuniv.ponnect.domain.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(callSuper = true)
public class Card extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String organisation; //조직

    @Column
    private String link; //링크

    @Column(columnDefinition = "TEXT")
    private String content; //추가 글

    @Column
    private String instagram;
    @Column
    private String youtube;


    @Column
    private String facebook;

    @Column
    private String x;
    @Column
    private String tiktok;

    @Column
    private String naver;

    @Column
    private String linkedIn;

    @Column
    private String notefolio;

    @Column
    private String behance;

    @Column
    private String github;

    @Column
    private String kakao;

    @Column
    @Builder.Default
    private String bgColor = "#ffe3e7"; //배경색

    @Column
    @Builder.Default
    private String textColor = "#000"; //텍스트 색상

    @OneToOne(mappedBy = "card", cascade = { CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Member member;

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Media> mediaList = new ArrayList<Media>();

}
