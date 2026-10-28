import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CadastroPedidos {

    public void cadastrarPedido(Scanner scanner) {

        System.out.println("\n--- NOVO PEDIDO ---");

        CadastroClientes cadastroClientes = new CadastroClientes();
        cadastroClientes.listarClientes();

        int clienteId;

        while (true) {

            System.out.print("Digite o ID do cliente: ");

            try {
                clienteId = Integer.parseInt(scanner.nextLine().trim());

                if (clienteId > 0) {
                    break;
                }

                System.out.println("O ID deve ser maior que zero.");

            } catch (NumberFormatException e) {
                System.out.println("Digite um ID numérico válido.");
            }
        }

        // SELECT verifica se o cliente existe no banco de dados
        String sql = "SELECT nome FROM clientes WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, clienteId);

            try (ResultSet resultado = stmt.executeQuery()) {

                if (resultado.next()) {

                    String nome = resultado.getString("nome");

                    System.out.println("Cliente encontrado: " + nome);
                    System.out.println("Cliente selecionado para o pedido!");

                } else {
                    System.out.println("Cliente não encontrado.");
                    return;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao consultar cliente!");
            System.out.println(e.getMessage());
            return;
        }

        CadastroPratos cadastroPratos = new CadastroPratos();

        BigDecimal valorTotal = BigDecimal.ZERO;
        String continuar = "S";

        // Guarda os itens temporariamente antes de salvar no banco
        List<Integer> pratosIds = new ArrayList<>();
        List<Integer> quantidades = new ArrayList<>();
        List<BigDecimal> precos = new ArrayList<>();

        do {

            cadastroPratos.listarPratos();

            int pratoId;

            while (true) {

                System.out.print("Digite o ID do prato: ");

                try {
                    pratoId = Integer.parseInt(scanner.nextLine().trim());

                    if (pratoId > 0) {
                        break;
                    }

                    System.out.println("O ID deve ser maior que zero.");

                } catch (NumberFormatException e) {
                    System.out.println("Digite um ID numérico válido.");
                }
            }

            // SELECT busca o nome e o preço do prato pelo ID
            String sqlPrato = "SELECT nome, preco FROM pratos WHERE id = ?";

            try (Connection conexao = Conexao.conectar();
                 PreparedStatement stmtPrato = conexao.prepareStatement(sqlPrato)) {

                stmtPrato.setInt(1, pratoId);

                try (ResultSet resultado = stmtPrato.executeQuery()) {

                    if (!resultado.next()) {
                        System.out.println("Prato não encontrado. Tente novamente.");
                        continue;
                    }

                    String nomePrato = resultado.getString("nome");
                    BigDecimal preco = resultado.getBigDecimal("preco");

                    int quantidade;

                    while (true) {

                        System.out.print("Digite a quantidade: ");

                        try {
                            quantidade = Integer.parseInt(scanner.nextLine().trim());

                            if (quantidade > 0) {
                                break;
                            }

                            System.out.println("A quantidade deve ser maior que zero.");

                        } catch (NumberFormatException e) {
                            System.out.println("Digite uma quantidade numérica válida.");
                        }
                    }

                    BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(quantidade));
                    valorTotal = valorTotal.add(subtotal);

                    // Adiciona o prato à lista de itens do pedido
                    pratosIds.add(pratoId);
                    quantidades.add(quantidade);
                    precos.add(preco);

                    System.out.println("\nPrato adicionado: " + nomePrato);
                    System.out.println("Quantidade: " + quantidade);
                    System.out.printf("Subtotal: R$ %.2f%n", subtotal);
                    System.out.printf("Total até agora: R$ %.2f%n", valorTotal);
                }

            } catch (SQLException e) {
                System.out.println("Erro ao consultar prato!");
                System.out.println(e.getMessage());
                return;
            }

            while (true) {

                System.out.print("\nDeseja adicionar outro prato? (S/N): ");
                continuar = scanner.nextLine().trim();

                if (continuar.equalsIgnoreCase("S") ||
                        continuar.equalsIgnoreCase("N")) {
                    break;
                }

                System.out.println("Digite S para continuar ou N para finalizar.");
            }

        } while (continuar.equalsIgnoreCase("S"));

        System.out.println("\n--- RESUMO DO PEDIDO ---");
        System.out.println("ID do cliente: " + clienteId);
        System.out.printf("Valor total: R$ %.2f%n", valorTotal);

        // Impede salvar um pedido sem nenhum prato
        if (pratosIds.isEmpty()) {
            System.out.println("Nenhum prato foi adicionado ao pedido.");
            return;
        }

        salvarPedido(clienteId, valorTotal, pratosIds, quantidades, precos);
    }

    public void salvarPedido(int clienteId, BigDecimal valorTotal,
                             List<Integer> pratosIds,
                             List<Integer> quantidades,
                             List<BigDecimal> precos) {

        String sqlPedido = "INSERT INTO pedidos (cliente_id, valor_total) VALUES (?, ?)";

        String sqlItem = "INSERT INTO itens_pedido (pedido_id, prato_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar()) {

            // Desativa o salvamento automático para usar uma transação
            conexao.setAutoCommit(false);

            try {

                int pedidoId;

                try (PreparedStatement stmtPedido = conexao.prepareStatement(
                        sqlPedido, Statement.RETURN_GENERATED_KEYS)) {

                    stmtPedido.setInt(1, clienteId);
                    stmtPedido.setBigDecimal(2, valorTotal);
                    stmtPedido.executeUpdate();

                    // Recupera o ID gerado automaticamente pelo MySQL
                    try (ResultSet resultado = stmtPedido.getGeneratedKeys()) {

                        if (!resultado.next()) {
                            throw new SQLException("Não foi possível obter o ID do pedido.");
                        }

                        pedidoId = resultado.getInt(1);
                    }
                }

                try (PreparedStatement stmtItem = conexao.prepareStatement(sqlItem)) {

                    for (int i = 0; i < pratosIds.size(); i++) {

                        stmtItem.setInt(1, pedidoId);
                        stmtItem.setInt(2, pratosIds.get(i));
                        stmtItem.setInt(3, quantidades.get(i));
                        stmtItem.setBigDecimal(4, precos.get(i));

                        stmtItem.executeUpdate();
                    }
                }

                // Confirma o salvamento do pedido e de todos os itens
                conexao.commit();

                System.out.println("Pedido salvo com sucesso!");
                System.out.println("ID do pedido: " + pedidoId);

            } catch (SQLException e) {

                // Desfaz as alterações se ocorrer algum erro
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    System.out.println("Erro ao desfazer a transação!");
                    System.out.println(erroRollback.getMessage());
                }

                System.out.println("Erro ao salvar pedido!");
                System.out.println(e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Erro de conexão ao salvar pedido!");
            System.out.println(e.getMessage());
        }
    }

    public void listarPedidos() {

        System.out.println("\n--- HISTÓRICO DE PEDIDOS ---");

        // JOIN relaciona os pedidos aos clientes cadastrados
        String sqlPedidos = "SELECT p.id, p.data_pedido, p.valor_total, " +
                "c.nome AS nome_cliente " +
                "FROM pedidos p " +
                "INNER JOIN clientes c ON p.cliente_id = c.id " +
                "ORDER BY p.id";

        // JOIN relaciona os itens aos pratos do cardápio
        String sqlItens = "SELECT pr.nome, i.quantidade, i.preco_unitario " +
                "FROM itens_pedido i " +
                "INNER JOIN pratos pr ON i.prato_id = pr.id " +
                "WHERE i.pedido_id = ? " +
                "ORDER BY i.id";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmtPedidos = conexao.prepareStatement(sqlPedidos);
             PreparedStatement stmtItens = conexao.prepareStatement(sqlItens);
             ResultSet resultadoPedidos = stmtPedidos.executeQuery()) {

            boolean encontrouPedidos = false;

            while (resultadoPedidos.next()) {

                encontrouPedidos = true;

                int pedidoId = resultadoPedidos.getInt("id");
                String nomeCliente = resultadoPedidos.getString("nome_cliente");
                String dataPedido = resultadoPedidos.getString("data_pedido");
                BigDecimal valorTotal = resultadoPedidos.getBigDecimal("valor_total");

                System.out.println("\n----------------------");
                System.out.println("Pedido: " + pedidoId);
                System.out.println("Cliente: " + nomeCliente);
                System.out.println("Data: " + dataPedido);
                System.out.println("\nItens:");

                stmtItens.setInt(1, pedidoId);

                try (ResultSet resultadoItens = stmtItens.executeQuery()) {

                    while (resultadoItens.next()) {

                        String nomePrato = resultadoItens.getString("nome");
                        int quantidade = resultadoItens.getInt("quantidade");
                        BigDecimal preco = resultadoItens.getBigDecimal("preco_unitario");

                        BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(quantidade));

                        System.out.printf("- %s | %d unidade(s) | R$ %.2f%n",
                                nomePrato, quantidade, subtotal);
                    }
                }

                System.out.printf("\nTotal: R$ %.2f%n", valorTotal);
            }

            if (!encontrouPedidos) {
                System.out.println("Nenhum pedido cadastrado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar pedidos!");
            System.out.println(e.getMessage());
        }
    }


    public void alterarPedido(Scanner scanner) {

        System.out.println("\n--- ALTERAR PEDIDO ---");
        listarPedidos();

        int pedidoId;
        while (true) {
            System.out.print("Digite o ID do pedido: ");
            try {
                pedidoId = Integer.parseInt(scanner.nextLine().trim());
                if (pedidoId > 0) {
                    break;
                }
                System.out.println("O ID deve ser maior que zero.");
            } catch (NumberFormatException e) {
                System.out.println("Digite um ID numérico válido.");
            }
        }

        String sqlPedido = "SELECT id FROM pedidos WHERE id = ?";
        String sqlItens = "SELECT i.id, pr.nome, i.quantidade " +
                "FROM itens_pedido i INNER JOIN pratos pr ON i.prato_id = pr.id " +
                "WHERE i.pedido_id = ? ORDER BY i.id";

        try (Connection conexao = Conexao.conectar()) {

            try (PreparedStatement stmt = conexao.prepareStatement(sqlPedido)) {
                stmt.setInt(1, pedidoId);
                try (ResultSet resultado = stmt.executeQuery()) {
                    if (!resultado.next()) {
                        System.out.println("Pedido não encontrado.");
                        return;
                    }
                }
            }

            System.out.println("\nPedido encontrado!");
            System.out.println("1 - Adicionar prato");
            System.out.println("2 - Alterar quantidade de um prato");
            System.out.println("3 - Remover prato");
            System.out.println("4 - Cancelar alteração");

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

            int pratoId = 0;
            int itemId = 0;
            int quantidade = 0;
            BigDecimal preco = BigDecimal.ZERO;

            if (opcao == 1) {
                CadastroPratos cadastroPratos = new CadastroPratos();
                cadastroPratos.listarPratos();

                while (true) {
                    System.out.print("Digite o ID do prato: ");
                    try {
                        pratoId = Integer.parseInt(scanner.nextLine().trim());
                        if (pratoId > 0) {
                            break;
                        }
                        System.out.println("O ID deve ser maior que zero.");
                    } catch (NumberFormatException e) {
                        System.out.println("Digite um ID numérico válido.");
                    }
                }

                String sqlPrato = "SELECT preco FROM pratos WHERE id = ?";
                try (PreparedStatement stmt = conexao.prepareStatement(sqlPrato)) {
                    stmt.setInt(1, pratoId);
                    try (ResultSet resultado = stmt.executeQuery()) {
                        if (!resultado.next()) {
                            System.out.println("Prato não encontrado.");
                            return;
                        }
                        preco = resultado.getBigDecimal("preco");
                    }
                }
            } else {
                System.out.println("\n--- ITENS DO PEDIDO ---");
                try (PreparedStatement stmt = conexao.prepareStatement(sqlItens)) {
                    stmt.setInt(1, pedidoId);
                    try (ResultSet resultado = stmt.executeQuery()) {
                        while (resultado.next()) {
                            System.out.println("ID do item: " + resultado.getInt("id") +
                                    " | Prato: " + resultado.getString("nome") +
                                    " | Quantidade: " + resultado.getInt("quantidade"));
                        }
                    }
                }

                while (true) {
                    System.out.print("Digite o ID do item: ");
                    try {
                        itemId = Integer.parseInt(scanner.nextLine().trim());
                        if (itemId > 0) {
                            break;
                        }
                        System.out.println("O ID deve ser maior que zero.");
                    } catch (NumberFormatException e) {
                        System.out.println("Digite um ID numérico válido.");
                    }
                }

                String sqlVerificarItem = "SELECT id FROM itens_pedido WHERE id = ? AND pedido_id = ?";
                try (PreparedStatement stmt = conexao.prepareStatement(sqlVerificarItem)) {
                    stmt.setInt(1, itemId);
                    stmt.setInt(2, pedidoId);
                    try (ResultSet resultado = stmt.executeQuery()) {
                        if (!resultado.next()) {
                            System.out.println("Item não encontrado neste pedido.");
                            return;
                        }
                    }
                }

                if (opcao == 3) {
                    String sqlContar = "SELECT COUNT(*) FROM itens_pedido WHERE pedido_id = ?";
                    try (PreparedStatement stmt = conexao.prepareStatement(sqlContar)) {
                        stmt.setInt(1, pedidoId);
                        try (ResultSet resultado = stmt.executeQuery()) {
                            resultado.next();
                            if (resultado.getInt(1) <= 1) {
                                System.out.println("Não é possível remover o último prato do pedido.");
                                return;
                            }
                        }
                    }
                }
            }

            if (opcao == 1 || opcao == 2) {
                while (true) {
                    System.out.print("Digite a quantidade: ");
                    try {
                        quantidade = Integer.parseInt(scanner.nextLine().trim());
                        if (quantidade > 0) {
                            break;
                        }
                        System.out.println("A quantidade deve ser maior que zero.");
                    } catch (NumberFormatException e) {
                        System.out.println("Digite uma quantidade numérica válida.");
                    }
                }
            }

            // Usa uma transação para alterar o item e atualizar o total juntos
            conexao.setAutoCommit(false);
            try {
                if (opcao == 1) {
                    String sqlAdicionar = "INSERT INTO itens_pedido " +
                            "(pedido_id, prato_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement stmt = conexao.prepareStatement(sqlAdicionar)) {
                        stmt.setInt(1, pedidoId);
                        stmt.setInt(2, pratoId);
                        stmt.setInt(3, quantidade);
                        stmt.setBigDecimal(4, preco);
                        stmt.executeUpdate();
                    }
                } else if (opcao == 2) {
                    String sqlAlterar = "UPDATE itens_pedido SET quantidade = ? WHERE id = ? AND pedido_id = ?";
                    try (PreparedStatement stmt = conexao.prepareStatement(sqlAlterar)) {
                        stmt.setInt(1, quantidade);
                        stmt.setInt(2, itemId);
                        stmt.setInt(3, pedidoId);
                        if (stmt.executeUpdate() != 1) {
                            throw new SQLException("Não foi possível alterar o item.");
                        }
                    }
                } else {
                    String sqlRemover = "DELETE FROM itens_pedido WHERE id = ? AND pedido_id = ?";
                    try (PreparedStatement stmt = conexao.prepareStatement(sqlRemover)) {
                        stmt.setInt(1, itemId);
                        stmt.setInt(2, pedidoId);
                        if (stmt.executeUpdate() != 1) {
                            throw new SQLException("Não foi possível remover o item.");
                        }
                    }
                }

                // Recalcula o valor total usando os preços salvos nos itens
                String sqlTotal = "SELECT COALESCE(SUM(quantidade * preco_unitario), 0) " +
                        "FROM itens_pedido WHERE pedido_id = ?";
                BigDecimal novoTotal;
                try (PreparedStatement stmt = conexao.prepareStatement(sqlTotal)) {
                    stmt.setInt(1, pedidoId);
                    try (ResultSet resultado = stmt.executeQuery()) {
                        resultado.next();
                        novoTotal = resultado.getBigDecimal(1);
                    }
                }

                String sqlAtualizarTotal = "UPDATE pedidos SET valor_total = ? WHERE id = ?";
                try (PreparedStatement stmt = conexao.prepareStatement(sqlAtualizarTotal)) {
                    stmt.setBigDecimal(1, novoTotal);
                    stmt.setInt(2, pedidoId);
                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("Não foi possível atualizar o total do pedido.");
                    }
                }

                conexao.commit();
                System.out.println("Pedido alterado com sucesso!");
                System.out.printf("Novo valor total: R$ %.2f%n", novoTotal);

            } catch (SQLException e) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    System.out.println("Erro ao desfazer a transação!");
                    System.out.println(erroRollback.getMessage());
                }
                System.out.println("Erro ao alterar pedido!");
                System.out.println(e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Erro ao consultar ou alterar pedido!");
            System.out.println(e.getMessage());
        }
    }


    public void excluirPedido(Scanner scanner) {

        System.out.println("\n--- EXCLUIR PEDIDO ---");

        listarPedidos();

        int id;

        while (true) {
            System.out.print("Digite o ID do pedido que deseja excluir: ");

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

        // SELECT verifica se o pedido existe antes de excluir
        String sqlBusca = "SELECT p.id, p.valor_total, c.nome AS nome_cliente " +
                "FROM pedidos p INNER JOIN clientes c ON p.cliente_id = c.id " +
                "WHERE p.id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmtBusca = conexao.prepareStatement(sqlBusca)) {

            stmtBusca.setInt(1, id);

            try (ResultSet resultado = stmtBusca.executeQuery()) {

                if (!resultado.next()) {
                    System.out.println("Pedido não encontrado.");
                    return;
                }

                String nomeCliente = resultado.getString("nome_cliente");
                BigDecimal valorTotal = resultado.getBigDecimal("valor_total");

                System.out.println("\nPedido encontrado:");
                System.out.println("ID: " + id);
                System.out.println("Cliente: " + nomeCliente);
                System.out.printf("Valor total: R$ %.2f%n", valorTotal);
            }

            String confirmacao;

            while (true) {
                System.out.print("Tem certeza que deseja excluir este pedido? (S/N): ");
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

            String sqlExcluirItens = "DELETE FROM itens_pedido WHERE pedido_id = ?";
            String sqlExcluirPedido = "DELETE FROM pedidos WHERE id = ?";

            // Usa uma transação para excluir os itens e o pedido juntos
            conexao.setAutoCommit(false);

            try {
                // DELETE remove primeiro os itens relacionados ao pedido
                try (PreparedStatement stmtItens = conexao.prepareStatement(sqlExcluirItens)) {
                    stmtItens.setInt(1, id);
                    stmtItens.executeUpdate();
                }

                // DELETE remove o pedido pelo ID
                try (PreparedStatement stmtPedido = conexao.prepareStatement(sqlExcluirPedido)) {
                    stmtPedido.setInt(1, id);

                    int linhasExcluidas = stmtPedido.executeUpdate();

                    if (linhasExcluidas != 1) {
                        throw new SQLException("Não foi possível excluir o pedido.");
                    }
                }

                conexao.commit();
                System.out.println("Pedido excluído com sucesso!");

            } catch (SQLException e) {
                // Desfaz as exclusões caso ocorra algum erro
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    System.out.println("Erro ao desfazer a transação!");
                    System.out.println(erroRollback.getMessage());
                }

                System.out.println("Erro ao excluir pedido!");
                System.out.println(e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Erro ao consultar ou excluir pedido!");
            System.out.println(e.getMessage());
        }
    }
}
