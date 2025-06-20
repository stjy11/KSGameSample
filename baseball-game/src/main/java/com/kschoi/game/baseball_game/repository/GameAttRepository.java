package com.kschoi.game.baseball_game.repository;

import com.kschoi.game.baseball_game.entity.GameAtt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameAttRepository extends JpaRepository<GameAtt, Long> {
    // 게임 기록 표를 불러올 때 사용
    List<GameAtt> findByGameMain_GameIdOrderByAttDateAsc(Long gameId);
}