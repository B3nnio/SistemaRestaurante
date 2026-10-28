
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CadastroClientes implements OperacoesCliente {

    @Override
    public void cadastrarCliente(Scanner scanner) {

        System.out.println("\n--- CADASTRO DE CLIENTE ---");

        String nome = validarNome(scanner);
        String telefone = validarTelefone(scanner);

        // comando SQL para cadastrar o cliente no banco
        // as ? serão substituidos pelo nome e telefone
        String sql = "INSERT INTO clientes (nome, telefone) VALUES (?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            // Connection representa a conexão com o MySQL
            // Conexao.conectar() linka a conexão com o banco

            // PreparedStatement prepara o comando SQL para enviar os dados ao banco com segurança

            stmt.setString(1, nome);
            stmt.setString(2, telefone);

            // setString coloca os valores no lugar dos ?

            stmt.executeUpdate();
            // executeUpdate executa comandos que alteram os dados do banco, como INSERT, UPDATE e DELETE

            System.out.println("Cliente cadastrado com sucesso!");

        } catch (SQLException e) {

            // SQLException representa erros relacionados ao banco de dados

            System.out.println("Erro ao cadastrar cliente!");
            System.out.println(e.getMessage());

        }
    }

    public static String validarNome(Scanner scanner) {

        System.out.print("Nome do cliente: ");
        String nome = scanner.nextLine().trim();
        // trim tira os espaços desnecessarios no começo e no final do texto

        while (nome.isEmpty() || !nome.matches("[\\p{L} ]+")) {
            // \\p{L} aceita somente letras

            if (nome.isEmpty()) {
                System.out.println("O nome esta vazio");
            } else {
                System.out.println("O nome deve conter apenas letras.");
            }

            System.out.print("Digite o nome do cliente: ");
            nome = scanner.nextLine().trim();
        }

        return nome;
    }

    public static String validarTelefone(Scanner scanner) {

        System.out.print("Telefone do cliente: ");
        String telefone = scanner.nextLine().trim();

        while (true) {

            if (telefone.isEmpty()) {
                System.out.println("O telefone não pode estar vazio.");

            } else if (!telefone.matches("\\d+")) {
                System.out.println("Apenas números são aceitos nessa parte.");

            } else if (telefone.length() < 10 || telefone.length() > 11) {
                System.out.println("O telefone deve ter 10 ou 11 dígitos com DDD.");

            } else if (telefone.matches("(\\d)\\1+")) {
                System.out.println("O telefone não pode ter todos os números iguais.");

            } else if (telefone.startsWith("0")) {
                System.out.println("O telefone não pode começar com 0.");

            } else {
                break;
            }

            System.out.print("Telefone do cliente: ");
            telefone = scanner.nextLine().trim();
        }

        return telefone;
    }

    @Override
    public void listarClientes() {

        System.out.println("\n--- CLIENTES CADASTRADOS ---");

        String sql = "SELECT * FROM clientes";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            // executeQuery executa comandos de consulta como SELECT

            // ResultSet guarda o resultado da consulta feita no banco de dados

            boolean encontrouClientes = false;

            while (resultado.next()) {

                // next vai passando para o proximo registro até não ter mais registros

                encontrouClientes = true;

                int id = resultado.getInt("id");
                String nome = resultado.getString("nome");
                String telefone = resultado.getString("telefone");

                // getString pega o valor de uma coluna do banco e transforma em uma String

                Cliente cliente = new Cliente(nome, telefone);

                // cria um objeto Cliente com os dados que foram encontrados no banco

                System.out.println("----------------------");
                System.out.println("ID: " + id);
                cliente.exibirCliente();
            }

            if (!encontrouClientes) {
                System.out.println("Nenhum cliente cadastrado.");
            }

        } catch (SQLException e) {

            System.out.println("Erro ao listar clientes!");
            System.out.println(e.getMessage());
        }
    }

    public void alterarCliente(Scanner scanner) {

        System.out.println("\n--- ALTERAR CLIENTE ---");

        listarClientes();

        int id;

        while (true) {
            System.out.print("Digite o ID do cliente que deseja alterar: ");

            try {
                id = Integer.parseInt(scanner.nextLine().trim());

                if (id <= 0) {
                    System.out.println("O ID deve ser maior que zero.");
                } else {
                    break;
                }

            } catch (NumberFormatException e) {
                System.out.println("Digite um ID numérico válido.");
            }
        }

        // SELECT verifica se o cliente existe antes de alterar
        String sqlBusca = "SELECT * FROM clientes WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmtBusca = conexao.prepareStatement(sqlBusca)) {

            stmtBusca.setInt(1, id);

            try (ResultSet resultado = stmtBusca.executeQuery()) {

                if (!resultado.next()) {
                    System.out.println("Cliente não encontrado.");
                    return;
                }

                String nomeAtual = resultado.getString("nome");
                String telefoneAtual = resultado.getString("telefone");

                System.out.println("Cliente encontrado!");
                System.out.println("Nome: " + nomeAtual);
                System.out.println("Telefone: " + telefoneAtual);

                System.out.println("\n1 - Alterar nome");
                System.out.println("2 - Alterar telefone");
                System.out.println("3 - Alterar nome e telefone");
                System.out.println("4 - Cancelar");

                int opcao;

                while (true) {
                    System.out.print("Escolha uma opção: ");

                    try {
                        opcao = Integer.parseInt(scanner.nextLine().trim());

                        if (opcao >= 1 && opcao <= 4) {
                            break;
                        }

                        System.out.println("Opção inválida.");

                    } catch (NumberFormatException e) {
                        System.out.println("Digite uma opção numérica válida.");
                    }
                }

                if (opcao == 4) {
                    System.out.println("Alteração cancelada.");
                    return;
                }

                String novoNome = nomeAtual;
                String novoTelefone = telefoneAtual;

                if (opcao == 1 || opcao == 3) {
                    novoNome = validarNome(scanner);
                }

                if (opcao == 2 || opcao == 3) {
                    novoTelefone = validarTelefone(scanner);
                }

                // UPDATE modifica os dados do cliente escolhido pelo ID
                String sqlUpdate = "UPDATE clientes SET nome = ?, telefone = ? WHERE id = ?";

                try (PreparedStatement stmtUpdate = conexao.prepareStatement(sqlUpdate)) {

                    stmtUpdate.setString(1, novoNome);
                    stmtUpdate.setString(2, novoTelefone);
                    stmtUpdate.setInt(3, id);

                    int linhasAlteradas = stmtUpdate.executeUpdate();

                    if (linhasAlteradas > 0) {
                        System.out.println("Cliente alterado com sucesso!");
                    } else {
                        System.out.println("Nenhum cliente foi alterado.");
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao alterar cliente!");
            System.out.println(e.getMessage());
        }
    }

    public void excluirCliente(Scanner scanner) {

        System.out.println("\n--- EXCLUIR CLIENTE ---");

        listarClientes();

        int id;

        while (true) {
            System.out.print("Digite o ID do cliente que deseja excluir: ");

            try {
                id = Integer.parseInt(scanner.nextLine().trim());

                if (id <= 0) {
                    System.out.println("O ID deve ser maior que zero.");
                } else {
                    break;
                }

            } catch (NumberFormatException e) {
                System.out.println("Digite um ID numérico válido.");
            }
        }

        // SELECT verifica se o cliente existe antes de excluir
        String sqlBusca = "SELECT * FROM clientes WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmtBusca = conexao.prepareStatement(sqlBusca)) {

            stmtBusca.setInt(1, id);

            try (ResultSet resultado = stmtBusca.executeQuery()) {

                if (!resultado.next()) {
                    System.out.println("Cliente não encontrado.");
                    return;
                }

                String nome = resultado.getString("nome");
                String telefone = resultado.getString("telefone");

                System.out.println("\nCliente encontrado:");
                System.out.println("ID: " + id);
                System.out.println("Nome: " + nome);
                System.out.println("Telefone: " + telefone);
            }

            String confirmacao;

            while (true) {
                System.out.print("Tem certeza que deseja excluir este cliente? (S/N): ");
                confirmacao = scanner.nextLine().trim();

                if (confirmacao.equalsIgnoreCase("S") ||
                        confirmacao.equalsIgnoreCase("N")) {
                    break;
                }

                System.out.println("Digite S para confirmar ou N para cancelar.");
            }

            if (confirmacao.equalsIgnoreCase("N")) {
                System.out.println("Exclusão cancelada.");
                return;
            }

            // DELETE remove o cliente do banco de dados pelo ID
            String sqlDelete = "DELETE FROM clientes WHERE id = ?";

            try (PreparedStatement stmtDelete = conexao.prepareStatement(sqlDelete)) {

                stmtDelete.setInt(1, id);

                int linhasExcluidas = stmtDelete.executeUpdate();

                if (linhasExcluidas > 0) {
                    System.out.println("Cliente excluído com sucesso!");
                } else {
                    System.out.println("Nenhum cliente foi excluído.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Não é possível excluir este cliente porque ele possui pedidos registrados.");
            System.out.println(e.getMessage());
        }
    }
}
