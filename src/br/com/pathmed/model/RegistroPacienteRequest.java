package br.com.pathmed.model;

import java.time.LocalDate;

public class RegistroPacienteRequest {
    private String identificadorRghc;
    private String cpfPaciente;
    private String nomePaciente;
    private LocalDate dataNascimento;
    private String tipoSanguineo;
    private String email;
    private String telefone;
    private String usuario;
    private String senha;

    public RegistroPacienteRequest() {}

    // Getters e Setters
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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
