package org.goormuniv.ponnect.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterDto {

    private String name;
    private String email;
    private String password;
    private String phone;
}
