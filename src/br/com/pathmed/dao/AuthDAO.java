package br.com.pathmed.dao;

import br.com.pathmed.model.LoginRequest;
import br.com.pathmed.model.LoginResponse;
import br.com.pathmed.model.RegistroPacienteRequest;
import br.com.pathmed.util.DatabaseConnection;
import java.sql.*;

public class AuthDAO {
    private Connection connection;

    public AuthDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void debugRegistroPaciente(RegistroPacienteRequest request) {
        System.out.println("🔍 DEBUG - Dados do registro:");
        System.out.println("  RGHC: " + request.getIdentificadorRghc());
        System.out.println("  CPF: " + request.getCpfPaciente());
        System.out.println("  Nome: " + request.getNomePaciente());
        System.out.println("  Data Nasc: " + request.getDataNascimento());
        System.out.println("  Tipo Sanguíneo: " + request.getTipoSanguineo());
        System.out.println("  Email: " + request.getEmail());
        System.out.println("  Telefone: " + request.getTelefone());
        System.out.println("  Usuário: " + request.getUsuario());
        System.out.println("  Senha: " + request.getSenha());

        // Verifica se já existe
        System.out.println("  CPF existe: " + verificarCpfExistente(request.getCpfPaciente()));
        System.out.println("  RGHC existe: " + verificarRghcExistente(request.getIdentificadorRghc()));
        System.out.println("  Usuário existe: " + verificarUsuarioExistente(request.getUsuario()));
        System.out.println("  Email existe: " + verificarEmailExistente(request.getEmail()));
        System.out.println("  Telefone existe: " + verificarTelefoneExistente(request.getTelefone()));
    }

    /**
     * Realiza login de paciente
     */
    public LoginResponse loginPaciente(LoginRequest loginRequest) {
        String sql = "SELECT lp.ID_LOGIN_PACIENTE, lp.ID_PACIENTE, p.NOME_PACIENTE, lp.USUARIO_LOGIN " +
                "FROM TB_PATHMED_LOGIN_PACIENTE lp " +
                "JOIN TB_PATHMED_PACIENTE p ON lp.ID_PACIENTE = p.ID_PACIENTE " +
                "WHERE lp.USUARIO_LOGIN = ? AND lp.SENHA_LOGIN = ? AND lp.ATIVO = 'S'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loginRequest.getUsuario());
            stmt.setString(2, loginRequest.getSenha());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new LoginResponse(
                            true,
                            "Login realizado com sucesso",
                            rs.getLong("ID_PACIENTE"),
                            rs.getString("NOME_PACIENTE"),
                            "PACIENTE"
                    );
                } else {
                    return new LoginResponse(false, "Usuário ou senha inválidos");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao realizar login do paciente", e);
        }
    }

    /**
     * Realiza login de colaborador
     */
    public LoginResponse loginColaborador(LoginRequest loginRequest) {
        String sql = "SELECT lc.ID_LOGIN_COLABORADOR, lc.ID_COLABORADOR, c.NOME_COLABORADOR, lc.USUARIO_LOGIN " +
                "FROM TB_PATHMED_LOGIN_COLABORADOR lc " +
                "JOIN TB_PATHMED_COLABORADOR c ON lc.ID_COLABORADOR = c.ID_COLABORADOR " +
                "WHERE lc.USUARIO_LOGIN = ? AND lc.SENHA_LOGIN = ? AND lc.ATIVO = 'S'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loginRequest.getUsuario());
            stmt.setString(2, loginRequest.getSenha());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new LoginResponse(
                            true,
                            "Login realizado com sucesso",
                            rs.getLong("ID_COLABORADOR"),
                            rs.getString("NOME_COLABORADOR"),
                            "COLABORADOR"
                    );
                } else {
                    return new LoginResponse(false, "Usuário ou senha inválidos");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao realizar login do colaborador", e);
        }
    }

    /**
     * Verifica se usuário já existe
     */
    public boolean verificarUsuarioExistente(String usuario) {
        String sqlPaciente = "SELECT 1 FROM TB_PATHMED_LOGIN_PACIENTE WHERE USUARIO_LOGIN = ?";
        String sqlColaborador = "SELECT 1 FROM TB_PATHMED_LOGIN_COLABORADOR WHERE USUARIO_LOGIN = ?";

        try (
                PreparedStatement stmtPaciente = connection.prepareStatement(sqlPaciente);
                PreparedStatement stmtColaborador = connection.prepareStatement(sqlColaborador)
        ) {
            stmtPaciente.setString(1, usuario);
            stmtColaborador.setString(1, usuario);

            try (
                    ResultSet rsPaciente = stmtPaciente.executeQuery();
                    ResultSet rsColaborador = stmtColaborador.executeQuery()
            ) {
                return rsPaciente.next() || rsColaborador.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar usuário existente", e);
        }
    }

    /**
     * Verifica se CPF já está cadastrado
     */
    public boolean verificarCpfExistente(String cpf) {
        String sql = "SELECT 1 FROM TB_PATHMED_PACIENTE WHERE CPF_PACIENTE = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar CPF existente", e);
        }
    }

    /**
     * Verifica se RGHC já está cadastrado
     */
    public boolean verificarRghcExistente(String rghc) {
        String sql = "SELECT 1 FROM TB_PATHMED_PACIENTE WHERE IDENTIFICADOR_RGHC = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rghc);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar RGHC existente", e);
        }
    }

    /**
     * Verifica se email já está cadastrado
     */
    public boolean verificarEmailExistente(String email) {
        String sql = "SELECT 1 FROM TB_PATHMED_CONTATO_PACIENTE WHERE EMAIL_PACIENTE = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar email existente", e);
        }
    }

    /**
     * Verifica se telefone já está cadastrado
     */
    public boolean verificarTelefoneExistente(String telefone) {
        String sql = "SELECT 1 FROM TB_PATHMED_CONTATO_PACIENTE WHERE TELEFONE_PACIENTE = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, telefone);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar telefone existente", e);
        }
    }

    /**
     * Valida todos os dados antes do registro
     */
    public String validarDadosRegistro(RegistroPacienteRequest request) {
        if (verificarCpfExistente(request.getCpfPaciente())) {
            return "CPF já cadastrado no sistema";
        }

        if (verificarRghcExistente(request.getIdentificadorRghc())) {
            return "RGHC já cadastrado no sistema";
        }

        if (verificarUsuarioExistente(request.getUsuario())) {
            return "Nome de usuário já está em uso";
        }

        if (verificarEmailExistente(request.getEmail())) {
            return "Email já cadastrado no sistema";
        }

        if (verificarTelefoneExistente(request.getTelefone())) {
            return "Telefone já cadastrado no sistema";
        }

        return null; // Retorna null se todos os dados são válidos
    }

    /**
     * Registra novo paciente com login - VERSÃO REFATORADA
     */
    public boolean registrarPaciente(RegistroPacienteRequest request) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            System.out.println("🔍 Iniciando registro do paciente...");

            // Validação preventiva de dados
            System.out.println("🔍 Validando dados do registro...");
            String erroValidacao = validarDadosRegistro(request);
            if (erroValidacao != null) {
                System.out.println("❌ Validação falhou: " + erroValidacao);
                throw new RuntimeException(erroValidacao);
            }
            System.out.println("✅ Dados validados com sucesso");

            // 1. Primeiro inserir o paciente (deixe o trigger gerar o ID)
            String sqlPaciente = "INSERT INTO TB_PATHMED_PACIENTE (IDENTIFICADOR_RGHC, CPF_PACIENTE, " +
                    "NOME_PACIENTE, DATA_NASCIMENTO, TIPO_SANGUINEO) " +
                    "VALUES (?, ?, ?, ?, ?)";

            System.out.println("🔍 Inserindo paciente na tabela...");

            try (PreparedStatement stmtPaciente = conn.prepareStatement(sqlPaciente)) {
                stmtPaciente.setString(1, request.getIdentificadorRghc());
                stmtPaciente.setString(2, request.getCpfPaciente());
                stmtPaciente.setString(3, request.getNomePaciente());
                stmtPaciente.setDate(4, Date.valueOf(request.getDataNascimento()));
                stmtPaciente.setString(5, request.getTipoSanguineo());

                int rowsPaciente = stmtPaciente.executeUpdate();
                System.out.println("✅ Paciente inserido. Linhas afetadas: " + rowsPaciente);
            }

            // 2. Buscar o ID que foi gerado pelo trigger
            Long idPaciente = obterIdPacientePorCpf(conn, request.getCpfPaciente());

            if (idPaciente == null) {
                System.out.println("❌ Não foi possível obter o ID do paciente inserido");
                conn.rollback();
                return false;
            }

            System.out.println("🔍 ID do paciente obtido: " + idPaciente);

            // 3. Inserir contato do paciente (sem ID - deixe o trigger gerar)
            String sqlContato = "INSERT INTO TB_PATHMED_CONTATO_PACIENTE (ID_PACIENTE, " +
                    "EMAIL_PACIENTE, TELEFONE_PACIENTE) " +
                    "VALUES (?, ?, ?)";

            System.out.println("🔍 Inserindo contato do paciente...");

            try (PreparedStatement stmtContato = conn.prepareStatement(sqlContato)) {
                stmtContato.setLong(1, idPaciente);
                stmtContato.setString(2, request.getEmail());
                stmtContato.setString(3, request.getTelefone());

                int rowsContato = stmtContato.executeUpdate();
                System.out.println("✅ Contato inserido. Linhas afetadas: " + rowsContato);
            } catch (SQLException e) {
                // Tratamento adicional de segurança, mesmo após validação
                if (e.getErrorCode() == 1) {
                    tratarErroUnicidadeContato(conn, e, idPaciente, request.getEmail(), request.getTelefone());
                } else {
                    throw e;
                }
            }

            // 4. Inserir login do paciente (sem ID - deixe o trigger gerar)
            String sqlLogin = "INSERT INTO TB_PATHMED_LOGIN_PACIENTE (ID_PACIENTE, " +
                    "USUARIO_LOGIN, SENHA_LOGIN, DATA_CRIACAO, ATIVO) " +
                    "VALUES (?, ?, ?, CURRENT_TIMESTAMP, 'S')";

            System.out.println("🔍 Inserindo login do paciente...");

            try (PreparedStatement stmtLogin = conn.prepareStatement(sqlLogin)) {
                stmtLogin.setLong(1, idPaciente);
                stmtLogin.setString(2, request.getUsuario());
                stmtLogin.setString(3, request.getSenha());

                int rowsLogin = stmtLogin.executeUpdate();
                System.out.println("✅ Login inserido. Linhas afetadas: " + rowsLogin);
            } catch (SQLException e) {
                if (e.getErrorCode() == 1) {
                    tratarErroUnicidadeLogin(conn, e, idPaciente, request.getUsuario());
                } else {
                    throw e;
                }
            }

            conn.commit();
            System.out.println("🎉 REGISTRO CONCLUÍDO COM SUCESSO!");
            System.out.println("📋 Resumo do registro:");
            System.out.println("   👤 Paciente: " + request.getNomePaciente());
            System.out.println("   🆔 ID: " + idPaciente);
            System.out.println("   📧 Email: " + request.getEmail());
            System.out.println("   📞 Telefone: " + request.getTelefone());
            System.out.println("   👤 Usuário: " + request.getUsuario());
            return true;

        } catch (SQLException e) {
            System.out.println("❌ ERRO NO REGISTRO: " + e.getMessage());
            System.out.println("❌ Código do erro: " + e.getErrorCode());
            System.out.println("❌ Estado SQL: " + e.getSQLState());

            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("🔁 Transação revertida devido ao erro");
                }
            } catch (SQLException ex) {
                System.out.println("❌ Erro ao reverter transação: " + ex.getMessage());
            }
            return false;

        } catch (Exception e) {
            System.out.println("❌ ERRO INESPERADO: " + e.getMessage());
            e.printStackTrace();

            try {
                if (conn != null) {
                    conn.rollback();
                    System.out.println("🔁 Transação revertida devido ao erro inesperado");
                }
            } catch (SQLException ex) {
                System.out.println("❌ Erro ao reverter transação: " + ex.getMessage());
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println("🔒 Conexão fechada");
                } catch (SQLException e) {
                    System.out.println("⚠️  Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Trata erro de unicidade na tabela de contato
     */
    private void tratarErroUnicidadeContato(Connection conn, SQLException e, Long idPaciente, String email, String telefone)
            throws SQLException {
        System.out.println("⚠️  Conflito na tabela de contato, verificando...");

        if (contatoPacienteExiste(conn, idPaciente)) {
            System.out.println("ℹ️  Contato já existe para este paciente, continuando...");
        } else if (telefonePacienteExiste(conn, telefone)) {
            throw new RuntimeException("Telefone " + telefone + " já está em uso por outro paciente");
        } else if (emailPacienteExiste(conn, email)) {
            throw new RuntimeException("Email " + email + " já está em uso por outro paciente");
        } else {
            throw new RuntimeException("Erro inesperado ao inserir contato: " + e.getMessage(), e);
        }
    }

    /**
     * Trata erro de unicidade na tabela de login
     */
    private void tratarErroUnicidadeLogin(Connection conn, SQLException e, Long idPaciente, String usuario)
            throws SQLException {
        System.out.println("⚠️  Conflito na tabela de login, verificando...");

        if (loginPacienteExiste(conn, idPaciente)) {
            System.out.println("ℹ️  Login já existe para este paciente, continuando...");
        } else if (usuarioLoginExiste(conn, usuario)) {
            throw new RuntimeException("Usuário " + usuario + " já está em uso");
        } else {
            throw new RuntimeException("Erro inesperado ao inserir login: " + e.getMessage(), e);
        }
    }

    // Métodos auxiliares para verificar existência
    private boolean contatoPacienteExiste(Connection conn, Long idPaciente) throws SQLException {
        String sql = "SELECT 1 FROM TB_PATHMED_CONTATO_PACIENTE WHERE ID_PACIENTE = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPaciente);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean loginPacienteExiste(Connection conn, Long idPaciente) throws SQLException {
        String sql = "SELECT 1 FROM TB_PATHMED_LOGIN_PACIENTE WHERE ID_PACIENTE = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPaciente);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean telefonePacienteExiste(Connection conn, String telefone) throws SQLException {
        String sql = "SELECT 1 FROM TB_PATHMED_CONTATO_PACIENTE WHERE TELEFONE_PACIENTE = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefone);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean emailPacienteExiste(Connection conn, String email) throws SQLException {
        String sql = "SELECT 1 FROM TB_PATHMED_CONTATO_PACIENTE WHERE EMAIL_PACIENTE = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean usuarioLoginExiste(Connection conn, String usuario) throws SQLException {
        String sqlPaciente = "SELECT 1 FROM TB_PATHMED_LOGIN_PACIENTE WHERE USUARIO_LOGIN = ?";
        String sqlColaborador = "SELECT 1 FROM TB_PATHMED_LOGIN_COLABORADOR WHERE USUARIO_LOGIN = ?";

        try (PreparedStatement stmtPaciente = conn.prepareStatement(sqlPaciente);
             PreparedStatement stmtColaborador = conn.prepareStatement(sqlColaborador)) {

            stmtPaciente.setString(1, usuario);
            stmtColaborador.setString(1, usuario);

            try (ResultSet rsPaciente = stmtPaciente.executeQuery();
                 ResultSet rsColaborador = stmtColaborador.executeQuery()) {

                return rsPaciente.next() || rsColaborador.next();
            }
        }
    }

    // Método auxiliar para obter o ID do paciente pelo CPF
    private Long obterIdPacientePorCpf(Connection conn, String cpf) throws SQLException {
        String sql = "SELECT ID_PACIENTE FROM TB_PATHMED_PACIENTE WHERE CPF_PACIENTE = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("ID_PACIENTE");
                }
            }
        }
        return null;
    }

    /**
     * Método de debug para verificar as sequences
     */
    public void debugSequences() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

            String[] sequences = {
                    "TB_PATHMED_PACIENTE_ID_PACIENT",
                    "TB_PATHMED_CONTATO_PACIENTE_ID",
                    "TB_PATHMED_LOGIN_PACIENTE_ID_SEQ"
            };

            System.out.println("🔍 VERIFICANDO SEQUENCES:");

            for (String sequence : sequences) {
                try {
                    String sql = "SELECT " + sequence + ".NEXTVAL FROM DUAL";
                    try (PreparedStatement stmt = conn.prepareStatement(sql);
                         ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("   ✅ " + sequence + " -> " + rs.getLong(1));
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("   ❌ " + sequence + " -> ERRO: " + e.getMessage());
                }
            }

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}