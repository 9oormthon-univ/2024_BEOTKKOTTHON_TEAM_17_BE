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
public class Follow extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "following_id")
    @ToString.Exclude
    private Member following;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "followed_id")
    @ToString.Exclude
    private Member followed;




    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
