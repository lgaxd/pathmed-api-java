package br.com.pathmed.service;

import br.com.pathmed.dao.DisponibilidadeDAO;
import br.com.pathmed.model.DisponibilidadeDia;
import br.com.pathmed.model.HorarioDisponivel;
import java.time.LocalDate;
import java.util.List;

public class DisponibilidadeService {
    private DisponibilidadeDAO disponibilidadeDAO;

    public DisponibilidadeService() {
        this.disponibilidadeDAO = new DisponibilidadeDAO();
    }

    /**
     * Busca disponibilidade completa para um dia e especialidade
     */
    public DisponibilidadeDia buscarDisponibilidadeDia(LocalDate data, Long idEspecialidade) {
        validarParametros(data, idEspecialidade);

        return disponibilidadeDAO.findDisponibilidadePorDia(data, idEspecialidade);
    }

    /**
     * Busca dias com disponibilidade para o calendário
     */
    public List<LocalDate> buscarDiasComDisponibilidade(Long idEspecialidade, int diasNoFuturo) {
        validarParametros(LocalDate.now(), idEspecialidade);

        if (diasNoFuturo <= 0 || diasNoFuturo > 90) {
            throw new IllegalArgumentException("Dias no futuro deve ser entre 1 e 90");
        }

        return disponibilidadeDAO.findDiasComDisponibilidade(idEspecialidade, diasNoFuturo);
    }

    /**
     * Busca disponibilidade para hoje
     */
    public DisponibilidadeDia buscarDisponibilidadeHoje(Long idEspecialidade) {
        return buscarDisponibilidadeDia(LocalDate.now(), idEspecialidade);
    }

    /**
     * Busca disponibilidade para amanhã
     */
    public DisponibilidadeDia buscarDisponibilidadeAmanha(Long idEspecialidade) {
        return buscarDisponibilidadeDia(LocalDate.now().plusDays(1), idEspecialidade);
    }

    /**
     * Valida parâmetros comuns
     */
    private void validarParametros(LocalDate data, Long idEspecialidade) {
        if (data == null) {
            throw new IllegalArgumentException("Data não pode ser nula");
        }

        if (data.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data não pode ser no passado");
        }

        if (idEspecialidade == null || idEspecialidade <= 0) {
            throw new IllegalArgumentException("ID da especialidade é obrigatório e deve ser maior que zero");
        }
    }

    /**
     * Gera relatório resumido da disponibilidade do dia
     */
    public String gerarRelatorioDisponibilidade(DisponibilidadeDia disponibilidade) {
        if (disponibilidade == null || disponibilidade.getHorarios() == null) {
            return "Nenhuma disponibilidade encontrada";
        }

        int totalHorarios = disponibilidade.getHorarios().size();
        int horariosDisponiveis = disponibilidade.getTotalHorariosDisponiveis();
        double percentual = (horariosDisponiveis * 100.0) / totalHorarios;

        return String.format(
                "📅 %s | %s\n" +
                        "📊 %d/%d horários disponíveis (%.1f%%)",
                disponibilidade.getData(),
                disponibilidade.getNomeEspecialidade(),
                horariosDisponiveis,
                totalHorarios,
                percentual
        );
    }
}