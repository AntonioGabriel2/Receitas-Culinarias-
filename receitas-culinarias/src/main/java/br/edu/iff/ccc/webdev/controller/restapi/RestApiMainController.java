package br.edu.iff.ccc.webdev.controller.restapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/v1")
public class RestApiMainController {

    @GetMapping
    public ResponseEntity<String> getApiHome() {
        return ResponseEntity.ok("API de Receitas em funcionamento!");
    }
}

