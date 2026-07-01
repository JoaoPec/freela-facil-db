import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Executa os scripts SQL no banco configurado em config/database.properties.
 * Uso: java -cp "out;lib/postgresql-42.7.4.jar" SetupDatabase
 */
public class SetupDatabase {
    public static void main(String[] args) throws Exception {
        String ddl = Files.readString(Path.of("database/01_ddl.sql"));
        String dml = Files.readString(Path.of("database/02_dml.sql"));

        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement()) {
            System.out.println("Conectado ao banco. Executando DDL...");
            st.execute(ddl);
            System.out.println("DDL concluido. Executando DML...");
            st.execute(dml);
            System.out.println("DML concluido. Banco pronto para uso.");
        }
    }
}
