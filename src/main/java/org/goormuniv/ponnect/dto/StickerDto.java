package org.goormuniv.ponnect.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StickerDto {
    private String type;
    private Double posX;
    private Double posY;
}
