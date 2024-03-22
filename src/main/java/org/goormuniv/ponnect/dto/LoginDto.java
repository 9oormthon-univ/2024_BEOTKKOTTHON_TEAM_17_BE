package org.goormuniv.ponnect.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {
    private String principal;
    private String credential;
}
