package br.com.pathmed.model;

public class ProfissionalResumido {
    private Long idProfissional;
    private String nomeProfissional;
    private String especialidade;

    public ProfissionalResumido() {}

    public ProfissionalResumido(Long idProfissional, String nomeProfissional, String especialidade) {
        this.idProfissional = idProfissional;
        this.nomeProfissional = nomeProfissional;
        this.especialidade = especialidade;
    }

    // Getters e Setters
    public Long getIdProfissional() { return idProfissional; }
    public void setIdProfissional(Long idProfissional) { this.idProfissional = idProfissional; }

    public String getNomeProfissional() { return nomeProfissional; }
    public void setNomeProfissional(String nomeProfissional) { this.nomeProfissional = nomeProfissional; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
}