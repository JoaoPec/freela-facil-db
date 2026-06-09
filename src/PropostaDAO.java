import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PropostaDAO {
    public void inserir(int projetoId, int freelancerId, double valor, int prazoDias, String mensagem) {
        String sql = "INSERT INTO propostas (projeto_id, freelancer_id, valor, prazo_dias, mensagem) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);
            stmt.setInt(2, freelancerId);
            stmt.setDouble(3, valor);
            stmt.setInt(4, prazoDias);
            stmt.setString(5, mensagem);
            stmt.executeUpdate();

            System.out.println("Proposta inserida com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir proposta: " + e.getMessage());
        }
    }

    public void listarComProjetoEFreelancer() {
        String sql = "SELECT pr.id, p.titulo, f.nome AS freelancer, pr.valor, pr.status " +
                     "FROM propostas pr " +
                     "JOIN projetos p ON p.id = pr.projeto_id " +
                     "JOIN freelancers f ON f.id = pr.freelancer_id " +
                     "ORDER BY pr.id";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " - " +
                    rs.getString("titulo") + " - " +
                    rs.getString("freelancer") + " - R$ " +
                    rs.getDouble("valor") + " - " +
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar propostas: " + e.getMessage());
        }
    }

    public void aceitarProposta(int propostaId) {
        String sql = "CALL sp_aceitar_proposta(?, CURRENT_DATE)";

        try (Connection conn = Conexao.conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, propostaId);
            stmt.execute();

            System.out.println("Proposta aceita e contrato criado.");
        } catch (SQLException e) {
            System.out.println("Erro ao aceitar proposta: " + e.getMessage());
        }
    }
}
