package br.com.pathmed.service;
import br.com.pathmed.dao.ProfissionalDAO;
import br.com.pathmed.model.Profissional;
import java.util.List;
import java.util.Optional;

public class ProfissionalService {
    private ProfissionalDAO profissionalDAO;
    private EspecialidadeService especialidadeService;

    public ProfissionalService() {
        this.profissionalDAO = new ProfissionalDAO();
        this.especialidadeService = new EspecialidadeService();
    }

    // GET /profissionais - Listar todos os profissionais
    public List<Profissional> listarTodosProfissionais() {
        return profissionalDAO.findAll();
    }

    // Buscar profissional por ID
    public Optional<Profissional> buscarProfissionalPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do profissional não pode ser nulo");
        }
        return profissionalDAO.findById(id);
    }

    // Buscar profissionais por especialidade
    public List<Profissional> buscarProfissionaisPorEspecialidade(Long especialidadeId) {
        if (especialidadeId == null) {
            throw new IllegalArgumentException("ID da especialidade não pode ser nulo");
        }

        // Verificar se a especialidade existe
        if (especialidadeService.buscarEspecialidadePorId(especialidadeId).isEmpty()) {
            throw new IllegalArgumentException("Especialidade não encontrada com ID: " + especialidadeId);
        }

        return profissionalDAO.findByEspecialidade(especialidadeId);
    }

    // Buscar profissionais por nome
    public List<Profissional> buscarProfissionaisPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }

        return profissionalDAO.findByNome(nome);
    }

    // Buscar profissional por CPF
    public Optional<Profissional> buscarProfissionalPorCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }

        return profissionalDAO.findByCpf(cpf);
    }

    // Método para obter estatísticas dos profissionais
    public String obterEstatisticasProfissionais() {
        List<Profissional> todos = profissionalDAO.findAll();
        long total = todos.size();

        if (total == 0) {
            return "Nenhum profissional cadastrado";
        }

        // Contar profissionais por especialidade (simulação)
        long cardiologia = todos.stream()
                .filter(p -> p.getIdEspecialidade() == 1L)
                .count();
        long dermatologia = todos.stream()
                .filter(p -> p.getIdEspecialidade() == 2L)
                .count();

        return String.format(
                "Total de profissionais: %d\nCardiologia: %d\nDermatologia: %d",
                total, cardiologia, dermatologia
        );
    }
}