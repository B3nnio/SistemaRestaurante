import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Connection: representa a conexão com o banco de dados.
//DriverManager: abre a conexão utilizando o MySQL Connector/J.
//SQLException: representa erros relacionados ao banco de dados.

public class Conexao {

    private static final String URL =
            "jdbc:mysql://localhost:3306/restaurante";

    private static final String USUARIO = "root";

    private static final String SENHA =
            System.getenv("MYSQL_PASSWORD");

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}