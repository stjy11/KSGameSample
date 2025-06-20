package com.kschoi.game.baseball_game.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "GAME_POINT")
@Getter
@Setter
@NoArgsConstructor
public class GamePoint {
	
	@Id
	@Column(name = "member_id")
	private Long memberId; // key이자 외래키
	
	@OneToOne(fetch = FetchType.LAZY) // 멤버 엔티티와 일대일 관계
	@MapsId // 게임포인트의 pk를 멤버 pk에서 매핑함
	@JoinColumn(name = "member_id")
	private Member member;
	
	@Column(name = "total_point", nullable = false)
	private Integer totalPoint;
	
	@Column(name = "up_point_date", nullable = false)
	private LocalDateTime upPointDate;
}
