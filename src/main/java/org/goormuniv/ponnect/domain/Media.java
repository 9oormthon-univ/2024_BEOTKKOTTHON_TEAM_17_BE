package org.goormuniv.ponnect.domain;


import jakarta.persistence.*;
import lombok.*;
import org.goormuniv.ponnect.domain.base.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(callSuper = true)
public class Media extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String type; //스티커의 종류

    @Column
    private Double posX; //x좌표

    @Column
    private Double posY; //y좌표

    @Column
    private Integer zIndex;  // z-Index


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "card_id")
    @ToString.Exclude
    private Card card;
}
