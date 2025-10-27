package br.com.pathmed.model;
import java.time.LocalDate;
import java.util.List;

public class DisponibilidadeDia {
    private LocalDate data;
    private List<HorarioDisponivel> horarios;
    private Long idEspecialidade;
    private String nomeEspecialidade;

    public DisponibilidadeDia() {}

    public DisponibilidadeDia(LocalDate data, Long idEspecialidade, String nomeEspecialidade) {
        this.data = data;
        this.idEspecialidade = idEspecialidade;
        this.nomeEspecialidade = nomeEspecialidade;
    }

    // Getters e Setters
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public List<HorarioDisponivel> getHorarios() { return horarios; }
    public void setHorarios(List<HorarioDisponivel> horarios) { this.horarios = horarios; }

    public Long getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(Long idEspecialidade) { this.idEspecialidade = idEspecialidade; }

    public String getNomeEspecialidade() { return nomeEspecialidade; }
    public void setNomeEspecialidade(String nomeEspecialidade) { this.nomeEspecialidade = nomeEspecialidade; }

    public int getTotalHorariosDisponiveis() {
        return (int) horarios.stream()
                .filter(HorarioDisponivel::hasDisponibilidade)
                .count();
    }
}