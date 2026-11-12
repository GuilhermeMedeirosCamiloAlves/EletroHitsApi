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
                int sub1 = ((tarefaId - 1) * 3) + 1;
                int sub2 = ((tarefaId - 1) * 3) + 2;
                int sub3 = ((tarefaId - 1) * 3) + 3;

                if (equipe.getTarefasConcluidas().contains(sub1) &&
                        equipe.getTarefasConcluidas().contains(sub2) &&
                        equipe.getTarefasConcluidas().contains(sub3)) {
                    resposta.put("sucesso", false);
                    resposta.put("mensagem", "Vocês já resolveram este enigma!");
                    return resposta;
                } else {
                    equipe.getTarefasConcluidas().add(sub1);
                    equipe.getTarefasConcluidas().add(sub2);
                    equipe.getTarefasConcluidas().add(sub3);
                    equipe.adicionarPontos(1);
                    equipe.setUltimaAtualizacao(System.currentTimeMillis());

                    resposta.put("sucesso", true);
                    resposta.put("mensagem", "Enigma concluído com sucesso! (+1 ponto)");
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