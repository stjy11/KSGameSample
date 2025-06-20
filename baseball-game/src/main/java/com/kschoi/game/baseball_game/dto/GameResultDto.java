package com.kschoi.game.baseball_game.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameResultDto {
	private boolean isGameWon; // 게임 성공 여부 (true: 성공)
    private Integer gotPoints; // 획득 포인트
    private String finalHiddenNumber; // 최종 정답 숫자
    private Integer totalAttempts; // 총 시도 횟수
    
    private String resultMessage; // 게임 결과 메세지
}
