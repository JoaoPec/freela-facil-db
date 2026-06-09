import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjetoDAO {
    public void inserir(int clienteId, String titulo, String descricao, double orcamento) {
        String sql = "INSERT INTO projetos (cliente_id, titulo, descricao, orcamento) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);
            stmt.setString(2, titulo);
            stmt.setString(3, descricao);
            stmt.setDouble(4, orcamento);
            stmt.executeUpdate();

            System.out.println("Projeto inserido com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir projeto: " + e.getMessage());
        }
    }

    public void listarComCliente() {
        String sql = "SELECT p.id, p.titulo, c.nome AS cliente, p.orcamento, p.status " +
                     "FROM projetos p " +
                     "JOIN clientes c ON c.id = p.cliente_id " +
                     "ORDER BY p.id";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " - " +
                    rs.getString("titulo") + " - Cliente: " +
                    rs.getString("cliente") + " - R$ " +
                    rs.getDouble("orcamento") + " - " +
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar projetos: " + e.getMessage());
        }
    }

    public void atualizarStatus(int id, String status) {
        String sql = "UPDATE projetos SET status = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();

            System.out.println("Status do projeto atualizado.");
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar projeto: " + e.getMessage());
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM projetos WHERE id = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

            System.out.println("Projeto excluido com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao excluir projeto: " + e.getMessage());
        }
    }

    public void listarViewResumo() {
        String sql = "SELECT projeto_id, titulo, cliente, orcamento, status, total_propostas FROM vw_resumo_projetos";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(
                    rs.getInt("projeto_id") + " - " +
                    rs.getString("titulo") + " - " +
                    rs.getString("cliente") + " - propostas: " +
                    rs.getInt("total_propostas")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar view: " + e.getMessage());
        }
    }
}
