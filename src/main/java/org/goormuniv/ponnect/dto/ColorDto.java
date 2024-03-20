package org.goormuniv.ponnect.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColorDto {
    private String bgColor;
    private String textColor;
    private List<StickerDto> StickerList;
}
