package com.orioninc.controllers;

import com.orioninc.services.ListenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    ListenService listenService;

    @PostMapping("/")
    public String index(@RequestParam String key, @RequestBody String value) {
        return listenService.send(key, value);
    }
}
