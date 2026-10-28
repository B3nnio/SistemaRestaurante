import java.util.Scanner;

public class Restaurante {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        OperacoesPrato cadastro = new CadastroPratos();
        CadastroClientes cadastroClientes = new CadastroClientes();
        CadastroPedidos cadastroPedidos = new CadastroPedidos();

        int opcao = 0;

        do {
            System.out.println("\n========================");
            System.out.println("  SISTEMA DE RESTAURANTE");
            System.out.println("========================");
            System.out.println("1 - Listar cardápio");
            System.out.println("2 - Cadastrar cliente");
            System.out.println("3 - Listar clientes");
            System.out.println("4 - Alterar cliente");
            System.out.println("5 - Excluir cliente");
            System.out.println("6 - Fazer pedido");
            System.out.println("7 - Histórico de pedidos");
            System.out.println("8 - Alterar pedido");
            System.out.println("9 - Excluir pedido");
            System.out.println("10 - Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
                // parseInt converte texto em número inteiro

                switch (opcao) {

                    case 1:
                        cadastro.listarPratos();
                        break;

                    case 2:
                        cadastroClientes.cadastrarCliente(scanner);
                        break;

                    case 3:
                        cadastroClientes.listarClientes();
                        break;

                    case 4:
                        cadastroClientes.alterarCliente(scanner);
                        break;

                    case 5:
                        cadastroClientes.excluirCliente(scanner);
                        break;

                    case 6:
                        cadastroPedidos.cadastrarPedido(scanner);
                        break;

                    case 7:
                        cadastroPedidos.listarPedidos();
                        break;

                    case 8:
                        cadastroPedidos.alterarPedido(scanner);
                        break;

                    case 9:
                        cadastroPedidos.excluirPedido(scanner);
                        break;

                    case 10:
                        System.out.println("Sistema encerrado.");
                        break;

                    default:
                        System.out.println("Opção inválida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Digite uma opção numérica válida.");
            }

        } while (opcao != 10);

        scanner.close();
        // Encerra o leitor quando o programa termina
    }
}
