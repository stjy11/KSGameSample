package com.kschoi.game.baseball_game.controller;

import com.kschoi.game.baseball_game.dto.GamePlayDto;
import com.kschoi.game.baseball_game.dto.GameResultDto;
import com.kschoi.game.baseball_game.dto.GameStatDto;
import com.kschoi.game.baseball_game.service.GameService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;

    @GetMapping("/game-main")
    public String gameMain(HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute("loggedInMemberId");
        if (memberId == null) {
            log.warn("로그인되지 않은 사용자가 게임 메인 페이지에 접근 시도. 로그인 페이지로 리다이렉트.");
            return "redirect:/login";
        }

        try {
            GameStatDto gameStat = gameService.getGameStatusForMain(memberId);
            model.addAttribute("gameStat", gameStat);
            log.info("게임 메인 페이지 로드 성공: 성공 ID = {}", memberId);
        } catch (Exception e) {
            log.error("게임 메인 페이지 로드 중 오류 발생: Member ID = {}", memberId, e);
            model.addAttribute("errorMessage", "게임 로드 중 오류가 발생했습니다.");
            return "error/errorPage";
        }
        return "game-main";
    }

    @PostMapping("/game-guess")
    @ResponseBody
    public GameStatDto processGuess(@SessionAttribute("loggedInMemberId") Long memberId,
                                    @Valid @RequestBody GamePlayDto playDto,
                                    BindingResult bindingResult) {

        if (memberId == null) {
            log.warn("세션 만료 또는 로그인되지 않은 사용자의 예측 시도. 에러 상태 반환.");
            GameStatDto errorStat = new GameStatDto();
            errorStat.setMessage("세션이 만료되었거나 로그인되지 않았습니다. 다시 로그인해주세요.");
            errorStat.setGameOver(true);
            return errorStat;
        }

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            log.warn("사용자 입력 유효성 검사 실패: Member ID = {}, Input = {}, Errors = {}", memberId, playDto.getInputNumber(), errorMessage);
            try {
                GameStatDto errorStat = gameService.getGameStatusForMain(memberId);
                errorStat.setMessage(errorMessage);
                return errorStat;
            } catch (Exception e) {
                log.error("유효성 검사 실패 후 게임 상태 로드 중 오류 발생: Member ID = {}", memberId, e);
                GameStatDto fatalError = new GameStatDto();
                fatalError.setMessage("입력값 오류 및 게임 상태 로드 중 치명적인 오류 발생.");
                fatalError.setGameOver(true);
                return fatalError;
            }
        }

        try {
            log.info("사용자 예측 처리 시작: Member ID = {}, Input = {}", memberId, playDto.getInputNumber());
            GameStatDto updatedStat = gameService.processGuess(memberId, playDto);
            log.info("사용자 예측 처리 완료: Member ID = {}, Result = {}", memberId, updatedStat);

            if (updatedStat.isGameOver()) {
                log.info("게임 종료: Member ID = {}, Game Won = {}", memberId, updatedStat.isGameWon());
            }
            return updatedStat;

        } catch (IllegalStateException e) {
            log.warn("게임 진행 중 상태 오류 발생: Member ID = {}, Error = {}", memberId, e.getMessage());
            try {
                GameStatDto errorStat = gameService.getGameStatusForMain(memberId);
                errorStat.setMessage(e.getMessage());
                errorStat.setGameOver(true);
                return errorStat;
            } catch (Exception ex) {
                log.error("상태 오류 후 게임 상태 로드 중 오류 발생: Member ID = {}", memberId, ex);
                GameStatDto fatalError = new GameStatDto();
                fatalError.setMessage("게임 상태 처리 중 치명적인 오류 발생.");
                fatalError.setGameOver(true);
                return fatalError;
            }
        } catch (Exception e) {
            log.error("숫자 예측 처리 중 알 수 없는 오류 발생: Member ID = {}, Input = {}", memberId, playDto.getInputNumber(), e);
            try {
                GameStatDto errorStat = gameService.getGameStatusForMain(memberId);
                errorStat.setMessage("게임 처리 중 알 수 없는 오류가 발생했습니다.");
                errorStat.setGameOver(true);
                return errorStat;
            } catch (Exception ex) {
                log.error("일반 오류 후 게임 상태 로드 중 오류 발생: Member ID = {}", memberId, ex);
                GameStatDto fatalError = new GameStatDto();
                fatalError.setMessage("게임 처리 중 치명적인 오류 발생.");
                fatalError.setGameOver(true);
                return fatalError;
            }
        }
    }


    @GetMapping("/game-result")
    public String gameResult(HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute("loggedInMemberId");
        if (memberId == null) {
            log.warn("로그인되지 않은 사용자가 게임 결과 페이지에 접근 시도. 로그인 페이지로 리다이렉트.");
            return "redirect:/login";
        }

        try {
            GameResultDto gameResult = gameService.getGameResult(memberId);
            model.addAttribute("gameResult", gameResult);
            log.info("게임 결과 페이지 로드 성공: Member ID = {}", memberId);
        } catch (Exception e) {
            log.error("게임 결과 페이지 로드 중 오류 발생: Member ID = {}", memberId, e);
            model.addAttribute("errorMessage", "게임 결과 로드 중 오류가 발생했습니다.");
            return "error/errorPage";
        }
        return "game-result";
    }
}