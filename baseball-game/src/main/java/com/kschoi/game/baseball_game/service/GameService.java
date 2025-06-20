package com.kschoi.game.baseball_game.service;

import com.kschoi.game.baseball_game.dto.GamePlayDto;
import com.kschoi.game.baseball_game.dto.GameResultDto;
import com.kschoi.game.baseball_game.dto.GameStatDto;
import com.kschoi.game.baseball_game.dto.GameStatDto.GameRecordEntryDto;
import com.kschoi.game.baseball_game.entity.GameAtt;
import com.kschoi.game.baseball_game.entity.GameMain;
import com.kschoi.game.baseball_game.entity.GamePoint;
import com.kschoi.game.baseball_game.entity.Member;
import com.kschoi.game.baseball_game.repository.GameAttRepository;
import com.kschoi.game.baseball_game.repository.GameMainRepository;
import com.kschoi.game.baseball_game.repository.GamePointRepository;
import com.kschoi.game.baseball_game.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok이 final 필드를 위한 생성자를 자동으로 생성
@Transactional // 이 클래스의 모든 메서드가 기본적으로 트랜잭션 범위 내에서 실행되도록 함.
public class GameService {

    private final GameMainRepository gameMainRepository;
    private final GamePointRepository gamePointRepository;
    private final GameAttRepository gameAttRepository;
    private final MemberRepository memberRepository;


    private static final int MAX_ATTEMPTS = 10; // 최대 시도 횟수 설정

    // 새로운 게임 시작 또는 기존 게임 로드를 처리
    public GameStatDto getGameStatusForMain(Long memberId) {
        LocalDate today = LocalDate.now();
        GameMain currentGame = null;
        GamePoint memberPoint = null;

        // 회원 정보, 보유 포인트 조회
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId);
        }
        Member member = memberOpt.get();

        // GamePoint 조회는 gamePointRepository 사용
        Optional<GamePoint> gamePointOpt = gamePointRepository.findByMember_MemberId(memberId);
        if (gamePointOpt.isPresent()) {
            memberPoint = gamePointOpt.get();
        } else {
            memberPoint = new GamePoint();
            memberPoint.setMember(member);
            memberPoint.setTotalPoint(0);
            memberPoint.setUpPointDate(LocalDateTime.now());
            gamePointRepository.save(memberPoint);
        }

        // 오늘 날짜로 진행 중인 게임이 있는지 확인 (GameMain 조회는 gameMainRepository 사용)
        Optional<GameMain> existingGameOpt = gameMainRepository.findByMember_MemberIdAndPlayDate(memberId, today);

        if (existingGameOpt.isPresent()) {
            currentGame = existingGameOpt.get();
            return buildGameStatDto(currentGame, memberPoint, null, null); 
        } else {
            currentGame = startNewGame(member);
            return buildGameStatDto(currentGame, memberPoint, null, null);
        }
    }

    private GameMain startNewGame(Member member) {
        String hiddenNum = generateRandomHiddenNumber();
        GameMain newGame = new GameMain();
        newGame.setMember(member);
        newGame.setHiddenNum(hiddenNum);
        newGame.setPlayDate(LocalDate.now()); // playDate는 LocalDate로 변경되었으므로 LocalDateTime 대신 LocalDate.now()
        newGame.setGameSuccess(false);
        newGame.setGamePlayAtt(0);
        
        return gameMainRepository.save(newGame);
    }


    // 사용자로부터 숫자를 입력받아 게임을 진행하고 결과를 업데이트
    public GameStatDto processGuess(Long memberId, GamePlayDto playDto) {
        LocalDate today = LocalDate.now();
        GameMain currentGame = gameMainRepository.findByMember_MemberIdAndPlayDate(memberId, today)
                .orElseThrow(() -> new IllegalStateException("진행 중인 게임이 없습니다."));

        // 게임이 이미 종료되었다면 더 이상 시도 불가
        if (currentGame.getGameSuccess() || currentGame.getGamePlayAtt() >= MAX_ATTEMPTS) {
            throw new IllegalStateException("이미 종료된 게임입니다.");
        }

        String inputNumString = playDto.getInputNumber();

        String systemMessage = null; // 시스템 메시지 (오류 또는 게임 결과)
        String currentTurnHint = null; // 현재 턴의 힌트 (S/B 결과)


        // 입력값 유효성 검사 (숫자 형식 및 중복 여부)
        if (!isValidInputNumber(inputNumString)) {
            GamePoint currentPoint = gamePointRepository.findByMember_MemberId(memberId).orElseGet(GamePoint::new);
            return buildGameStatDto(currentGame, currentPoint, "3자리의 중복 없는 숫자를 입력해주세요.", null);
        }

        // 시도 횟수 증가 및 GameMain 업데이트
        currentGame.setGamePlayAtt(currentGame.getGamePlayAtt() + 1);

        // 숫자 비교 및 판정
        String hiddenNum = currentGame.getHiddenNum();
        String hint = calculateHint(hiddenNum, inputNumString);
        currentTurnHint = hint; // 현재 턴의 힌트 설정

        GameAtt gameAtt = new GameAtt();
        gameAtt.setGameMain(currentGame);
        gameAtt.setInputNum(inputNumString);
        gameAtt.setGameAttNum(currentGame.getGamePlayAtt()); // 현재 시도 횟수 할당
        gameAtt.setGameHint(hint);
        gameAttRepository.save(gameAtt);

        // 게임 종료 여부 판단 (승리/패배)
        if (hint.equals("当たり")) { // 3S일 경우
            currentGame.setGameSuccess(true); // 게임 성공
            int pointsEarnedInThisGame = calculatePoints(currentGame.getGamePlayAtt());
            currentGame.setEarnedPoints(pointsEarnedInThisGame); // 이 게임에서 획득한 포인트 설정
            updatePointsForWin(memberId, currentGame.getGamePlayAtt()); // 포인트 업데이트
            systemMessage = null; // 시스템 메시지 설정
            currentTurnHint = null; // 게임 종료 시 힌트 메시지는 표시 안함
        } else if (currentGame.getGamePlayAtt() >= MAX_ATTEMPTS) {
            // 10회 시도 초과 (패배)
            currentGame.setGameSuccess(false); // 실패
            systemMessage = null;
            currentTurnHint = null; // 게임 종료 시 힌트 메시지는 표시 안함
        } else {
            systemMessage = null;
        }
        
        gameMainRepository.save(currentGame);

        // 업데이트된 게임 상태 반환
        GamePoint updatedPoint = gamePointRepository.findByMember_MemberId(memberId).orElseGet(GamePoint::new);
        return buildGameStatDto(currentGame, updatedPoint, systemMessage, currentTurnHint);
    }

    // 게임 결과 페이지에 필요한 정보를 제공
    public GameResultDto getGameResult(Long memberId) {
        LocalDate today = LocalDate.now();
        GameMain finishedGame = gameMainRepository.findByMember_MemberIdAndPlayDate(memberId, today)
                .orElseThrow(() -> new IllegalStateException("終了したゲームが見つかりません。"));

        if (!finishedGame.getGameSuccess() && finishedGame.getGamePlayAtt() < MAX_ATTEMPTS) {
            throw new IllegalStateException("ゲームはまだ終了していません。");
        }

        GameResultDto resultDto = new GameResultDto();
        resultDto.setGameWon(finishedGame.getGameSuccess());
        resultDto.setGotPoints(finishedGame.getEarnedPoints()); // 이 게임에서 획득한 포인트

        resultDto.setFinalHiddenNumber(finishedGame.getHiddenNum());
        resultDto.setTotalAttempts(finishedGame.getGamePlayAtt());
        resultDto.setResultMessage(finishedGame.getGameSuccess() ? "挑戦に成功しました！" : "挑戦に失敗しました…");

        return resultDto;
    }


    //숨겨진 3자리 중복 없는 숫자를 생성
    private String generateRandomHiddenNumber() {
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            digits.add(i);
        }
        Collections.shuffle(digits);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(digits.get(i));
        }
        return sb.toString();
    }

    // 사용자 입력 숫자의 유효성을 검사
    private boolean isValidInputNumber(String inputNum) {
        if (inputNum == null || inputNum.length() != 3 || !inputNum.matches("\\d{3}")) {
            return false;
        }

        // 중복 숫자 검사
        char[] chars = inputNum.toCharArray();
        return chars[0] != chars[1] && chars[0] != chars[2] && chars[1] != chars[2];
    }

    // 숨겨진 숫자와 사용자 입력 숫자를 비교하여 힌트 문자열을 계산
    private String calculateHint(String hiddenNum, String inputNum) {
        int strikes = 0;
        int balls = 0;

        for (int i = 0; i < 3; i++) {
            if (hiddenNum.charAt(i) == inputNum.charAt(i)) {
                strikes++;
            } else if (hiddenNum.contains(String.valueOf(inputNum.charAt(i)))) {
                balls++;
            }
        }

        if (strikes == 3) {
            return "当たり";
        } else if (strikes == 0 && balls == 0) {
            return "はずれ";
        } else {
            return strikes + "S" + balls + "B";
        }
    }
    
    // 1. 메시지와 힌트 메시지 모두 null인 경우 (초기 게임 로드 시 등)
    private GameStatDto buildGameStatDto(GameMain gameMain, GamePoint memberPoint) {
        return buildGameStatDto(gameMain, memberPoint, null, null); 
    }

    // 2. 시스템 메시지만 있고 힌트 메시지는 null인 경우 (오류 메시지 표시 시 등)
    private GameStatDto buildGameStatDto(GameMain gameMain, GamePoint memberPoint, String message) {
        return buildGameStatDto(gameMain, memberPoint, message, null); 
    }

    // 3. 메시지와 힌트 메시지를 모두 받는 최종 오버로드 메서드 (모든 정보 설정)
    private GameStatDto buildGameStatDto(GameMain gameMain, GamePoint memberPoint, String message, String hintMessage) {
        GameStatDto statDto = new GameStatDto();
        statDto.setTotalPoints(memberPoint != null ? memberPoint.getTotalPoint() : 0);
        statDto.setHiddenNumber(gameMain.getHiddenNum()); // 개발/디버깅용, 실제 배포 시 숨겨야 함
        statDto.setGameOver(gameMain.getGameSuccess() || gameMain.getGamePlayAtt() >= MAX_ATTEMPTS);
        statDto.setGameWon(gameMain.getGameSuccess());
        statDto.setAttemptsLeft(MAX_ATTEMPTS - gameMain.getGamePlayAtt());
        statDto.setCurrentAttemptCount(gameMain.getGamePlayAtt());

        List<GameAtt> gameAtts = gameAttRepository.findByGameMain_GameIdOrderByAttDateAsc(gameMain.getGameId());
        List<GameRecordEntryDto> recordEntries = gameAtts.stream()
                .map(att -> new GameRecordEntryDto(
                        gameAtts.indexOf(att) + 1, // 순번 (1부터 시작)
                        att.getInputNum(),
                        att.getGameHint()
                ))
                .collect(Collectors.toList());
        statDto.setGameRecords(recordEntries);
        
        statDto.setMessage(message != null ? message : ""); // 일반 메시지 설정
        statDto.setHintMessage(hintMessage != null ? hintMessage : ""); // 힌트 메시지 설정

        return statDto;
    }

    // 게임 승리 시 포인트 업데이트 로직
    private void updatePointsForWin(Long memberId, int attempts) {
        Optional<GamePoint> gamePointOpt = gamePointRepository.findByMember_MemberId(memberId);
        GamePoint gamePoint = gamePointOpt.orElseGet(() -> {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));
            GamePoint newPoint = new GamePoint();
            newPoint.setMember(member);
            newPoint.setTotalPoint(0);
            return newPoint;
        });

        int pointsEarned = calculatePoints(attempts);
        gamePoint.setTotalPoint(gamePoint.getTotalPoint() + pointsEarned);
        gamePoint.setUpPointDate(LocalDateTime.now());
        gamePointRepository.save(gamePoint);
    }


    // 시도 횟수에 따른 포인트 계산 로직
    private int calculatePoints(int attempts) {
        if (attempts >= 1 && attempts <= 5) {
            return 1000;
        } else if (attempts >= 6 && attempts <= 7) {
            return 500;
        } else if (attempts >= 8 && attempts <= 10) {
            return 200;
        }
        return 0;
    }
}
