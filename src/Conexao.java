import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexao {
    private static final String CONFIG_PATH = "config/database.properties";

    private Conexao() {}

    public static Connection conectar() throws SQLException {
        Credenciais cred = carregarCredenciais();
        return DriverManager.getConnection(cred.url, cred.usuario, cred.senha);
    }

    private static Credenciais carregarCredenciais() throws SQLException {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            return parseDatabaseUrl(databaseUrl.trim());
        }

        Properties props = carregarArquivoConfig();
        if (props != null) {
            String url = props.getProperty("jdbc.url");
            String usuario = props.getProperty("jdbc.user");
            String senha = props.getProperty("jdbc.password");
            if (url != null && usuario != null && senha != null) {
                return new Credenciais(url.trim(), usuario.trim(), senha);
            }
        }

        throw new SQLException(
            "Configuracao do banco nao encontrada. "
                + "Defina DATABASE_URL ou crie config/database.properties "
                + "(veja config/database.properties.example)."
        );
    }

    private static Properties carregarArquivoConfig() {
        Path config = Path.of(CONFIG_PATH);
        if (!Files.exists(config)) {
            return null;
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(config)) {
            props.load(in);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel ler " + CONFIG_PATH, e);
        }
    }

    /**
     * Converte URL no formato Railway/Heroku:
     * postgresql://usuario:senha@host:porta/banco
     */
    static Credenciais parseDatabaseUrl(String databaseUrl) throws SQLException {
        try {
            URI uri = URI.create(databaseUrl.replace("postgresql://", "http://"));
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String database = uri.getPath() == null ? "" : uri.getPath().replaceFirst("^/", "");
            String userInfo = uri.getUserInfo();

            if (host == null || host.isBlank() || database.isBlank() || userInfo == null) {
                throw new SQLException("DATABASE_URL invalida.");
            }

            String[] partes = userInfo.split(":", 2);
            if (partes.length != 2) {
                throw new SQLException("DATABASE_URL sem usuario e senha.");
            }

            String jdbcUrl =
                "jdbc:postgresql://" + host + ":" + port + "/" + database + "?sslmode=require";
            return new Credenciais(jdbcUrl, partes[0], partes[1]);
        } catch (IllegalArgumentException e) {
            throw new SQLException("DATABASE_URL invalida: " + e.getMessage(), e);
        }
    }

    private record Credenciais(String url, String usuario, String senha) {}
}
