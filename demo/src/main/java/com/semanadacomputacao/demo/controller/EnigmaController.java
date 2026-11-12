package com.semanadacomputacao.demo.controller;

import com.semanadacomputacao.demo.model.equipemodelo;
import com.semanadacomputacao.demo.service.EnigmaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
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

    @PostMapping("/concluir-etapa")
    public ResponseEntity<?> concluirEtapa(@RequestBody Map<String, Object> payload) {
        String equipeId = (String) payload.get("equipeId");
        Object tarefaObj = payload.get("tarefa");
        Object etapaObj = payload.get("etapa");

        if (equipeId == null || tarefaObj == null || etapaObj == null) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Os campos 'equipeId', 'tarefa' e 'etapa' são obrigatórios!");
            return ResponseEntity.badRequest().body(erro);
        }

        try {
            int tarefa = Integer.parseInt(tarefaObj.toString());
            int etapa = Integer.parseInt(etapaObj.toString());
            Map<String, Object> resultado = enigmaService.concluirEtapa(equipeId, tarefa, etapa);
            if ((Boolean) resultado.get("sucesso")) return ResponseEntity.ok(resultado);
            return ResponseEntity.badRequest().body(resultado);
        } catch (NumberFormatException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Os campos 'tarefa' e 'etapa' precisam ser números!");
            return ResponseEntity.badRequest().body(erro);
        }
    }

    @PostMapping("/adicionar-pontos")
    public ResponseEntity<?> adicionarPontos(@RequestBody Map<String, Object> payload) {
        String equipeId = (String) payload.get("equipeId");
        Object pontosObj = payload.get("pontos");

        if (equipeId == null || pontosObj == null) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Campos ausentes");
            return ResponseEntity.badRequest().body(erro);
        }

        try {
            int pontos = Integer.parseInt(pontosObj.toString());
            Map<String, Object> resultado = enigmaService.adicionarPontosManuais(equipeId, pontos);
            if ((Boolean) resultado.get("sucesso")) return ResponseEntity.ok(resultado);
            return ResponseEntity.status(404).body(resultado);
        } catch (NumberFormatException e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Formato numérico inválido");
            return ResponseEntity.badRequest().body(erro);
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Collection<equipemodelo>> obterStatus() {
        return ResponseEntity.ok(enigmaService.obterDashboard());
    }

    @PostMapping("/cronometro/iniciar")
    public ResponseEntity<?> iniciarCronometro(@RequestBody Map<String, Integer> payload) {
        Integer minutos = payload.get("minutos");
        if (minutos == null || minutos <= 0) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Informe os minutos (>0)");
            return ResponseEntity.badRequest().body(erro);
        }
        return ResponseEntity.ok(enigmaService.iniciarCronometro(minutos));
    }

    @PostMapping("/cronometro/parar")
    public ResponseEntity<?> pararCronometro() {
        return ResponseEntity.ok(enigmaService.pararCronometro());
    }

    @GetMapping("/cronometro")
    public ResponseEntity<?> statusCronometro() {
        return ResponseEntity.ok(enigmaService.statusCronometro());
    }
}