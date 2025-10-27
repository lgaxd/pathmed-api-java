package br.com.pathmed.model;

public class Profissional {
    private Long idProfissional;
    private Long idEspecialidade;
    private String cpfProfissional;
    private String emailCorporativoProfissional;
    private String nomeProfissionalSaude;

    public Profissional() {}

    public Profissional(Long idProfissional, Long idEspecialidade, String cpfProfissional,
                        String emailCorporativoProfissional, String nomeProfissionalSaude) {
        this.idProfissional = idProfissional;
        this.idEspecialidade = idEspecialidade;
        this.cpfProfissional = cpfProfissional;
        this.emailCorporativoProfissional = emailCorporativoProfissional;
        this.nomeProfissionalSaude = nomeProfissionalSaude;
    }

    public Long getIdProfissional() { return idProfissional; }
    public void setIdProfissional(Long idProfissional) { this.idProfissional = idProfissional; }

    public Long getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(Long idEspecialidade) { this.idEspecialidade = idEspecialidade; }

    public String getCpfProfissional() { return cpfProfissional; }
    public void setCpfProfissional(String cpfProfissional) { this.cpfProfissional = cpfProfissional; }

    public String getEmailCorporativoProfissional() { return emailCorporativoProfissional; }
    public void setEmailCorporativoProfissional(String emailCorporativoProfissional) {
        this.emailCorporativoProfissional = emailCorporativoProfissional;
    }

    public String getNomeProfissionalSaude() { return nomeProfissionalSaude; }
    public void setNomeProfissionalSaude(String nomeProfissionalSaude) {
        this.nomeProfissionalSaude = nomeProfissionalSaude;
    }

    @Override
    public String toString() {
        return "Profissional{" +
                "idProfissional=" + idProfissional +
                ", idEspecialidade=" + idEspecialidade +
                ", cpfProfissional='" + cpfProfissional + '\'' +
                ", emailCorporativoProfissional='" + emailCorporativoProfissional + '\'' +
                ", nomeProfissionalSaude='" + nomeProfissionalSaude + '\'' +
                '}';
    }
}