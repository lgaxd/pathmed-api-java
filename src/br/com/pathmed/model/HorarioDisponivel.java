package br.com.pathmed.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioDisponivel {
    private LocalDateTime dataHora;
    private List<ProfissionalResumido> profissionaisDisponiveis;

    public HorarioDisponivel() {
        this.profissionaisDisponiveis = new ArrayList<>();
    }

    public HorarioDisponivel(LocalDateTime dataHora) {
        this();
        this.dataHora = dataHora;
    }

    // Getters e Setters
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public List<ProfissionalResumido> getProfissionaisDisponiveis() { return profissionaisDisponiveis; }
    public void setProfissionaisDisponiveis(List<ProfissionalResumido> profissionaisDisponiveis) {
        this.profissionaisDisponiveis = profissionaisDisponiveis;
    }

    public void addProfissional(ProfissionalResumido profissional) {
        this.profissionaisDisponiveis.add(profissional);
    }

    public boolean hasDisponibilidade() {
        return !profissionaisDisponiveis.isEmpty();
    }
}