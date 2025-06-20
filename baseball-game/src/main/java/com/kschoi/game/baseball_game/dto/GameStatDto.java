package com.kschoi.game.baseball_game.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameStatDto {
	
	private Integer totalPoints; // 보유 포인트
	
	private String hiddenNumber;
	
	private boolean isGameOver; // 게임이 종료되었는지 여부 (3S이거나 10회 시도 초과)
	private boolean isGameWon; // 게임을 성공했는지 여부 (3S로 종료되었을 경우)
	
	private Integer attemptsLeft; // 남은 시도 횟수
	private Integer currentAttemptCount; // 현재까지 시도한 횟수
	
	private List<GameRecordEntryDto> gameRecords; // 오늘 게임 진행한 표 데이터
	
	private String message; //사용자에게 보여줄 메세지
	private String hintMessage;
	
	@Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameRecordEntryDto {
        private Integer attemptCount; // 회차
        private String inputNumber; // 사용자가 입력한 숫자
        private String hint; // 판정 결과
    }
	
	
}
