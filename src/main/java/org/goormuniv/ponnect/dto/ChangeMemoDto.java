package org.goormuniv.ponnect.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeMemoDto {
    private Long userId;
    private String memo;
}
