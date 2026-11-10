package com.semanadacomputacao.demo.model;

import java.util.HashSet;
import java.util.Set;

public class equipemodelo {
    private String id;
    private String nome;
    private Set<Integer> tarefasConcluidas;
    private int pontuacao;
    private long ultimaAtualizacao;

    public equipemodelo(String id, String nome) {
        this.id = id;
        this.nome = nome;
        this.tarefasConcluidas = new HashSet<>();
        this.pontuacao = 0;
        this.ultimaAtualizacao = 0;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public Set<Integer> getTarefasConcluidas() { return tarefasConcluidas; }
    public int getPontuacao() { return pontuacao; }
    public void setPontuacao(int pontuacao) { this.pontuacao = pontuacao; }
    public void adicionarPontos(int quantidade) { this.pontuacao += quantidade; }
    public long getUltimaAtualizacao() { return ultimaAtualizacao; }
    public void setUltimaAtualizacao(long ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }
}