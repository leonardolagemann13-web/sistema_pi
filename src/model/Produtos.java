package model;

public class Produtos {
    // Declaração dos atributos da classe
    private int idProduto;
    private String nome;
    private double preco;

    // Getters e Setter

    // Getters
    public int getIdProduto() {
        return idProduto;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    // Setters
    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    // Para exibir os produtos no ComboBox
    @Override
    public String toString(){
        return nome;
    }
}
