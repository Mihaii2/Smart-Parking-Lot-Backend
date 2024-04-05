package com.smartparkinglot.backend;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class MobileAppController {
    @GetMapping
    public List<String> getStatus() {
        return List.of("hey", "bro");
    }

}
