package org.goormuniv.ponnect.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardToCategoryDto {
    Long categoryId;
    List<CardIdDto> cardIdList;
}
