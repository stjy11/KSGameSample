package com.kschoi.game.baseball_game.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kschoi.game.baseball_game.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 사용자의 로그인 ID인 memberName으로 Member를 찾도록 메서드 변경
    Optional<Member> findByMemberName(String memberName);
}
