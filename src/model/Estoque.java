package model;
import java.time.LocalDate;
public class Estoque {
    // Declaração dos atributos da classe
    private int idEstoque;
    private Produtos produto;
    private int quantidade;
    private LocalDate dataProducao;

    // Getters e Setters

    // Gettters

    public int getIdEstoque() {
        return idEstoque;
    }

    public Produtos getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public LocalDate getDataProducao() {
        return dataProducao;
    }


    // Setters

    public void setIdEstoque(int idEstoque) {
        this.idEstoque = idEstoque;
    }

    public void setProduto(Produtos produto) {
        this.produto = produto;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setDataProducao(LocalDate dataProducao) {
        this.dataProducao = dataProducao;
    }

}
