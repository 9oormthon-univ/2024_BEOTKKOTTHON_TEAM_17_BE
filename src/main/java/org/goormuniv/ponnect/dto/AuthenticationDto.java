package org.goormuniv.ponnect.dto;


import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationDto {
    private Long userId;
    private String name;
    private String email;
    private String phone;
}

