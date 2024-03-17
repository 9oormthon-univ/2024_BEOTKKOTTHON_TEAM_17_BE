package org.goormuniv.ponnect.dto;


import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryCreateDto {

    @Builder.Default
    private String categoryName = "카테고리";
}
