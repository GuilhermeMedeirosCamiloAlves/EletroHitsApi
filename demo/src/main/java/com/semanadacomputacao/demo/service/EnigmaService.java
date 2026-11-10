package com.semanadacomputacao.demo.service;

import com.semanadacomputacao.demo.model.equipemodelo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EnigmaService {
    private final Map<String, equipemodelo> equipes = new ConcurrentHashMap<>();
    private final Map<Integer, String> senhas = new HashMap<>();
    private final Map<Integer, String> conteudos = new HashMap<>();

    public EnigmaService() {
        senhas.put(1, "senha1");
        senhas.put(2, "senha2");
        senhas.put(3, "senha3");

        conteudos.put(1, "=== CÓDIGO DO ENIGMA 1 ===\n\nSystem.out.println('Parabéns, chave 1 encontrada!');\n// Dica para a próxima fase: Procure no laboratório.");
        conteudos.put(2, "=== CÓDIGO DO ENIGMA 2 ===\n\nSELECT * FROM segredos WHERE id = 42;");
        conteudos.put(3, "=== CÓDIGO DO ENIGMA 3 ===\n\n01001000 01100101 01111000 01100001");

        equipes.put("pc1", new equipemodelo("pc1", "Equipe 1"));
        equipes.put("pc2", new equipemodelo("pc2", "Equipe 2"));
        equipes.put("pc3", new equipemodelo("pc3", "Equipe 3"));
        equipes.put("pc4", new equipemodelo("pc4", "Equipe 4"));
    }

    public Map<String, Object> submeterSenha(String equipeId, String senhaTentativa) {
        equipemodelo equipe = equipes.get(equipeId);
        Map<String, Object> resposta = new HashMap<>();

        if (equipe == null) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Equipe não encontrada no sistema!");
            return resposta;
        }

        for (Map.Entry<Integer, String> entry : senhas.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(senhaTentativa)) {
                int tarefaId = entry.getKey();

                if (equipe.getTarefasConcluidas().contains(tarefaId)) {
                    resposta.put("sucesso", false);
                    resposta.put("mensagem", "Vocês já resolveram este enigma!");
                    return resposta;
                } else {
                    equipe.getTarefasConcluidas().add(tarefaId);
                    equipe.adicionarPontos(1); // Incrementa 1 ponto por acerto
                    equipe.setUltimaAtualizacao(System.currentTimeMillis());

                    resposta.put("sucesso", true);
                    resposta.put("mensagem", "Enigma concluído com sucesso! (+1 ponto)");
                    resposta.put("pontuacaoAtual", equipe.getPontuacao());
                    resposta.put("conteudo", conteudos.get(tarefaId));
                    return resposta;
                }
            }
        }

        resposta.put("sucesso", false);
        resposta.put("mensagem", "Chave inválida");
        return resposta;
    }

    public Map<String, Object> adicionarPontosManuais(String equipeId, int pontos) {
        equipemodelo equipe = equipes.get(equipeId);
        Map<String, Object> resposta = new HashMap<>();

        if (equipe == null) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Equipe não encontrada!");
            return resposta;
        }

        equipe.adicionarPontos(pontos);
        equipe.setUltimaAtualizacao(System.currentTimeMillis());

        resposta.put("sucesso", true);
        resposta.put("mensagem", pontos + " ponto(s) adicionado(s) com sucesso!");
        resposta.put("equipeId", equipe.getId());
        resposta.put("pontuacaoTotal", equipe.getPontuacao());
        return resposta;
    }

    public Collection<equipemodelo> obterDashboard() {
        return equipes.values();
    }
}