package br.edu.iff.ccc.webdev.controller.restapi;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1") // ajuste para /api se preferir sem versão
public class RestApiMainController {

    @GetMapping
    public Map<String, Object> getApiHome() {
        return Map.of(
            "name", "Receitas API",
            "version", "v1",
            "status", "ok"
        );
    }
}
