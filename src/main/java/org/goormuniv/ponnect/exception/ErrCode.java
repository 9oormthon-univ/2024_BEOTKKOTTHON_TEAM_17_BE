package org.goormuniv.ponnect.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrCode {
    UNAUTHORIZED("P401", "인증에 실패하였습니다.", HttpStatus.UNAUTHORIZED.value()),
    INVALID_ACCESS_TOKEN("P007", "JWT가 유효하지 않습니다.", HttpStatus.FORBIDDEN.value()),
    DUPLICATED_EMAIL("P0006", "이미 사용중인 이메일입니다.", HttpStatus.BAD_REQUEST.value()),
    LOGIN_FAILED("P001", "아이디 또는 비밀번호가 잘못되었습니다.", HttpStatus.BAD_REQUEST.value()),
    SIGN_UP_FAILED("P002", "회원가입에 실패하였습니다.", HttpStatus.NOT_ACCEPTABLE.value()),
    REISSUE_FAILED("P003", "비밀번호 재발급에 실패했습니다.", HttpStatus.NOT_ACCEPTABLE.value()),
    ALREADY_MEMBER("P004", "이미 가입된 사용자 입니다.", HttpStatus.BAD_REQUEST.value()),
    USER_NOT_FOUND("P005", "사용자를 찾을 수 없습니다,", HttpStatus.NOT_FOUND.value()),
    ALREADY_FOLLOW("P100", "이미 추가한 사용자입니다.", HttpStatus.BAD_REQUEST.value()),
    NO_EXIST_FOLLOW_MEMBER("P101", "해당 회원은 팔로우 된 상태가 아닙니다.", HttpStatus.BAD_REQUEST.value()),
    NO_EXIST_CARD("P102","해당 명함이 존재하지 않습니다." , HttpStatus.NOT_FOUND.value()),
    SELF_FOLLOW("P103", "자신을 팔로우 할 수 업습니다.", HttpStatus.BAD_REQUEST.value()),
    NO_EXIST_CATEGORY("P104", "해당 카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    NOT_CONTENT("P105", "추가할 항목이 없습니다.", HttpStatus.NOT_ACCEPTABLE.value()),
    NOT_FOUND_MEMBER("P410", "사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    INTERNAL_SERVER_ERROR("P500", "서버가 요청 처리에 실패하였습니다..", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    CREATE_CARD_TO_CATEGORY_FAILED("P106", "명함함 등록에 실패하였습니다.", HttpStatus.NOT_ACCEPTABLE.value()),
    RENAME_CATEGORY_FAILED("P107", "카테고리 이름 수정에 실파하였습니다.", HttpStatus.NOT_ACCEPTABLE.value()),
    REMOVE_CARD_OF_CATEGORY_FAILED("P108", "카테고리 내 명함 삭제에 실파하였습니다.", HttpStatus.NOT_ACCEPTABLE.value()),
    NO_EXIST_CARD_OF_CATEGORY("P109", "해당 카테고리 내 명함을 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    REMOVE_CATEGORY_FAILED("P110", "해당 카테고리를 삭제할 수 없습니다.", HttpStatus.NOT_ACCEPTABLE.value()),
    NO_PERMISSION("P406", "접근 권한이 없어 수행할 수 없습니다.", HttpStatus.NOT_ACCEPTABLE.value());





    private final String code;
    private final String message;
    private final int status;



}
