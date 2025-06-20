package com.kschoi.game.baseball_game.dto;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

 // 사용자가 로그인 시 입력할 아이디
@Size(min = 6, max = 6, message = "IDは6桁でなければなりません。")
 private String memberId;

 // 사용자가 로그인 시 입력할 비밀번호
@Size(min = 8, max = 8, message = "パスワードは8桁でなければなりません。")
 private String memberPassword;

}
