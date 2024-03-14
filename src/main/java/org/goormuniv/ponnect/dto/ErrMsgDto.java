package org.goormuniv.ponnect.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrMsgDto {
    private String message;
    private int statusCode;
}
