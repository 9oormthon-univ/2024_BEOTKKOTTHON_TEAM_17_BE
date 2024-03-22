package org.goormuniv.ponnect.dto;


import lombok.*;

@Deprecated
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrMsgDto {
    private String message;
    private int statusCode;
}
