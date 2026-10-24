import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RelatorioDAO {
    public void freelancersMaisAtivos(int minimoPropostas) {
        String sql = "SELECT f.nome AS freelancer, f.especialidade, " +
                     "COUNT(pr.id) AS total_propostas, ROUND(AVG(pr.valor), 2) AS valor_medio " +
                     "FROM freelancers f " +
                     "JOIN propostas pr ON pr.freelancer_id = f.id " +
                     "GROUP BY f.id, f.nome, f.especialidade " +
                     "HAVING COUNT(pr.id) >= ? " +
                     "ORDER BY total_propostas DESC, valor_medio DESC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, minimoPropostas);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println(
                        rs.getString("freelancer") + " - " +
                        rs.getString("especialidade") + " - " +
                        rs.getInt("total_propostas") + " propostas - media R$ " +
                        rs.getDouble("valor_medio")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gerar relatorio: " + e.getMessage());
        }
    }

    public void clientesQueMaisInvestem(double valorMinimo) {
        String sql = "SELECT c.nome AS cliente, COUNT(co.id) AS total_contratos, " +
                     "SUM(co.valor_total) AS total_contratado " +
                     "FROM clientes c " +
                     "JOIN projetos p ON p.cliente_id = c.id " +
                     "JOIN propostas pr ON pr.projeto_id = p.id " +
                     "JOIN contratos co ON co.proposta_id = pr.id " +
                     "WHERE co.status <> 'CANCELADO' " +
                     "GROUP BY c.id, c.nome " +
                     "HAVING SUM(co.valor_total) >= ? " +
                     "ORDER BY total_contratado DESC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, valorMinimo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println(
                        rs.getString("cliente") + " - " +
                        rs.getInt("total_contratos") + " contratos - total R$ " +
                        rs.getDouble("total_contratado")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gerar relatorio: " + e.getMessage());
        }
    }

    public void contratosComSaldo() {
        String sql = "SELECT co.id AS contrato_id, p.titulo AS projeto, f.nome AS freelancer, " +
                     "co.valor_total, COALESCE(SUM(pg.valor), 0) AS total_pago, " +
                     "co.valor_total - COALESCE(SUM(pg.valor), 0) AS saldo " +
                     "FROM contratos co " +
                     "JOIN propostas pr ON pr.id = co.proposta_id " +
                     "JOIN projetos p ON p.id = pr.projeto_id " +
                     "JOIN freelancers f ON f.id = pr.freelancer_id " +
                     "LEFT JOIN pagamentos pg ON pg.contrato_id = co.id AND pg.status = 'PAGO' " +
                     "GROUP BY co.id, p.titulo, f.nome, co.valor_total " +
                     "HAVING COALESCE(SUM(pg.valor), 0) < co.valor_total " +
                     "ORDER BY saldo DESC";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(
                    "Contrato " + rs.getInt("contrato_id") + " - " +
                    rs.getString("projeto") + " - " +
                    rs.getString("freelancer") + " - pago R$ " +
                    rs.getDouble("total_pago") + " de R$ " +
                    rs.getDouble("valor_total") + " - saldo R$ " +
                    rs.getDouble("saldo")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gerar relatorio: " + e.getMessage());
        }
    }
}
