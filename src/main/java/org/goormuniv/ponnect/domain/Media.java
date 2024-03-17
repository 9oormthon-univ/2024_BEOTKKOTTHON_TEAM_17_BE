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
    private String type;

    @Column
    private String posX;

    @Column
    private String posY;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "card_id")
    @ToString.Exclude
    private Card card;
}
