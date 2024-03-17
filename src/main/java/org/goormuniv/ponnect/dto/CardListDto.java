package org.goormuniv.ponnect.dto;

import lombok.*;

@Deprecated
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardListDto {
    private Long userId;
    private String name;
    private String phone;
    private String email;
}
