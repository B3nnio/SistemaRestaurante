public class Prato {
    private String nome;
    private String descricao;
    private String categoria;
    private double preco;

    //construtor
    public Prato(String nome, String descricao, String categoria, double preco){
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        //ele serve pra guardar o valor recebido no atributo do objeto objeto 9nome0 vai recebr o valor de string
    }

    public String getNome(){
        return nome;
    }

    public String getDescricao(){
        return descricao;
    }

    public String getCategoria(){
        return categoria;
    }

    public double getPreco(){
        return preco;
    }

    public void exibirPrato(){
        System.out.println("Nome: " + nome);
        System.out.println("Descrição: " + descricao);
        System.out.println("Categoria: " + categoria);
        System.out.printf("Preço: R$ %.2f%n", preco);
        //%2f serve pra mostrrar o numero e pular uma linha no final
    }
}
