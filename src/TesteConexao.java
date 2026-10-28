import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {

        try (Connection conexao = Conexao.conectar()) {
        //connection é a conexão como banco de dados

            System.out.println("Conexão com MySQL realizada com sucesso!");

        } catch (SQLException e) {

            System.out.println("Erro ao conectar com MySQL!");
            System.out.println(e.getMessage());

        }
    }
}

