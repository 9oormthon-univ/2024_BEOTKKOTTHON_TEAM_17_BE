package org.goormuniv.ponnect.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardCreateDto {
    private Long userId;
    private String organization;
    private String link;
    private String content;
    private String instagram;
    private String youtube;
    private String status;
    private String facebook;
    private String x;
    private String tictok;
    private String naverBlog;
    private String linkedIn;
    private String notefolio;
    private String behance;
    private String github;
    private String kakaoTalk;
}
