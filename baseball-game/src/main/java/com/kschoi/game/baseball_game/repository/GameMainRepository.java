package com.kschoi.game.baseball_game.repository;

import com.kschoi.game.baseball_game.entity.GameMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GameMainRepository extends JpaRepository<GameMain, Long> {
    // GameMain 엔티티 관련 메서드 (JpaRepository가 기본 CRUD 제공)

    // 특정 회원ID와 날짜에 해당하는 GameMain 엔티티를 조회.
    Optional<GameMain> findByMember_MemberIdAndPlayDate(Long memberId, LocalDate playDate);
}