package br.com.pathmed.model;

import java.time.LocalDate;

public class Paciente {

    private Long idPaciente;
    private String identificadorRghc;
    private String cpfPaciente;
    private String nomePaciente;
    private LocalDate dataNascimento;
    private String tipoSanguineo;

    public Paciente() {}

    public Paciente(Long idPaciente, String identificadorRghc, String cpfPaciente,
                    String nomePaciente, LocalDate dataNascimento, String tipoSanguineo) {
        this.idPaciente = idPaciente;
        this.identificadorRghc = identificadorRghc;
        this.cpfPaciente = cpfPaciente;
        this.nomePaciente = nomePaciente;
        this.dataNascimento = dataNascimento;
        this.tipoSanguineo = tipoSanguineo;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getIdentificadorRghc() {
        return identificadorRghc;
    }

    public void setIdentificadorRghc(String identificadorRghc) {
        this.identificadorRghc = identificadorRghc;
    }

    public String getCpfPaciente() {
        return cpfPaciente;
    }

    public void setCpfPaciente(String cpfPaciente) {
        this.cpfPaciente = cpfPaciente;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }
}
