package com.kschoi.game.baseball_game.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayDto {

      
    @Pattern(regexp = "^[0-9]{3}$", message = "3 つの数字を入力してください。")
    @Size(min = 3, max = 3, message = "3 つの数字を入力してください。")
    private String inputNumber;
    
}
