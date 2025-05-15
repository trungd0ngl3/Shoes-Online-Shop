package com.shoestore.shoestoreWeb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class Test {
    @GetMapping("/helo")
    public ResponseEntity<String> getExample() {
        return ResponseEntity.ok("Hello World");
    }
}

