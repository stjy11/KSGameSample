package com.kschoi.game.baseball_game.repository;

import com.kschoi.game.baseball_game.entity.GamePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GamePointRepository extends JpaRepository<GamePoint, Long> {
    // GamePoint 엔티티와 그 Primary Key 타입 (Long)을 지정합니다.
    Optional<GamePoint> findByMember_MemberId(Long memberId);
}