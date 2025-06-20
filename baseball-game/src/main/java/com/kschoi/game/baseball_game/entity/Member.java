package com.kschoi.game.baseball_game.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MEMBER_KS")
@Getter
@Setter
@NoArgsConstructor
public class Member {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_name", nullable = false, unique = true, length = 50)
    private String memberName;

    @Column(name = "member_password", nullable = false, length = 100)
    private String memberPassword;

}