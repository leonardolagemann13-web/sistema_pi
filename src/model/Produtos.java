package model;

public class Produtos {
    // Declaração dos atributos da classe
    private int idProduto;
    private String nome;
    private double preco;
    private String caracteristicas;

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

    public String getCaracteristicas() {
        return caracteristicas;
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

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    // Para exibir os produtos no ComboBox
    @Override
    public String toString(){
        return nome;
    }
}
