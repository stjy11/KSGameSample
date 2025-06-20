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
            log.warn("ログインしていないメンバーがゲームにアクセスしようとする。 ログインページに移動。");
            return "redirect:/login";
        }

        try {
            GameStatDto gameStat = gameService.getGameStatusForMain(memberId);
            model.addAttribute("gameStat", gameStat);
            log.info("ゲームメインページのロード成功:会員ID = {}", memberId);
        } catch (Exception e) {
            log.error("ゲームメインページのロード中にエラー: 会員ID = {}", memberId, e);
            model.addAttribute("errorMessage", "ゲームのロード中にエラーが発生。");
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
            log.warn("ログインされていないユーザーのゲームの試行。");
            GameStatDto errorStat = new GameStatDto();
            errorStat.setMessage("ログインできませんでした。 もう一度ログインしてください。");
            errorStat.setGameOver(true);
            return errorStat;
        }

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            log.warn("会員入力チェック失敗: Member ID = {}, Input = {}, Errors = {}", memberId, playDto.getInputNumber(), errorMessage);
            try {
                GameStatDto errorStat = gameService.getGameStatusForMain(memberId);
                errorStat.setMessage(errorMessage);
                return errorStat;
            } catch (Exception e) {
                log.error("会員入力チェック失敗後のゲームロード中のエラー: Member ID = {}", memberId, e);
                GameStatDto fatalError = new GameStatDto();
                fatalError.setMessage("入力値エラーとゲームステータスのロード中に致命的なエラーが発生します。");
                fatalError.setGameOver(true);
                return fatalError;
            }
        }

        try {
            log.info("会員ゲーム進行処理開始: Member ID = {}, Input = {}", memberId, playDto.getInputNumber());
            GameStatDto updatedStat = gameService.processGuess(memberId, playDto);
            log.info("会員ゲーム進行処理完了: Member ID = {}, Result = {}", memberId, updatedStat);

            if (updatedStat.isGameOver()) {
                log.info("ゲーム終了: Member ID = {}, Game Won = {}", memberId, updatedStat.isGameWon());
            }
            return updatedStat;

        } catch (IllegalStateException e) {
            log.warn("ゲーム進行中に状態エラー発生: Member ID = {}, Error = {}", memberId, e.getMessage());
            try {
                GameStatDto errorStat = gameService.getGameStatusForMain(memberId);
                errorStat.setMessage(e.getMessage());
                errorStat.setGameOver(true);
                return errorStat;
            } catch (Exception ex) {
                log.error("ステータスエラー後、ゲームステータスのロード中にエラーが発生: Member ID = {}", memberId, ex);
                GameStatDto fatalError = new GameStatDto();
                fatalError.setMessage("ゲーム状態の処理中に致命的なエラーが発生します。");
                fatalError.setGameOver(true);
                return fatalError;
            }
        } catch (Exception e) {
            log.error("ゲーム進行処理中に不明なエラーが発生: Member ID = {}, Input = {}", memberId, playDto.getInputNumber(), e);
            try {
                GameStatDto errorStat = gameService.getGameStatusForMain(memberId);
                errorStat.setMessage("ゲームの処理中に不明なエラーが発生しました。");
                errorStat.setGameOver(true);
                return errorStat;
            } catch (Exception ex) {
                log.error("通常エラー後、ゲームステータスのロード中にエラーが発生: Member ID = {}", memberId, ex);
                GameStatDto fatalError = new GameStatDto();
                fatalError.setMessage("ゲーム処理中に致命的なエラーが発生。");
                fatalError.setGameOver(true);
                return fatalError;
            }
        }
    }


    @GetMapping("/game-result")
    public String gameResult(HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute("loggedInMemberId");
        if (memberId == null) {
            log.warn("ログインしていないユーザーがゲーム結果ページにアクセスしようとする。");
            return "redirect:/login";
        }

        try {
            GameResultDto gameResult = gameService.getGameResult(memberId);
            model.addAttribute("gameResult", gameResult);
            log.info("ゲーム結果ロード成功: Member ID = {}", memberId);
        } catch (Exception e) {
            log.error("ゲーム結果のロード中のエラー: Member ID = {}", memberId, e);
            model.addAttribute("errorMessage", "ゲームの結果、ロード中にエラーが発生しました。");
            return "error/errorPage";
        }
        return "game-result";
    }
}