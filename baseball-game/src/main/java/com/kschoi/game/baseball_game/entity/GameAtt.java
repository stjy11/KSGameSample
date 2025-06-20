package com.kschoi.game.baseball_game.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_att")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameAtt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_att_id")
    private Long gameAttId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameMain gameMain;

    @Column(name = "game_att_num", nullable = false)
    private Integer gameAttNum;

    @Column(name = "input_num", nullable = false, length = 3)
    private String inputNum;

    @Column(name = "game_hint", nullable = false, length = 20)
    private String gameHint;

    @Column(name = "att_date", nullable = false, updatable = false)
    private LocalDateTime attDate = LocalDateTime.now();
}