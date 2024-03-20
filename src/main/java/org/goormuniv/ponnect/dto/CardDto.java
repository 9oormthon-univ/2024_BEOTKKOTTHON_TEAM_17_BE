package org.goormuniv.ponnect.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardDto {
    private Long cardId;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String qrUrl;
    private String organization; //조직
    private String status;
    private String link; //링크
    private String content; //추가 글
    private String instagram;
    private String youtube;
    private String facebook;
    private String x;
    private String tiktok;
    private String naver;
    private String linkedIn;
    private String notefolio;
    private String behance;
    private String github;
    private String kakao;
    private String bgColor;
    private String textColor;
    private List<StickerDto> stickerDtoList;
}
