package org.goormuniv.ponnect.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    private Long categoryId;
    private String categoryName;
}
