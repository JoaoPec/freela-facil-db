import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Teste rapido de conexao JDBC.
 * Uso: java -cp "out;lib/postgresql-42.7.4.jar" TesteConexao
 */
public class TesteConexao {
    public static void main(String[] args) throws Exception {
        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT current_database(), version()")) {
            if (rs.next()) {
                System.out.println("Conexao OK!");
                System.out.println("Banco: " + rs.getString(1));
                System.out.println("Versao: " + rs.getString(2));
            }
        }
    }
}
