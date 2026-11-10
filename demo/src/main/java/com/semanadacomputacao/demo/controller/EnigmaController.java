package com.semanadacomputacao.demo.controller;

import com.semanadacomputacao.demo.model.equipemodelo;
import com.semanadacomputacao.demo.service.EnigmaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/enigma")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EnigmaController {

    private final EnigmaService enigmaService;

    public EnigmaController(EnigmaService enigmaService) {
        this.enigmaService = enigmaService;
    }

    @PostMapping("/submeter")
    public ResponseEntity<?> submeterSenha(@RequestBody Map<String, String> payload) {
        String equipeId = payload.get("equipeId");
        String senha = payload.get("senha");

        Map<String, Object> resultado = enigmaService.submeterSenha(equipeId, senha);
        boolean sucesso = (Boolean) resultado.get("sucesso");

        if (sucesso) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(401).body(resultado);
        }
    }

    @PostMapping("/adicionar-pontos")
    public ResponseEntity<?> adicionarPontos(@RequestBody Map<String, Object> payload) {
        String equipeId = (String) payload.get("equipeId");
        Object pontosObj = payload.get("pontos");

        if (equipeId == null || pontosObj == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", "Os campos 'equipeId' e 'pontos' são obrigatórios!"
            ));
        }

        int pontos;
        try {
            pontos = Integer.parseInt(pontosObj.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", "O campo 'pontos' precisa ser um número inteiro válido!"
            ));
        }

        Map<String, Object> resultado = enigmaService.adicionarPontosManuais(equipeId, pontos);
        boolean sucesso = (Boolean) resultado.get("sucesso");

        if (sucesso) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(404).body(resultado);
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Collection<equipemodelo>> obterStatus() {
        return ResponseEntity.ok(enigmaService.obterDashboard());
    }
}