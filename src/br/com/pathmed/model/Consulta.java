package br.com.pathmed.model;

import java.time.LocalDateTime;

public class Consulta {
    private Long idConsulta;
    private Long idPaciente;
    private Long idProfissional;
    private Long idStatus;
    private LocalDateTime dataHoraConsulta;

    public Consulta() {}

    public Consulta(Long idConsulta, Long idPaciente, Long idProfissional,
                    Long idStatus, LocalDateTime dataHoraConsulta) {
        this.idConsulta = idConsulta;
        this.idPaciente = idPaciente;
        this.idProfissional = idProfissional;
        this.idStatus = idStatus;
        this.dataHoraConsulta = dataHoraConsulta;
    }

    public Long getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Long idConsulta) { this.idConsulta = idConsulta; }

    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }

    public Long getIdProfissional() { return idProfissional; }
    public void setIdProfissional(Long idProfissional) { this.idProfissional = idProfissional; }

    public Long getIdStatus() { return idStatus; }
    public void setIdStatus(Long idStatus) { this.idStatus = idStatus; }

    public LocalDateTime getDataHoraConsulta() { return dataHoraConsulta; }
    public void setDataHoraConsulta(LocalDateTime dataHoraConsulta) { this.dataHoraConsulta = dataHoraConsulta; }

    @Override
    public String toString() {
        return "Consulta{" +
                "idConsulta=" + idConsulta +
                ", idPaciente=" + idPaciente +
                ", idProfissional=" + idProfissional +
                ", idStatus=" + idStatus +
                ", dataHoraConsulta=" + dataHoraConsulta +
                '}';
    }
}