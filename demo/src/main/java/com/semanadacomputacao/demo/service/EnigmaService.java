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

    private long tempoTermino = 0;
    private boolean cronometroAtivo = false;

    public EnigmaService() {
        // Senhas correspondentes à Etapa 1 de cada uma das 5 barras
        senhas.put(1, "senha1");
        senhas.put(2, "senha2");
        senhas.put(3, "senha3");
        senhas.put(4, "senha4");
        senhas.put(5, "senha5");

        conteudos.put(1, "=== CÓDIGO DO ENIGMA 1 ===\n\nSystem.out.println('Parabéns, chave 1 encontrada!');\n// Dica para a próxima fase: Procure no laboratório.");
        conteudos.put(2, "=== CÓDIGO DO ENIGMA 2 ===\n\nSELECT * FROM segredos WHERE id = 42;");
        conteudos.put(3, "=== CÓDIGO DO ENIGMA 3 ===\n\n01001000 01100101 01111000 01100001");
        conteudos.put(4, "=== CÓDIGO DO ENIGMA 4 ===\n\n// Conteúdo da fase 4 desbloqueado!");
        conteudos.put(5, "=== CÓDIGO DO ENIGMA 5 ===\n\n// Conteúdo da fase 5 desbloqueado!");

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
                // Índice apenas da Etapa 1 da tarefa correspondente (1, 4, 7, 10 ou 13)
                int etapa1Id = ((tarefaId - 1) * 3) + 1;

                if (equipe.getTarefasConcluidas().contains(etapa1Id)) {
                    resposta.put("sucesso", false);
                    resposta.put("mensagem", "Vocês já desbloquearam a etapa 1 deste desafio!");
                    return resposta;
                } else {
                    equipe.getTarefasConcluidas().add(etapa1Id);
                    equipe.adicionarPontos(1);
                    equipe.setUltimaAtualizacao(System.currentTimeMillis());

                    resposta.put("sucesso", true);
                    resposta.put("mensagem", "Etapa 1 do desafio " + tarefaId + " concluída! (+1 ponto)");
                    resposta.put("pontuacaoAtual", equipe.getPontuacao());
                    resposta.put("conteudo", conteudos.get(tarefaId));
                    return resposta;
                }
            }
        }

        equipe.incrementarTentativasErradas();

        resposta.put("sucesso", false);
        resposta.put("mensagem", "Chave inválida");
        resposta.put("tentativasErradas", equipe.getTentativasErradas());
        return resposta;
    }

    public Map<String, Object> concluirEtapa(String equipeId, int tarefaId, int etapaId) {
        equipemodelo equipe = equipes.get(equipeId);
        Map<String, Object> resposta = new HashMap<>();

        if (equipe == null) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Equipe não encontrada!");
            return resposta;
        }

        if (etapaId < 1 || etapaId > 3) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "A etapa precisa ser 1, 2 ou 3!");
            return resposta;
        }

        int subTarefaId = ((tarefaId - 1) * 3) + etapaId;

        if (equipe.getTarefasConcluidas().contains(subTarefaId)) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Etapa " + etapaId + " do desafio " + tarefaId + " já foi concluída!");
            return resposta;
        }

        equipe.getTarefasConcluidas().add(subTarefaId);
        equipe.adicionarPontos(1);
        equipe.setUltimaAtualizacao(System.currentTimeMillis());

        resposta.put("sucesso", true);
        resposta.put("mensagem", "Etapa " + etapaId + " da tarefa " + tarefaId + " concluída! (+1 ponto)");
        resposta.put("equipeId", equipe.getId());
        resposta.put("pontuacaoTotal", equipe.getPontuacao());
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

    public Map<String, Object> iniciarCronometro(int minutos) {
        this.tempoTermino = System.currentTimeMillis() + (minutos * 60000L);
        this.cronometroAtivo = true;

        Map<String, Object> res = new HashMap<>();
        res.put("sucesso", true);
        res.put("mensagem", "Cronômetro iniciado para " + minutos + " minutos.");
        res.put("tempoTermino", this.tempoTermino);
        return res;
    }

    public Map<String, Object> pararCronometro() {
        this.cronometroAtivo = false;
        this.tempoTermino = 0;

        Map<String, Object> res = new HashMap<>();
        res.put("sucesso", true);
        res.put("mensagem", "Cronômetro parado e resetado.");
        return res;
    }

    public Map<String, Object> statusCronometro() {
        long restanteSegundos = 0;
        if (this.cronometroAtivo) {
            restanteSegundos = (this.tempoTermino - System.currentTimeMillis()) / 1000;
            if (restanteSegundos <= 0) {
                restanteSegundos = 0;
                this.cronometroAtivo = false;
            }
        }

        Map<String, Object> res = new HashMap<>();
        res.put("ativo", this.cronometroAtivo);
        res.put("tempoRestanteSegundos", restanteSegundos);
        return res;
    }
}