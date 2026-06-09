import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PagamentoDAO {
    public void inserir(int contratoId, double valor) {
        String sql = "INSERT INTO pagamentos (contrato_id, valor, data_pagamento, status) VALUES (?, ?, CURRENT_DATE, 'PAGO')";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contratoId);
            stmt.setDouble(2, valor);
            stmt.executeUpdate();

            System.out.println("Pagamento inserido com sucesso.");
        } catch (SQLException e) {
            System.out.println("Erro ao inserir pagamento: " + e.getMessage());
        }
    }

    public void mostrarTotalPago(int contratoId) {
        String sql = "SELECT fn_total_pago_contrato(?) AS total_pago";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contratoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Total pago no contrato " + contratoId + ": R$ " + rs.getDouble("total_pago"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao calcular total pago: " + e.getMessage());
        }
    }
}
