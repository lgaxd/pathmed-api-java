package br.com.pathmed.model;

public class LoginRequest {
    private String usuario;
    private String senha;
    private String tipoUsuario; // "PACIENTE" ou "COLABORADOR"

    public LoginRequest() {}

    public LoginRequest(String usuario, String senha, String tipoUsuario) {
        this.usuario = usuario;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    // Getters e Setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}
