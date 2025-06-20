package com.kschoi.game.baseball_game.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "GAME_MAIN")
@Getter
@Setter
@NoArgsConstructor
public class GameMain {
	@Id // 이 필드가 테이블의 기본 키임을 알려줌
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임
	@Column(name = "game_id") // 실제 db 컬럼명
	private Long gameId;
	
	@ManyToOne(fetch = FetchType.LAZY) // member 엔티티와 다대일
	@JoinColumn(name = "member_id", nullable = false) //member id를 외래키로 지정, nullabe은 true면 null이 저장될수있음
	private Member member;
	
	@Column(name = "hidden_num", nullable = false, length = 3)
	private String hiddenNum;
	
	@Column(name = "play_date", nullable = false)
	private LocalDate playDate;
	
	@Column(name = "game_success", nullable = false)
	private Boolean gameSuccess; // 게임 성공여부 true 성공
	
	@Column(name = "game_play_att", nullable = false)
	private Integer gamePlayAtt;
	
	@Column(name = "earned_points", nullable = false) // 획득 포인트 (NULL 허용 안 함, 기본값 0)
    private Integer earnedPoints = 0;
}
