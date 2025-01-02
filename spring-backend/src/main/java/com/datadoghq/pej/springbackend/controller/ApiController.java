package com.datadoghq.pej.springbackend.controller;


import com.datadoghq.pej.springbackend.model.ApiResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/data")
    public ApiResponse getData() {
        return new ApiResponse("Hello from Spring Boot API!");
    }
}

