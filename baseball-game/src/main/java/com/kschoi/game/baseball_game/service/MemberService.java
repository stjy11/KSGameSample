package com.kschoi.game.baseball_game.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kschoi.game.baseball_game.entity.Member;
import com.kschoi.game.baseball_game.repository.MemberRepository;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Long loginMember(String memberIdInput, String memberPasswordInput) {
        Optional<Member> memberOptional = memberRepository.findByMemberName(memberIdInput);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if (member.getMemberPassword().equals(memberPasswordInput)) {
                return member.getMemberId();
            }
        }
        return null; // 사용자를 찾지 못했거나 비밀번호가 일치하지 않음
    }
    
    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

}