package br.com.pathmed.model;

public class Especialidade {
    private Long idEspecialidade;
    private String descricaoEspecialidade;

    // Construtores
    public Especialidade() {}

    public Especialidade(Long idEspecialidade, String descricaoEspecialidade) {
        this.idEspecialidade = idEspecialidade;
        this.descricaoEspecialidade = descricaoEspecialidade;
    }

    // Getters e Setters
    public Long getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(Long idEspecialidade) { this.idEspecialidade = idEspecialidade; }

    public String getDescricaoEspecialidade() { return descricaoEspecialidade; }
    public void setDescricaoEspecialidade(String descricaoEspecialidade) {
        this.descricaoEspecialidade = descricaoEspecialidade;
    }

    @Override
    public String toString() {
        return "Especialidade{" +
                "idEspecialidade=" + idEspecialidade +
                ", descricaoEspecialidade='" + descricaoEspecialidade + '\'' +
                '}';
    }
}