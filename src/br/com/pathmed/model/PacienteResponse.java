package br.com.pathmed.model;

import java.time.LocalDate;

public class PacienteResponse {
    private Long idPaciente;
    private String identificadorRghc;
    private String cpfPaciente;
    private String nomePaciente;
    private LocalDate dataNascimento;
    private String tipoSanguineo;

    // Construtor a partir da entidade Paciente
    public PacienteResponse(Paciente paciente) {
        this.idPaciente = paciente.getIdPaciente();
        this.identificadorRghc = paciente.getIdentificadorRghc();
        this.cpfPaciente = paciente.getCpfPaciente();
        this.nomePaciente = paciente.getNomePaciente();
        this.dataNascimento = paciente.getDataNascimento();
        this.tipoSanguineo = paciente.getTipoSanguineo();
    }

    // Getters e Setters
    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }

    public String getIdentificadorRghc() { return identificadorRghc; }
    public void setIdentificadorRghc(String identificadorRghc) { this.identificadorRghc = identificadorRghc; }

    public String getCpfPaciente() { return cpfPaciente; }
    public void setCpfPaciente(String cpfPaciente) { this.cpfPaciente = cpfPaciente; }

    public String getNomePaciente() { return nomePaciente; }
    public void setNomePaciente(String nomePaciente) { this.nomePaciente = nomePaciente; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getTipoSanguineo() { return tipoSanguineo; }
    public void setTipoSanguineo(String tipoSanguineo) { this.tipoSanguineo = tipoSanguineo; }
}