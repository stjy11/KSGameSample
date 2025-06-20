package com.kschoi.game.baseball_game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class helloworld {

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello, World!";
    }
}