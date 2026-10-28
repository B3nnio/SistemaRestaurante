public class Pedido {

    private int id;
    private int clienteId;
    private double valorTotal;

    // construtor
    public Pedido(int id, int clienteId, double valorTotal) {
        this.id = id;
        this.clienteId = clienteId;
        this.valorTotal = valorTotal;
    }

    public int getId() {
        return id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void exibirPedido() {
        System.out.println("ID do pedido: " + id);
        System.out.println("ID do cliente: " + clienteId);
        System.out.printf("Valor total: R$ %.2f%n", valorTotal);
    }
}

