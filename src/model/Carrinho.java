package model;

public class Carrinho {
    // Declaração dos atributos da classe
    private int idCarrinho;
    private Encomendas encomenda;
    private Produtos produto;
    private int quantidade;

    // Getters e Setter

    // Getters
    public int getIdCarrinho(){
     return idCarrinho;
    }
    public Encomendas getEncomenda(){
        return encomenda;
    }
    public Produtos getProduto() {
        return produto;
    }
    public int getQuantidade() {
        return quantidade;
    }
    // Setters
    public void setIdCarrinho(int idCarrinho){
        this.idCarrinho = idCarrinho;
    }
    public void setEncomenda(Encomendas encomenda) {
        this.encomenda = encomenda;
    }
    public void setProduto(Produtos produto) {
        this.produto = produto;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
