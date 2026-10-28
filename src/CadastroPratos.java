
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CadastroPratos implements OperacoesPrato {

    @Override
    public void listarPratos() {

        System.out.println("\n--- CARDÁPIO DO RESTAURANTE ---");

        String sql = "SELECT * FROM pratos";
        //esse comando vai ler todos os pratos registrados no my sql

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {
            //resultset faz a consulta e recebe os dados do banco

            boolean encontrouPratos = false;

            while (resultado.next()) {

                encontrouPratos = true;

                int id = resultado.getInt("id");
                String nome = resultado.getString("nome");
                String descricao = resultado.getString("descricao");
                String categoria = resultado.getString("categoria");
                double preco = resultado.getDouble("preco");

                Prato prato = new Prato(
                        nome, descricao, categoria, preco
                );

                System.out.println("----------------------");
                System.out.println("ID: " + id);
                prato.exibirPrato();
            }

            if (!encontrouPratos) {
                System.out.println("Nenhum prato encontrado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao consultar o cardápio!");
            System.out.println(e.getMessage());
        }
    }
}
