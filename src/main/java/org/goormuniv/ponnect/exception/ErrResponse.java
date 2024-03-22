package org.goormuniv.ponnect.exception;

import lombok.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class ErrResponse {

    private String code;

    private int status;

    private String message;

    public ErrResponse(ErrCode errCode) {
        this.code = errCode.getCode();
        this.message = errCode.getMessage();
        this.status = errCode.getStatus();
    }

}