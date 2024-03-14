package org.goormuniv.ponnect.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SMTPMsgDto {
    private String address;
    private String title;
    private String message;
}
